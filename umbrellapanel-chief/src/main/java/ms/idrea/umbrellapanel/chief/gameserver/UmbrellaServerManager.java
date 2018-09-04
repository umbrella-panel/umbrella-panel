package ms.idrea.umbrellapanel.chief.gameserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

	private static final JsonParser PARSER = new JsonParser();
	private final PanelUserDatabase panelUserDatabase;
	private final WorkerManager workerManager;
	private final List<ManagedServer> servers = Collections.synchronizedList(new ArrayList<>());
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
		return Collections.unmodifiableList(servers);
	}

	@Override
	public ManagedServer getServer(int id) {
		return servers.get(id);
	}

	@Override
	// only used for NEW servers.
	public UmbrellaSingleInstanceGameServer createSingleInstanceServer(Address address, String startCommand,
			int workerId) {
		UmbrellaSingleInstanceGameServer server = new UmbrellaSingleInstanceGameServer(getNextId(), workerId, address,
				startCommand, workerManager, panelUserDatabase);
		if (server.getOnlineWorker() == null) {
			throw new IllegalStateException("Worker is offline!");
		}
		servers.add(server);
		server.getWorkerOrThrow().send(new UpdateGameServerMessage(UpdateGameServerMessage.Action.CREATE,
				server.getId(), server.getAddress(), server.getStartCommand()));
		return server;
	}

	@Override
	// only used for NEW servers.
	public UmbrellaMultiInstanceGameServer createMultiInstanceServer(String startCommand, int workerId) {
		UmbrellaMultiInstanceGameServer server = new UmbrellaMultiInstanceGameServer(getNextId(), workerId,
				startCommand, workerManager, panelUserDatabase, 0);
		if (server.getOnlineWorker() == null) {
			throw new IllegalStateException("Worker is offline!");
		}
		servers.add(server);
		server.getWorkerOrThrow().send(new UpdateMultiInstanceServerMessage(UpdateGameServerMessage.Action.CREATE,
				server.getId(), server.getStartCommand()));
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
		JsonObject serverSaveData = new JsonObject();
		serverSaveData.addProperty("nextId", nextId);
		JsonArray serversArray = new JsonArray();
		for (GameServer server : servers) {
			JsonObject obj = new JsonObject();
			obj.addProperty("id", server.getId());
			obj.addProperty("worker", String.valueOf(((ManagedServer) server).getWorker().getId()));
			obj.addProperty("startCommand", server.getStartCommand());
			if (server instanceof SingleInstanceServer) {
				obj.addProperty("type", "SingleInstanceServer");
				SingleInstanceServer s = (SingleInstanceServer) server;
				obj.addProperty("address", s.getAddress().getHost());
				obj.addProperty("port", s.getAddress().getPort());
			} else {
				obj.addProperty("type", "MultiInstanceServer");
				MultiInstanceServer s = (MultiInstanceServer) server;
				obj.addProperty("nextInstanceId", s.getNextInstanceId());
				JsonArray instancesArray = new JsonArray();
				for (ServerInstance instance : s.getInstances()) {
					JsonObject instanceObject = new JsonObject();
					instanceObject.addProperty("id", instance.getId());
					instanceObject.addProperty("address", instance.getAddress().getHost());
					instanceObject.addProperty("port", instance.getAddress().getPort());
					instancesArray.add(instanceObject);
				}
				obj.add("instances", instancesArray);
			}
			serversArray.add(obj);
		}
		serverSaveData.add("servers", serversArray);
		writer.write(serverSaveData.toString());
		writer.flush();
	}

	@Override
	public void load(Reader in) throws IOException {
		BufferedReader reader = new BufferedReader(in);
		String readed = reader.readLine();
		if (readed == null) {
			return;
		}
		JsonObject obj = PARSER.parse(readed).getAsJsonObject();
		this.nextId = obj.get("nextId").getAsInt();
		JsonArray serversArray = obj.get("servers").getAsJsonArray();
		for (JsonElement element : serversArray) {
			ManagedServer server = null;
			JsonObject serverData = (JsonObject) element;
			int id = serverData.get("id").getAsInt();
			int workerId = serverData.get("worker").getAsInt();
			String startCommand = serverData.get("startCommand").getAsString();
			if (serverData.get("type").getAsString().equals("SingleInstanceServer")) {
				Address address = new Address(serverData.get("address").getAsString(),
						serverData.get("port").getAsInt());
				server = new UmbrellaSingleInstanceGameServer(id, workerId, address, startCommand, workerManager,
						panelUserDatabase);
			} else if (serverData.get("type").getAsString().equals("MultiInstanceServer")) {
				int nextInstanceId = serverData.get("nextInstanceId").getAsInt();
				server = new UmbrellaMultiInstanceGameServer(id, workerId, startCommand, workerManager,
						panelUserDatabase, nextInstanceId);
				JsonArray instancesArray = serverData.get("instances").getAsJsonArray();
				for (JsonElement instanceElement : instancesArray) {
					JsonObject instanceObject = (JsonObject) instanceElement;
					int instanceId = instanceObject.get("id").getAsInt();
					Address address = new Address(instanceObject.get("address").getAsString(),
							instanceObject.get("port").getAsInt());
					((MultiInstanceServer) server).addInstance(instanceId, address);
				}
			}
			if (server == null) {
				throw new RuntimeException("Error while loading server: " + element);
			}
			servers.add(server);
		}
	}
}
