package ms.idrea.umbrellapanel.worker.net;

import ms.idrea.umbrellapanel.core.PanelUser;
import ms.idrea.umbrellapanel.core.net.DynamicSession;
import ms.idrea.umbrellapanel.core.net.messages.*;
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
		System.out.println("[CLIENT-IN " + session + "]: " + rawMessage);
		try {
			if (rawMessage instanceof CreateGameServerMessage) {
				CreateGameServerMessage message = (CreateGameServerMessage) rawMessage;
				PanelUser user = getUserOrThrow(message.getId());
				GameServer server = new UmbrellaGameServer(message.getId(), user.getId(), message.getAddress(), worker.getLogHandler(), worker.getServerManager(), worker.getUserRegistery());
				// if the servers was already assigned to this worker!
				if (message.isNoSetup()) {
					worker.getServerManager().addServer(server);
				} else {
					worker.getServerManager().createServer(server);
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
			}
		} catch (Throwable e) {
			System.err.println("Error while processing network messsage: \"" + rawMessage.getClass() + "\" (\"" + rawMessage.toString() + "\"), send by \"" + session.getClass() + "\" (\"" + session.toString() + "\")");
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
