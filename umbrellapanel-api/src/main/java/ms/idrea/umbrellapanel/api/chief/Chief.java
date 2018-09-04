package ms.idrea.umbrellapanel.api.chief;

import java.util.logging.Logger;

import ms.idrea.umbrellapanel.api.chief.conf.ChiefProperties;
import ms.idrea.umbrellapanel.api.chief.net.NetworkServer;
import ms.idrea.umbrellapanel.api.gameserver.ServerManager;

public interface Chief {

	NetworkServer getNetworkServer();

	PanelUserDatabase getPanelUserDatabase();

	ServerManager getServerManager();

	WorkerManager getWorkerManager();

	void start();

	Logger getLogger();

	ChiefProperties getChiefProperties();

	void enableConsole();
}
