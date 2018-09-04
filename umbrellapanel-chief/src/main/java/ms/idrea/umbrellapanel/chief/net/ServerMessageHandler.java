package ms.idrea.umbrellapanel.chief.net;

import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;

import ms.idrea.umbrellapanel.api.chief.Chief;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.gameserver.ManagedServer;
import ms.idrea.umbrellapanel.api.gameserver.ManagedServerInstance;
import ms.idrea.umbrellapanel.api.gameserver.MultiInstanceServer;
import ms.idrea.umbrellapanel.api.gameserver.ServerInstance;
import ms.idrea.umbrellapanel.api.gameserver.SingleInstanceServer;
import ms.idrea.umbrellapanel.api.worker.gameserver.GameServer;
import ms.idrea.umbrellapanel.chief.UmbrellaChief;
import ms.idrea.umbrellapanel.net.messages.GameServerStatusMessage;
import ms.idrea.umbrellapanel.net.messages.GameServerStatusMessage.Status;
import ms.idrea.umbrellapanel.net.messages.LogMessage;
import ms.idrea.umbrellapanel.net.messages.UpdatePanelUserMessage;
import ms.idrea.umbrellapanel.net.messages.WorkerMessage;
import ms.idrea.umbrellapanel.net.messages.WorkerMessage.Action;

public class ServerMessageHandler implements MessageHandler<UmbrellaWorker, Message> {

	private Chief chief;

	public ServerMessageHandler() {
		chief = UmbrellaChief.getInstance();
	}

	@Override
	public void handle(UmbrellaWorker worker, Message rawMessage) {
		chief.getLogger().finest("[CLIENT-IN " + worker.getId() + "]: " + rawMessage);
		try {
			if (rawMessage instanceof WorkerMessage) {
				WorkerMessage message = (WorkerMessage) rawMessage;
				if (message.getSharedPassword().equals(chief.getChiefProperties().getSharedPassword())) {
					switch (message.getAction()) {
						case REGISTER:
							chief.getWorkerManager().onRegister(worker);
							break;
						case STARTED:
							chief.getWorkerManager().onStart(worker, message.getId());
							break;
						case STOPPED:
							chief.getWorkerManager().onStop(worker);
							break;
						default:
							throw new UnsupportedOperationException();
					}
					if (message.getAction() == Action.REGISTER || message.getAction() == Action.STARTED) {
						worker.send(new WorkerMessage(message.getAction(), worker.getId(), message.getSharedPassword()));
						for (PanelUser user : chief.getPanelUserDatabase().getAllUsers()) {
							worker.send(new UpdatePanelUserMessage(UpdatePanelUserMessage.Action.UPDATE, user));
						}
						for (ManagedServer server : chief.getServerManager().getAllServers()) {
							// only send the worker the server if he owns it
							if (server.getWorker().getId() == worker.getId()) {
								server.update();
							}
							if (server instanceof MultiInstanceServer) {
								MultiInstanceServer s = (MultiInstanceServer) server;
								for (ServerInstance i : s.getInstances()) {
									((ManagedServerInstance) i).update();
								}
							}
						}
					}
				} else {
					worker.send(new WorkerMessage(Action.REGISTER, -1, ""));
					worker.disconnect();
				}
			} else if (rawMessage instanceof LogMessage) {
				LogMessage message = (LogMessage) rawMessage;
				GameServer server = getServerOrThrow(message.getServerId());
				if (server instanceof ManagedServerInstance) {
					((ManagedServerInstance) server).appendLog(message.getLine());
				} else if (server instanceof MultiInstanceServer) {
					MultiInstanceServer s = (MultiInstanceServer) server;
					s.getInstance(message.getInstanceId()).appendLog(message.getLine());
				} else {
					throw new UnsupportedOperationException();
				}
			} else if (rawMessage instanceof GameServerStatusMessage) {
				GameServerStatusMessage message = (GameServerStatusMessage) rawMessage;
				GameServer server = getServerOrThrow(message.getServerId());
				if (server instanceof SingleInstanceServer) {
					((ManagedServerInstance) server).setRunning(message.getStatus() == Status.RUNNING);
				} else if (server instanceof MultiInstanceServer) {
					MultiInstanceServer s = (MultiInstanceServer) server;
					ManagedServerInstance instance = (ManagedServerInstance) s.getInstance(message.getInstanceId());
					instance.setRunning(message.getStatus() == Status.RUNNING);
				}
			}
		} catch (Throwable e) {
			System.out.println("Error while processing network messsage: \"" + rawMessage.getClass() + "\" (\"" + rawMessage.toString() + "\"), send by \"" + worker.getClass() + "\" (\"" + worker.toString() + "\")");
			e.printStackTrace();
		}
	}

	private GameServer getServerOrThrow(int id) {
		GameServer server = chief.getServerManager().getServer(id);
		if (server == null) {
			throw new NullPointerException("No server found with id " + id);
		}
		return server;
	}
}
