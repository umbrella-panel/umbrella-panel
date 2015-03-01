package ms.idrea.umbrellapanel.api.chief.gameserver;

import java.util.List;

import ms.idrea.umbrellapanel.api.core.LoadAndSaveable;
import ms.idrea.umbrellapanel.api.util.Address;

public interface ServerManager extends LoadAndSaveable {

	public GameServer getServer(int id);

	public List<GameServer> getAllServers();

	public GameServer createServer(Address address, String startCommand, int workerId);

	public void deleteServer(GameServer server);

	public int getNextId();
}
