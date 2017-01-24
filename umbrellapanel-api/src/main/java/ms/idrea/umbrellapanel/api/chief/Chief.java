package ms.idrea.umbrellapanel.api.chief;

import java.util.logging.Logger;

import ms.idrea.umbrellapanel.api.chief.conf.ChiefProperties;
import ms.idrea.umbrellapanel.api.chief.gameserver.ServerManager;
import ms.idrea.umbrellapanel.api.chief.net.NetworkServer;

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
