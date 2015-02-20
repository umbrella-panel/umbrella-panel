package ms.idrea.umbrellapanel.worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.idrea.umbrellapanel.api.worker.LogHandler;
import ms.idrea.umbrellapanel.api.worker.Worker;
import ms.idrea.umbrellapanel.net.messages.LogMessage;

public class UmbrellaLogHandler extends Thread implements LogHandler {

	public static final int SEND_INTERVAL = 10 * 1000;
	private Map<Integer, List<String>> logBuffer = new HashMap<>();
	private Worker worker;

	public UmbrellaLogHandler(Worker worker) {
		super("umbrellaLogHandler");
		this.worker = worker;
		super.start();
	}

	@Override
	public void start() {
		throw new UnsupportedOperationException("Cannot start " + getClass().getSimpleName());
	}

	@Override
	public void run() {
		while (worker.isRunning()) {
			synchronized (this) {
				try {
					wait(SEND_INTERVAL);
				} catch (InterruptedException e) {
				}
				flush();
			}
		}
	}

	@Override
	public void shutdown() {
		flush();
		try {
			join();
		} catch (InterruptedException e) {
		}
	}

	@Override
	public void append(int id, String message) {
		append(new ServerLog(id, message));
	}

	@Override
	public synchronized void append(ServerLog log) {
		if (logBuffer.get(log.getId()) == null) {
			logBuffer.put(log.getId(), new ArrayList<String>());
		}
		logBuffer.get(log.getId()).add(log.getMessage());
		worker.getLogger().fine("[" + log.getId() + "]: " + log.getMessage());
	}

	@Override
	public synchronized void flush() {
		for (int id : logBuffer.keySet()) {
			worker.getNetworkClient().send(new LogMessage(id, logBuffer.get(id)));
		}
		logBuffer.clear();
	}
}
