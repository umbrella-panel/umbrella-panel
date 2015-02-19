package ms.idrea.umbrellapanel.api.chief.gameserver;

import ms.idrea.umbrellapanel.api.core.PanelUser;
import ms.idrea.umbrellapanel.api.util.Address;

public interface ServerManager {

	public GameServer getServer(int id);

	public GameServer createServer(PanelUser user, Address address, String startCommand, int workerId);

	public void deleteServer(GameServer server);

	public int getNextId();
}
