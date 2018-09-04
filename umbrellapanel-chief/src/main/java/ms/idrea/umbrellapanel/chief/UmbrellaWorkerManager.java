package ms.idrea.umbrellapanel.chief;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.flowpowered.networking.session.Session;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.Getter;
import ms.idrea.umbrellapanel.api.chief.Worker;
import ms.idrea.umbrellapanel.api.chief.WorkerManager;
import ms.idrea.umbrellapanel.api.chief.net.RunningWorker;
import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.chief.net.UmbrellaWorker;

public class UmbrellaWorkerManager implements WorkerManager {

	private static final JsonParser PARSER = new JsonParser();
	// contains all workers, they may be offline
	private final List<OfflineWorker> workers = Collections.synchronizedList(new ArrayList<OfflineWorker>());
	// contains all running workers
	private final ConcurrentMap<Integer, UmbrellaWorker> runningWorkers = new ConcurrentHashMap<>();
	private List<RunningWorker> workerList = null;
	private int nextId = 0;

	@Override
	public OfflineWorker getWorker(int id) {
		for (OfflineWorker worker : workers) {
			if (worker.getId() == id) {
				return worker;
			}
		}
		return null;
	}

	@Override
	public UmbrellaWorker getRunningWorker(int id) {
		return runningWorkers.get(id);
	}

	@Override
	public List<RunningWorker> getAllWorkers() {
		buildCache();
		return workerList;
	}

	public void buildCache() {
		if (workerList == null) {
			List<RunningWorker> temp = new ArrayList<>();
			for (Integer id : runningWorkers.keySet()) {
				UmbrellaWorker worker = runningWorkers.get(id);
				if (worker.getId() != -1) { // Worker#getId() is safer then #keySet()
					temp.add(worker);
				}
			}
			workerList = Collections.unmodifiableList(temp);
		}
	}

	@Override
	public synchronized int getNextId() {
		return nextId++;
	}

	@Override
	public void onRegister(Session session) {
		UmbrellaWorker worker = sessionToWorker(session);
		worker.setId(getNextId());
		workers.add(new OfflineWorker(worker));
		onStart(worker, worker.getId());
	}

	@Override
	public void onStart(Session session, int id) {
		UmbrellaWorker worker = sessionToWorker(session);
		worker.setId(id);
		runningWorkers.put(worker.getId(), worker);
		workerList = null;
	}

	@Override
	public void onStop(Session session) {
		UmbrellaWorker worker = sessionToWorker(session);
		runningWorkers.remove(worker.getId());
		workerList = null;
	}

	private UmbrellaWorker sessionToWorker(Session session) {
		if (session instanceof UmbrellaWorker) {
			return (UmbrellaWorker) session;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Getter
	public class OfflineWorker implements Worker {

		private int id;

		public OfflineWorker(int id) {
			this.id = id;
		}

		public OfflineWorker(UmbrellaWorker worker) {
			this.id = worker.getId();
		}

		public UmbrellaWorker getOnlineWorker() {
			return runningWorkers.get(id);
		}

		@Override
		public boolean isOnline() {
			return getOnlineWorker() != null;
		}

		@Override
		public Address getAddress() {
			UmbrellaWorker worker = getOnlineWorker();
			if (worker == null) {
				return null;
			} else {
				return new Address(worker.getAddress().getHostString(), worker.getAddress().getPort());
			}
		}
	}

	@Override
	public void save(Writer out) throws IOException {
		BufferedWriter writer = new BufferedWriter(out);
		JsonObject workersSaveData = new JsonObject();
		workersSaveData.addProperty("nextId", nextId);
		JsonArray workersArray = new JsonArray();
		for (OfflineWorker entry : workers) {
			JsonObject obj = new JsonObject();
			obj.addProperty("id", entry.getId());
			workersArray.add(obj);
		}
		workersSaveData.add("workers", workersArray);
		writer.write(workersSaveData.toString());
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
		JsonArray workersArray = obj.get("workers").getAsJsonArray();
		for (JsonElement element : workersArray) {
			JsonObject userData = (JsonObject) element;
			int id = userData.get("id").getAsInt();
			workers.add(new OfflineWorker(id));
		}
	}
}
