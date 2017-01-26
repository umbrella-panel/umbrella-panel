package ms.idrea.umbrellapanel.worker;

import ms.idrea.umbrellapanel.api.worker.LogHandler;
import ms.idrea.umbrellapanel.api.worker.Worker;
import ms.idrea.umbrellapanel.net.messages.LogMessage;

public class UmbrellaLogHandler extends Thread implements LogHandler {

	public static final int SEND_INTERVAL = Integer.getInteger("worker.log.sendInterval", 500);
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
		append(id, id, message);
	}

	@Override
	public synchronized void append(int serverId, int instanceId, String message) {
		worker.getNetworkClient().send(new LogMessage(serverId, instanceId, message));
	}

	public synchronized void append(ServerLog log) {
		append(log.getServerId(), log.getInstanceId(), log.getMessage());
	}

	@Override
	public synchronized void flush() {
	}
}
