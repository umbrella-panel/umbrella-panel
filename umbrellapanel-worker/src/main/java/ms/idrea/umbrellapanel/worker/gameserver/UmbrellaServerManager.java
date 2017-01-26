package ms.idrea.umbrellapanel.worker.gameserver;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import ms.idrea.umbrellapanel.api.gameserver.MultiInstanceServer;
import ms.idrea.umbrellapanel.api.gameserver.SingleInstanceServer;
import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.api.worker.Worker;
import ms.idrea.umbrellapanel.api.worker.gameserver.GameServer;
import ms.idrea.umbrellapanel.api.worker.gameserver.ServerManager;
import ms.idrea.umbrellapanel.api.worker.net.NetworkClient;

@ToString(of = { "servers" })
@RequiredArgsConstructor
public class UmbrellaServerManager implements ServerManager {

	public static final String[] STOP_COMMANDS = { "stop", "end", "exit", "shutdown" };
	private final Worker worker;
	private ConcurrentMap<Integer, GameServer> servers = new ConcurrentHashMap<>();

	public GameServer createSingleServer(int id, Address address, String startCommand, NetworkClient networkClient) {
		return new UmbrellaSingleInstanceGameServer(id, address, startCommand, worker.getLogHandler(), this, networkClient);
	}

	public MultiInstanceServer createMultiServer(int id, String startCommand, NetworkClient networkClient) {
		return new UmbrellaMultiInstanceGameServer(id, startCommand, worker.getLogHandler(), this, networkClient);
	}

	@Override
	public GameServer getServer(int id) {
		return servers.get(id);
	}

	@Override
	public void addServer(GameServer server) {
		if (servers.containsKey(server.getId())) {
			throw new IllegalArgumentException("Id \"" + server.getId() + "\" is already taken");
		}
		servers.put(server.getId(), server);
	}

	@Override
	public void createServer(GameServer server) {
		addServer(server);
	}

	@Override
	public void deleteServer(GameServer server) {
		server.delete();
		servers.remove(server.getId());
	}

	@Override
	public void shutdown() {
		for (GameServer server : servers.values()) {
			if (server instanceof SingleInstanceServer) {
				SingleInstanceServer s = (SingleInstanceServer) server;
				if (s.isRunning()) {
					for (String command : STOP_COMMANDS) {
						s.dispatchCommand(command);
					}
					try {
						((UmbrellaSingleInstanceGameServer) s).joinProcess();
					} catch (Exception e) {
					}
				}
			}
		}
	}

	@Override
	public File getGameServerDirectory() {
		return new File("servers"); // TODO: config
	}
}
