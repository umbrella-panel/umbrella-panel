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

import lombok.Getter;

import ms.idrea.umbrellapanel.api.chief.WorkerManager;
import ms.idrea.umbrellapanel.chief.net.Worker;

import com.flowpowered.networking.session.Session;

public class UmbrellaWorkerManager implements WorkerManager {

	// contains all workers, they may be offline
	private ConcurrentMap<Integer, OfflineWorker> workers = new ConcurrentHashMap<>();
	// contains all running workers
	private ConcurrentMap<Integer, Worker> runningWorkers = new ConcurrentHashMap<>();
	private List<Worker> workerList = null;
	private int nextId = 0;

	@Override
	public OfflineWorker getWorker(int id) {
		return workers.get(id);
	}

	@Override
	public Worker getRunningWorker(int id) {
		return runningWorkers.get(id);
	}

	@Override
	public List<Worker> getAllWorkers() {
		buildCache();
		return workerList;
	}

	public void buildCache() {
		if (workerList == null) {
			List<Worker> temp = new ArrayList<>();
			for (Integer id : runningWorkers.keySet()) {
				Worker worker = runningWorkers.get(id);
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
		Worker worker = sessionToWorker(session);
		worker.setId(getNextId());
		workers.put(worker.getId(), new OfflineWorker(worker));
		onStart(worker, worker.getId());
	}

	@Override
	public void onStart(Session session, int id) {
		Worker worker = sessionToWorker(session);
		worker.setId(id);
		runningWorkers.put(worker.getId(), worker);
		workerList = null;
	}

	@Override
	public void onStop(Session session) {
		Worker worker = sessionToWorker(session);
		runningWorkers.remove(worker.getId());
		workerList = null;
	}

	private Worker sessionToWorker(Session session) {
		if (session instanceof Worker) {
			return (Worker) session;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Getter
	public class OfflineWorker {

		private int id;

		public OfflineWorker(int id) {
			this.id = id;
		}

		public OfflineWorker(Worker worker) {
			this.id = worker.getId();
		}

		public Worker getWorker() {
			return runningWorkers.get(id);
		}

		public boolean isOnline() {
			return getWorker() != null;
		}
	}

	@Override
	public void save(Writer out) throws IOException {
		BufferedWriter writer = new BufferedWriter(out);
		writer.write(String.valueOf(nextId));
		writer.newLine();
		writer.write(String.valueOf(workers.size()));
		writer.newLine();
		for (int id : workers.keySet()) {
			OfflineWorker worker = getWorker(id);
			writer.write(String.valueOf(worker.getId()));
			writer.newLine();
		}
		writer.flush();
	}

	@Override
	public void load(Reader in) throws IOException {
		BufferedReader reader = new BufferedReader(in);
		nextId = Integer.valueOf(reader.readLine());
		int workerSize = Integer.valueOf(reader.readLine());
		workers = new ConcurrentHashMap<>(workerSize);
		for (int i = 0; i < workerSize; i++) {
			int id = Integer.valueOf(reader.readLine());
			workers.put(id, new OfflineWorker(id));
		}
	}
}
