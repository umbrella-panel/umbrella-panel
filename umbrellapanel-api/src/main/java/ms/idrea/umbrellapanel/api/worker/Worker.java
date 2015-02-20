package ms.idrea.umbrellapanel.api.worker;

import java.util.logging.Logger;

import ms.idrea.umbrellapanel.api.worker.conf.WorkerProperties;
import ms.idrea.umbrellapanel.api.worker.gameserver.ServerManager;
import ms.idrea.umbrellapanel.api.worker.net.NetworkClient;

public interface Worker {

	public void start();

	public void shutdown();

	public NetworkClient getNetworkClient();

	public LogHandler getLogHandler();

	public ServerManager getServerManager();

	public UserRegistery getUserRegistery();

	public WorkerProperties getWorkerProperties();

	public Logger getLogger();

	public boolean isRunning();
}
