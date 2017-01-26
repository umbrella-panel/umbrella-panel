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

import lombok.ToString;

import ms.idrea.umbrellapanel.api.chief.PanelUserDatabase;
import ms.idrea.umbrellapanel.api.chief.WorkerManager;
import ms.idrea.umbrellapanel.api.gameserver.ManagedServer;
import ms.idrea.umbrellapanel.api.gameserver.MultiInstanceServer;
import ms.idrea.umbrellapanel.api.gameserver.ServerInstance;
import ms.idrea.umbrellapanel.api.gameserver.ServerManager;
import ms.idrea.umbrellapanel.api.gameserver.SingleInstanceServer;
import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.api.worker.gameserver.GameServer;
import ms.idrea.umbrellapanel.net.messages.UpdateGameServerMessage;
import ms.idrea.umbrellapanel.net.messages.UpdateMultiInstanceServerMessage;

@ToString(of = { "servers" })
public class UmbrellaServerManager implements ServerManager {

	private PanelUserDatabase panelUserDatabase;
	private WorkerManager workerManager;
	private ConcurrentMap<Integer, ManagedServer> servers = new ConcurrentHashMap<>();
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
	public List<ManagedServer> getAllServers() {
		return new ArrayList<>(servers.values());
	}

	@Override
	public ManagedServer getServer(int id) {
		return servers.get(id);
	}

	@Override
	// only used for NEW servers.
	public UmbrellaSingleInstanceGameServer createSingleInstanceServer(Address address, String startCommand, int workerId) {
		UmbrellaSingleInstanceGameServer server = new UmbrellaSingleInstanceGameServer(getNextId(), workerId, address, startCommand, workerManager, panelUserDatabase);
		if (server.getOnlineWorker() == null) {
			throw new IllegalStateException("Worker is offline!");
		}
		servers.put(server.getId(), server);
		server.getWorkerOrThrow().send(new UpdateGameServerMessage(UpdateGameServerMessage.Action.CREATE, server.getId(), server.getAddress(), server.getStartCommand()));
		return server;
	}

	@Override
	// only used for NEW servers.
	public UmbrellaMultiInstanceGameServer createMultiInstanceServer(String startCommand, int workerId) {
		UmbrellaMultiInstanceGameServer server = new UmbrellaMultiInstanceGameServer(getNextId(), workerId, startCommand, workerManager, panelUserDatabase, 0);
		if (server.getOnlineWorker() == null) {
			throw new IllegalStateException("Worker is offline!");
		}
		servers.put(server.getId(), server);
		server.getWorkerOrThrow().send(new UpdateMultiInstanceServerMessage(UpdateGameServerMessage.Action.CREATE, server.getId(), server.getStartCommand()));
		return server;
	}

	@Override
	public void deleteServer(ManagedServer server) {
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
			GameServer s = getServer(id);
			writer.write(String.valueOf(s.getId()));
			writer.newLine();
			writer.write(String.valueOf(((ManagedServer) s).getWorker().getId()));
			writer.newLine();
			writer.write(String.valueOf(s.getStartCommand()));
			writer.newLine();
			writer.write(String.valueOf(((ManagedServer) s).getName()));
			writer.newLine();
			if (s instanceof SingleInstanceServer) {
				writer.write(String.valueOf(0));
				writer.newLine();
				SingleInstanceServer server = (SingleInstanceServer) s;
				writer.write(String.valueOf(server.getAddress().getHost()));
				writer.newLine();
				writer.write(String.valueOf(server.getAddress().getPort()));
				writer.newLine();
			} else if (s instanceof MultiInstanceServer) {
				writer.write(String.valueOf(1));
				writer.newLine();
				MultiInstanceServer server = (MultiInstanceServer) s;
				writer.write(String.valueOf(server.getNextInstanceId()));
				writer.newLine();
				writer.write(String.valueOf(server.getInstances().size()));
				writer.newLine();
				for (ServerInstance instance : server.getInstances()) {
					writer.write(String.valueOf(instance.getId()));
					writer.newLine();
					writer.write(String.valueOf(instance.getAddress().getHost()));
					writer.newLine();
					writer.write(String.valueOf(instance.getAddress().getPort()));
					writer.newLine();
				}
			}
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
			ManagedServer server;
			int id = Integer.valueOf(reader.readLine());
			int workerId = Integer.valueOf(reader.readLine());
			String startCommand = reader.readLine();
			String name = reader.readLine();
			int type = Integer.valueOf(reader.readLine());
			if (type == 0) {
				// single instance
				String host = reader.readLine();
				int port = Integer.valueOf(reader.readLine());
				server = new UmbrellaSingleInstanceGameServer(id, workerId, new Address(host, port), startCommand, workerManager, panelUserDatabase);
			} else if (type == 1) {
				// multi instance
				//
				int nextInstanceId = Integer.valueOf(reader.readLine());
				int instanceCount = Integer.valueOf(reader.readLine());
				server = new UmbrellaMultiInstanceGameServer(id, workerId, startCommand, workerManager, panelUserDatabase, nextInstanceId);
				for (int j = 0; j < instanceCount; j++) {
					int instanceId = Integer.valueOf(reader.readLine());
					String host = reader.readLine();
					int port = Integer.valueOf(reader.readLine());
					((MultiInstanceServer) server).addInstance(instanceId, new Address(host, port));
				}
			} else {
				System.out.println("Unsupported type: " + type);
				continue;
			}
			server.setName(name);
			servers.put(id, server);
		}
	}
}
