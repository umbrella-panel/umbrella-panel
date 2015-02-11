package ms.idrea.umbrellapanel.worker;

import java.util.logging.Logger;

import ms.idrea.umbrellapanel.worker.conf.WorkerProperties;
import ms.idrea.umbrellapanel.worker.net.NetworkClient;

public interface Worker {
	
	public void start();
	
	public void shutdown();
	
	public NetworkClient getNetworkClient();
	
	public LogHandler getLogHandler();
	
	public ServerManager getServerManager();
	
	public UserRegistery getUserRegistery();
	
	public WorkerProperties getWorkerProperties();
	
	public Logger getLogger();
}
