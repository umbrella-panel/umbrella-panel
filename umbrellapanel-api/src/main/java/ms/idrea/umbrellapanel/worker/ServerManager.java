package ms.idrea.umbrellapanel.worker;

import ms.idrea.umbrellapanel.worker.GameServer;

public interface ServerManager {

	public GameServer getServer(int id);

	public void addServer(GameServer server);

	public void createServer(GameServer server);
	
	public void deleteServer(GameServer server);
	
	public void shutdown();
}
