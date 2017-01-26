package ms.idrea.umbrellapanel.worker.net;

import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;

import ms.idrea.umbrellapanel.api.gameserver.MultiInstanceServer;
import ms.idrea.umbrellapanel.api.gameserver.ServerInstance;
import ms.idrea.umbrellapanel.api.gameserver.SingleInstanceServer;
import ms.idrea.umbrellapanel.api.worker.Worker;
import ms.idrea.umbrellapanel.api.worker.gameserver.GameServer;
import ms.idrea.umbrellapanel.net.DynamicSession;
import ms.idrea.umbrellapanel.net.UmbrellaProtocol;
import ms.idrea.umbrellapanel.net.messages.*;
import ms.idrea.umbrellapanel.net.messages.UpdateGameServerMessage.Action;
import ms.idrea.umbrellapanel.worker.UmbrellaWorker;

public class ClientMessageHandler implements MessageHandler<DynamicSession, Message> {

	private Worker worker;

	public ClientMessageHandler() {
		worker = UmbrellaWorker.getInstance();
	}

	@Override
	public void handle(DynamicSession session, Message raw) {
		worker.getLogger().finest("[CLIENT-IN " + session + "]: " + raw);
		try {
			if (raw instanceof ProtocolVersionMessage) {
				ProtocolVersionMessage message = (ProtocolVersionMessage) raw;
				session.setRemoteProtocolVersion(message.getVersion());
				if (session.getRemoteProtocolVersion() != UmbrellaProtocol.PROTOCOL_VERSION) {
					worker.getLogger().warning("Transport protocol versions are not matching. (local: " + UmbrellaProtocol.PROTOCOL_VERSION + " vs remote: " + session.getRemoteProtocolVersion() + ")");
				}
				((UmbrellaWorker) worker).getNetworkClient().onConnectionReady();
			} else if (raw instanceof UpdateMultiInstanceServerMessage) {
				UpdateMultiInstanceServerMessage message = (UpdateMultiInstanceServerMessage) raw;
				GameServer oldServer = worker.getServerManager().getServer(message.getId());
				if (oldServer == null) {
					GameServer server = worker.getServerManager().createMultiServer(message.getId(), message.getStartCommand(), worker.getNetworkClient());
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
					if (message.getStartCommand() != null) {
						oldServer.setStartCommand(message.getStartCommand());
					}
				}
			} else if (raw instanceof UpdateServerInstanceMessage) {
				UpdateServerInstanceMessage message = (UpdateServerInstanceMessage) raw;
				MultiInstanceServer server = getMultiServerOrThrow(message.getServerId());
				ServerInstance instance = server.getInstance(message.getInstanceId());
				if (instance == null) {
					switch (message.getAction()) {
						case CREATE:
							server.createNewInstance(message.getInstanceId(), message.getAddress());
							break;
						case UPDATE:
							server.addInstance(message.getInstanceId(), message.getAddress());
							break;
						default:
							throw new UnsupportedOperationException();
					}
				} else {
					if (message.getAddress() != null) {
						instance.setAddress(message.getAddress());
					}
				}
			} else if (raw instanceof ManageMultiServerInstanceMessage) {
				ManageMultiServerInstanceMessage message = (ManageMultiServerInstanceMessage) raw;
				MultiInstanceServer server = getMultiServerOrThrow(message.getServerId());
				ServerInstance instance = server.getInstance(message.getInstanceId());
				if (message.getAction() == ManageGameServerMessage.Action.DELETE) {
					instance.delete();
				} else {
					switch (message.getAction()) {
						case FORCE_STOP:
							instance.forceStop();
							break;
						case START:
							instance.start();
							break;
						default:
							throw new UnsupportedOperationException();
					}
				}
			} else if (raw instanceof UpdateGameServerMessage) {
				UpdateGameServerMessage message = (UpdateGameServerMessage) raw;
				GameServer oldServer = worker.getServerManager().getServer(message.getId());
				if (oldServer == null) {
					GameServer server = worker.getServerManager().createSingleServer(message.getId(), message.getAddress(), message.getStartCommand(), worker.getNetworkClient());
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
					if (message.getAddress() != null && oldServer instanceof SingleInstanceServer) {
						((SingleInstanceServer) oldServer).setAddress(message.getAddress());
					}
					if (message.getStartCommand() != null) {
						oldServer.setStartCommand(message.getStartCommand());
					}
				}
			} else if (raw instanceof ManageGameServerMessage) {
				ManageGameServerMessage message = (ManageGameServerMessage) raw;
				GameServer server = getServerOrThrow(message.getId());
				if (message.getAction() == ManageGameServerMessage.Action.DELETE) {
					worker.getServerManager().deleteServer(server);
				} else {
					if (server instanceof SingleInstanceServer) {
						switch (message.getAction()) {
							case FORCE_STOP:
								((SingleInstanceServer) server).forceStop();
								break;
							case START:
								((SingleInstanceServer) server).start();
								break;
							default:
								throw new UnsupportedOperationException();
						}
					} else {
						throw new UnsupportedOperationException();
					}
				}
			} else if (raw instanceof DispatchCommandMessage) {
				DispatchCommandMessage message = (DispatchCommandMessage) raw;
				GameServer server = getServerOrThrow(message.getId());
				if (server instanceof SingleInstanceServer) {
					((SingleInstanceServer) server).dispatchCommand(message.getCommand());
				}
				
			} else if  (raw instanceof DispatchMultiServerInstanceCommandMessage) {
				DispatchMultiServerInstanceCommandMessage message = (DispatchMultiServerInstanceCommandMessage) raw;
				MultiInstanceServer server = getMultiServerOrThrow(message.getServerId());
				ServerInstance instance = server.getInstance(message.getInstanceId());
				instance.dispatchCommand(message.getCommand());
			} else if (raw instanceof UpdatePanelUserMessage) {
				UpdatePanelUserMessage message = (UpdatePanelUserMessage) raw;
				switch (message.getAction()) {
					case DELETE:
						worker.getUserRegistery().delete(message.getPanelUser());
						break;
					case UPDATE:
						worker.getUserRegistery().update(message.getPanelUser());
						break;
					default:
						throw new UnsupportedOperationException();
				}
			} else if (raw instanceof WorkerMessage) {
				WorkerMessage message = (WorkerMessage) raw;
				switch (message.getAction()) {
					case STARTED:
					case REGISTER:
						if (message.getId() != -1) {
							worker.getWorkerProperties().setWorkerId(message.getId());
						} else {
							throw new RuntimeException("Could not register at chief!");
						}
						break;
					default:
						throw new UnsupportedOperationException();
				}
			}
		} catch (Throwable e) {
			worker.getLogger().warning("Error while processing network messsage: \"" + raw.getClass() + "\" (\"" + raw.toString() + "\"), send by \"" + session.getClass() + "\" (\"" + session.toString() + "\")");
			e.printStackTrace();
		}
	}

	private MultiInstanceServer getMultiServerOrThrow(int id) {
		GameServer server = worker.getServerManager().getServer(id);
		if (server == null || !(server instanceof MultiInstanceServer)) {
			throw new NullPointerException("No server found with id " + id);
		}
		return (MultiInstanceServer) server;
	}

	private GameServer getServerOrThrow(int id) {
		GameServer server = worker.getServerManager().getServer(id);
		if (server == null) {
			throw new NullPointerException("No server found with id " + id);
		}
		return server;
	}
}
