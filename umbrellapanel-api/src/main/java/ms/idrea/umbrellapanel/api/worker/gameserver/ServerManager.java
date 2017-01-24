package ms.idrea.umbrellapanel.api.worker.gameserver;

import java.io.File;

public interface ServerManager {

	File getGameServerDirectory();

	GameServer getServer(int id);

	void addServer(GameServer server);

	void createServer(GameServer server);

	void deleteServer(GameServer server);

	void shutdown();
}
