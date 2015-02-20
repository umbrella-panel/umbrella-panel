package ms.idrea.umbrellapanel.api.chief.gameserver;

import com.flowpowered.networking.session.Session;

import ms.idrea.umbrellapanel.api.core.PanelUser;
import ms.idrea.umbrellapanel.api.util.Address;

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

	public void setRunning(boolean running);
	
	public boolean isRunning();

	public String getStartCommand();

	public void setStartCommand(String startCommand);

	public Session getWorker();
}
