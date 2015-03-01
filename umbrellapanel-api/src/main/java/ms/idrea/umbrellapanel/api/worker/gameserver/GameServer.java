package ms.idrea.umbrellapanel.api.worker.gameserver;

import ms.idrea.umbrellapanel.api.util.Address;

public interface GameServer {

	public int getId();

	public Address getAddress();

	public void setAddress(Address address);

	public void setup();

	public void delete();

	public boolean start();

	public boolean forceStop();

	public boolean dispatchCommand(String str);

	public boolean isRunning();

	public String getStartCommand();

	public void setStartCommand(String startCommand);

	public void joinProcess() throws InterruptedException;
}
