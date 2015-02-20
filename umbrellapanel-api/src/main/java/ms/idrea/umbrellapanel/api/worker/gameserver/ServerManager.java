package ms.idrea.umbrellapanel.api.worker.gameserver;

import java.io.File;

public interface ServerManager {

	public File getGameServerDirectory();

	public GameServer getServer(int id);

	public void addServer(GameServer server);

	public void createServer(GameServer server);

	public void deleteServer(GameServer server);

	public void shutdown();
}
