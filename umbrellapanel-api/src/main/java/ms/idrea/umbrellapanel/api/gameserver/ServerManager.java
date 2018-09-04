package ms.idrea.umbrellapanel.api.gameserver;

import java.util.List;

import ms.idrea.umbrellapanel.api.core.LoadAndSaveable;
import ms.idrea.umbrellapanel.api.util.Address;

public interface ServerManager extends LoadAndSaveable {

	ManagedServer getServer(int id);

	List<ManagedServer> getAllServers();

	ManagedServer createSingleInstanceServer(Address address, String startCommand, int workerId);

	ManagedServer createMultiInstanceServer(String startCommand, int workerId);

	void deleteServer(ManagedServer server);

	int getNextId();
}
