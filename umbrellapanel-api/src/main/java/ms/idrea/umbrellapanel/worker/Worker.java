package ms.idrea.umbrellapanel.worker;

import ms.idrea.umbrellapanel.worker.net.NetworkClient;

public interface Worker {
	
	public void start();
	
	public void shutdown();
	
	public NetworkClient getNetworkClient();
	
	public LogHandler getLogHandler();
	
	public ServerManager getServerManager();
}
