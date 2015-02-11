package ms.idrea.umbrellapanel.worker.net;

import ms.idrea.umbrellapanel.core.PanelUser;
import ms.idrea.umbrellapanel.core.net.DynamicSession;
import ms.idrea.umbrellapanel.core.net.messages.*;
import ms.idrea.umbrellapanel.core.net.messages.UpdateGameServerMessage.Action;
import ms.idrea.umbrellapanel.worker.GameServer;
import ms.idrea.umbrellapanel.worker.UmbrellaWorker;
import ms.idrea.umbrellapanel.worker.Worker;
import ms.idrea.umbrellapanel.worker.gameserver.UmbrellaGameServer;

import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;

public class ClientMessageHandler implements MessageHandler<DynamicSession, Message> {

	private Worker worker;

	public ClientMessageHandler() {
		worker = UmbrellaWorker.getInstance();
	}

	@Override
	public void handle(DynamicSession session, Message rawMessage) {
		worker.getLogger().finest("[CLIENT-IN " + session + "]: " + rawMessage);
		try {
			if (rawMessage instanceof UpdateGameServerMessage) {
				UpdateGameServerMessage message = (UpdateGameServerMessage) rawMessage;
				PanelUser user = getUserOrThrow(message.getId());
				GameServer oldServer = worker.getServerManager().getServer(message.getId());
				if (oldServer == null) {
					GameServer server = new UmbrellaGameServer(message.getId(), user.getId(), message.getAddress(), message.getStartCommand(), worker.getLogHandler(), worker.getServerManager(), worker.getUserRegistery());
					switch (message.getAction()) {
						case CREATE:
							worker.getServerManager().createServer(server);
							break;
						case UPDATE:
							worker.getServerManager().addServer(server);
							break;
						default:
							throw new UnsupportedOperationException();
					}
				} else if (message.getAction() == Action.UPDATE) {
					if (message.getAddress() != null) {
						oldServer.setAddress(message.getAddress());
					}
					if (message.getStartCommand() != null) {
						oldServer.setStartCommand(message.getStartCommand());
					}
				}
			} else if (rawMessage instanceof ManageGameServerMessage) {
				ManageGameServerMessage message = (ManageGameServerMessage) rawMessage;
				GameServer server = getServerOrThrow(message.getId());
				switch (message.getAction()) {
					case DELETE:
						worker.getServerManager().deleteServer(server);
						break;
					case FORCE_STOP:
						server.forceStop();
						break;
					case START:
						server.start();
						break;
					default:
						throw new UnsupportedOperationException();
				}
			} else if (rawMessage instanceof DispatchCommandMessage) {
				DispatchCommandMessage message = (DispatchCommandMessage) rawMessage;
				GameServer server = getServerOrThrow(message.getId());
				server.dispatchCommand(message.getCommand());
			} else if (rawMessage instanceof UpdatePanelUserMessage) {
				UpdatePanelUserMessage message = (UpdatePanelUserMessage) rawMessage;
				worker.getUserRegistery().update(message.getPanelUser());
			} else if (rawMessage instanceof WorkerMessage) {
				WorkerMessage message = (WorkerMessage) rawMessage;
				switch (message.getAction()) {
					case STARTED:
					case REGISTER:
						if (message.getId() != -1) {
							worker.getWorkerProperties().setWorkerId(message.getId());
						} else {
							throw new RuntimeException("Could not register at web!");
						}
						break;
					default:
						throw new UnsupportedOperationException();
				}
			}
		} catch (Throwable e) {
			worker.getLogger().warning("Error while processing network messsage: \"" + rawMessage.getClass() + "\" (\"" + rawMessage.toString() + "\"), send by \"" + session.getClass() + "\" (\"" + session.toString() + "\")");
			e.printStackTrace();
		}
	}

	private PanelUser getUserOrThrow(int id) {
		PanelUser user = worker.getUserRegistery().getPanelUser(id);
		if (user == null) {
			throw new NullPointerException("No user found with id " + id);
		}
		return user;
	}

	private GameServer getServerOrThrow(int id) {
		GameServer server = worker.getServerManager().getServer(id);
		if (server == null) {
			throw new NullPointerException("No server found with id " + id);
		}
		return server;
	}
}
