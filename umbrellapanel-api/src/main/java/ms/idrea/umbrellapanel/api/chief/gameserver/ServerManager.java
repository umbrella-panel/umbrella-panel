package ms.idrea.umbrellapanel.api.chief.gameserver;

import java.util.List;

import ms.idrea.umbrellapanel.api.core.LoadAndSaveable;
import ms.idrea.umbrellapanel.api.util.Address;

public interface ServerManager extends LoadAndSaveable {

	GameServer getServer(int id);

	List<GameServer> getAllServers();

	GameServer createServer(Address address, String startCommand, int workerId);

	void deleteServer(GameServer server);

	int getNextId();
}
