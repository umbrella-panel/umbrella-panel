package ms.idrea.umbrellapanel.api.gameserver;

import java.util.List;

import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.api.worker.gameserver.GameServer;

public interface MultiInstanceServer extends GameServer {

	ServerInstance createNewInstance(int id, Address address);

	ServerInstance getInstance(int id);

	List<ServerInstance> getInstances();

	void addInstance(int id, Address address);

	int getNextInstanceId();
}
