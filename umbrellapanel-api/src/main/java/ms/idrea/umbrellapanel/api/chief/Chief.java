package ms.idrea.umbrellapanel.api.chief;

import java.util.logging.Logger;

import ms.idrea.umbrellapanel.api.chief.conf.ChiefProperties;
import ms.idrea.umbrellapanel.api.chief.gameserver.ServerManager;
import ms.idrea.umbrellapanel.api.chief.net.NetworkServer;

public interface Chief {

	public NetworkServer getNetworkServer();

	public PanelUserDatabase getPanelUserDatabase();

	public ServerManager getServerManager();

	public WorkerManager getWorkerManager();

	public void start();

	public Logger getLogger();
	
	public ChiefProperties getChiefProperties();
}
