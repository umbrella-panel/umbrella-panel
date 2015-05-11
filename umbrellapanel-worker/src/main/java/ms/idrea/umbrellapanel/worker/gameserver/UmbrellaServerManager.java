package ms.idrea.umbrellapanel.worker.gameserver;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ms.idrea.umbrellapanel.api.worker.gameserver.GameServer;
import ms.idrea.umbrellapanel.api.worker.gameserver.ServerManager;

public class UmbrellaServerManager implements ServerManager {

	public static final String[] STOP_COMMANDS = { "stop", "end", "exit", "shutdown" };

	private ConcurrentMap<Integer, GameServer> servers = new ConcurrentHashMap<>();

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
		server.setup();
	}

	@Override
	public void deleteServer(GameServer server) {
		server.delete();
		servers.remove(server.getId());
	}

	@Override
	public void shutdown() {
		for (int id : servers.keySet()) {
			GameServer gameServer = getServer(id);
			if (gameServer.isRunning()) {
				for (String command : STOP_COMMANDS) {
					gameServer.dispatchCommand(command);
				}
				try {
					gameServer.joinProcess();
				} catch (Exception e) {
				}
			}
		}
	}

	@Override
	public File getGameServerDirectory() {
		return new File("servers"); // TODO: config
	}
}
