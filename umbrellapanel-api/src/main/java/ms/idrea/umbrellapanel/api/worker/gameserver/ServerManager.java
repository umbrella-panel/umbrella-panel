package ms.idrea.umbrellapanel.api.worker.gameserver;

import java.io.File;

import ms.idrea.umbrellapanel.api.gameserver.MultiInstanceServer;
import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.api.worker.net.NetworkClient;

public interface ServerManager {

	File getGameServerDirectory();

	GameServer getServer(int id);

	void addServer(GameServer server);

	void createServer(GameServer server);

	void deleteServer(GameServer server);

	void shutdown();

	GameServer createSingleServer(int id, Address address, String startCommand, NetworkClient networkClient);

	MultiInstanceServer createMultiServer(int id, String startCommand, NetworkClient networkClient);
}
