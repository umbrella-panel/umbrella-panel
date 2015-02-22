package ms.idrea.umbrellapanel.chief.net;

import ms.idrea.umbrellapanel.api.chief.Chief;
import ms.idrea.umbrellapanel.api.chief.gameserver.GameServer;
import ms.idrea.umbrellapanel.api.core.PanelUser;
import ms.idrea.umbrellapanel.chief.UmbrellaChief;
import ms.idrea.umbrellapanel.net.messages.GameServerStatusMessage;
import ms.idrea.umbrellapanel.net.messages.LogMessage;
import ms.idrea.umbrellapanel.net.messages.UpdateGameServerMessage;
import ms.idrea.umbrellapanel.net.messages.UpdatePanelUserMessage;
import ms.idrea.umbrellapanel.net.messages.WorkerMessage;
import ms.idrea.umbrellapanel.net.messages.WorkerMessage.Action;

import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;

public class ServerMessageHandler implements MessageHandler<Worker, Message> {

	private Chief chief;

	public ServerMessageHandler() {
		chief = UmbrellaChief.getInstance();
	}

	@Override
	public void handle(Worker worker, Message rawMessage) {
		// System.out.println("[SERVER-IN " + worker + "]: " + rawMessage);
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
							worker.send(new UpdatePanelUserMessage(ms.idrea.umbrellapanel.net.messages.UpdatePanelUserMessage.Action.UPDATE, user));
						}
						for (GameServer server : chief.getServerManager().getAllServers()) {
							// only send the worker the server if he owns it
							if (server.getWorkerId() == worker.getId()) {
								worker.send(new UpdateGameServerMessage(ms.idrea.umbrellapanel.net.messages.UpdateGameServerMessage.Action.UPDATE, server.getId(), server.getUserId(), server.getAddress(), server.getStartCommand()));
							}
						}
					}
				} else {
					worker.send(new WorkerMessage(Action.REGISTER, -1, ""));
				}
			} else if (rawMessage instanceof LogMessage) {
				LogMessage message = (LogMessage) rawMessage;
				GameServer server = getServerOrThrow(message.getId());
				for (String line : message.getLines()) {
					server.appendLog(line);
				}
			} else if (rawMessage instanceof GameServerStatusMessage) {
				GameServerStatusMessage message = (GameServerStatusMessage) rawMessage;
				GameServer server = getServerOrThrow(message.getId());
				switch (message.getStatus()) {
					case RUNNING:
						server.setRunning(true);
						break;
					case STOPPED:
						server.setRunning(false);
						break;
					default:
						throw new UnsupportedOperationException();
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
