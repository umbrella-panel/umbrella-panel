package ms.idrea.umbrellapanel.web.gameserver;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ms.idrea.umbrellapanel.worker.GameServer;
import ms.idrea.umbrellapanel.worker.ServerManager;

public class UmbrellaServerManager implements ServerManager {

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
		// TODO send stop commands to all server!
		for (int id : servers.keySet()) {
			getServer(id).dispatchCommand("stop");
		}
		while (true) {
			boolean exit = true;
			for (int id : servers.keySet()) {
				if (getServer(id).isRunning()) {
					exit = false;
					break;
				}
			}
			if (exit) {
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}
	}

	@Override
	public File getGameServerDirectory() {
		return new File("servers"); // TODO: config
	}
}
