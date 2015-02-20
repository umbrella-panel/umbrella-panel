package ms.idrea.umbrellapanel.chief.gameserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ms.idrea.umbrellapanel.api.chief.PanelUserDatabase;
import ms.idrea.umbrellapanel.api.chief.WorkerManager;
import ms.idrea.umbrellapanel.api.chief.gameserver.GameServer;
import ms.idrea.umbrellapanel.api.chief.gameserver.ServerManager;
import ms.idrea.umbrellapanel.api.core.PanelUser;
import ms.idrea.umbrellapanel.api.util.Address;

public class UmbrellaServerManager implements ServerManager {

	private PanelUserDatabase panelUserDatabase;
	private WorkerManager workerManager;
	private ConcurrentMap<Integer, GameServer> servers = new ConcurrentHashMap<>();
	private int nextId = 0;

	public UmbrellaServerManager(PanelUserDatabase panelUserDatabase, WorkerManager workerManager) {
		this.panelUserDatabase = panelUserDatabase;
		this.workerManager = workerManager;
	}

	@Override
	public synchronized int getNextId() {
		return nextId++;
	}

	@Override
	public List<GameServer> getAllServers() {
		List<GameServer> list = new ArrayList<>();
		for (Integer id : servers.keySet()) {
			list.add(getServer(id));
		}
		return list;
	}

	@Override
	public GameServer getServer(int id) {
		return servers.get(id);
	}

	@Override
	public GameServer createServer(PanelUser user, Address address, String startCommand, int workerId) {
		GameServer server = new UmbrellaGameServer(getNextId(), user.getId(), workerId, address, startCommand, workerManager, panelUserDatabase);
		servers.put(server.getId(), server);
		server.setup();
		return server;
	}

	@Override
	public void deleteServer(GameServer server) {
		server.delete();
		servers.remove(server.getId());
	}
}
