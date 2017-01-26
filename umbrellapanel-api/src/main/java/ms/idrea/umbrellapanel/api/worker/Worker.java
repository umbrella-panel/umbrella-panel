package ms.idrea.umbrellapanel.api.worker;

import java.util.logging.Logger;

import org.apache.ftpserver.ftplet.UserManager;

import ms.idrea.umbrellapanel.api.worker.conf.WorkerProperties;
import ms.idrea.umbrellapanel.api.worker.gameserver.ServerManager;
import ms.idrea.umbrellapanel.api.worker.net.NetworkClient;

public interface Worker {

	void start();

	void shutdown();

	LogHandler getLogHandler();

	ServerManager getServerManager();
	
	NetworkClient getNetworkClient();

	UserRegistery getUserRegistery();

	WorkerProperties getWorkerProperties();

	Logger getLogger();

	boolean isRunning();

	void enableConsole();

	UserManager getFTPUserWrapper();
}
