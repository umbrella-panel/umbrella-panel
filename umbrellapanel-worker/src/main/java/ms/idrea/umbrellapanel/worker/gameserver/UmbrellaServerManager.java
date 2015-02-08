package ms.idrea.umbrellapanel.worker.gameserver;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ms.idrea.umbrellapanel.worker.GameServer;
import ms.idrea.umbrellapanel.worker.ServerManager;

public class UmbrellaServerManager implements ServerManager {

	// UmbrellaWeb -> UmbrellaWorker -> This -> (Commands)
	// OnStartup -> UmbrellaWeb -> SERVERS TO MANAGE -> This
	// 
	// Messages:
	// ManageGameServer -> Type (Create || Delete || Force-Stop)
	// GameServerCommandMessage
	// GameServerLogMessage
	// 
	// 
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
		// TODO
		throw new RuntimeException("TODO: DELETE SERVER");
	}

	@Override
	public void shutdown() {
		for (int id : servers.keySet()) {
			getServer(id).dispatchCommand("stop"); // TODO GameServer#getStopCommand()
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
}
