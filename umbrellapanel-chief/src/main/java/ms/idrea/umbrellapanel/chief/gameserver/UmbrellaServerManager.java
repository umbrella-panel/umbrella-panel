package ms.idrea.umbrellapanel.chief.gameserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ms.idrea.umbrellapanel.api.chief.PanelUserDatabase;
import ms.idrea.umbrellapanel.api.chief.WorkerManager;
import ms.idrea.umbrellapanel.api.chief.gameserver.GameServer;
import ms.idrea.umbrellapanel.api.chief.gameserver.ServerManager;
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
	public GameServer createServer(Address address, String startCommand, int workerId) {
		GameServer server = new UmbrellaGameServer(getNextId(), workerId, address, startCommand, workerManager, panelUserDatabase);
		if (server.getOnlineWorker() == null) {
			throw new IllegalStateException("Worker is offline!");
		}
		servers.put(server.getId(), server);
		server.setup();
		return server;
	}

	@Override
	public void deleteServer(GameServer server) {
		server.delete();
		servers.remove(server.getId());
	}

	@Override
	public void save(Writer out) throws IOException {
		BufferedWriter writer = new BufferedWriter(out);
		writer.write(String.valueOf(nextId));
		writer.newLine();
		writer.write(String.valueOf(servers.size()));
		writer.newLine();
		for (int id : servers.keySet()) {
			GameServer server = getServer(id);
			writer.write(String.valueOf(server.getId()));
			writer.newLine();
			writer.write(String.valueOf(server.getWorker().getId()));
			writer.newLine();
			writer.write(String.valueOf(server.getAddress().getHost()));
			writer.newLine();
			writer.write(String.valueOf(server.getAddress().getPort()));
			writer.newLine();
			writer.write(String.valueOf(server.getStartCommand()));
			writer.newLine();
			writer.write(String.valueOf(server.getName()));
			writer.newLine();
		}
		writer.flush();
	}

	@Override
	public void load(Reader in) throws IOException {
		BufferedReader reader = new BufferedReader(in);
		nextId = Integer.valueOf(reader.readLine());
		int serverSize = Integer.valueOf(reader.readLine());
		servers = new ConcurrentHashMap<>(serverSize);
		for (int i = 0; i < serverSize; i++) {
			int id = Integer.valueOf(reader.readLine());
			int workerId = Integer.valueOf(reader.readLine());
			String host = reader.readLine();
			int port = Integer.valueOf(reader.readLine());
			String startCommand = reader.readLine();
			GameServer server = new UmbrellaGameServer(id, workerId, new Address(host, port), startCommand, workerManager, panelUserDatabase);
			server.setName(reader.readLine());
			servers.put(id, server);
		}
	}
}
