package ms.idrea.umbrellapanel.worker;

import ms.idrea.umbrellapanel.core.PanelUser;
import ms.idrea.umbrellapanel.util.Address;

public interface GameServer {

	public int getId();

	public Address getAddress();

	public void setAddress(Address address);

	public int getUserId();

	public PanelUser getPanelUser();

	public void setup();

	public void delete();

	public boolean start();

	public boolean forceStop();

	public boolean dispatchCommand(String str);

	public boolean isRunning();

	public String getStartCommand();

	public void setStartCommand(String startCommand);
}
