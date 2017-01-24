package ms.idrea.umbrellapanel.api.worker.gameserver;

import ms.idrea.umbrellapanel.api.util.Address;

public interface GameServer {

	int getId();

	Address getAddress();

	void setAddress(Address address);

	void setup();

	void delete();

	boolean start();

	boolean forceStop();

	boolean dispatchCommand(String str);

	boolean isRunning();

	String getStartCommand();

	void setStartCommand(String startCommand);
}
