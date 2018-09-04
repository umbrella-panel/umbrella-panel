package ms.idrea.umbrellapanel.api.gameserver;

import ms.idrea.umbrellapanel.api.util.Address;

public interface ServerInstance {

	int getId();

	boolean start();

	boolean forceStop();

	boolean dispatchCommand(String str);

	boolean isRunning();

	Address getAddress();

	void setAddress(Address address);

	void delete();

	void appendLog(String log);
}
