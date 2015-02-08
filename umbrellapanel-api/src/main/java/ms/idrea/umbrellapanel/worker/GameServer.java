package ms.idrea.umbrellapanel.worker;

import ms.idrea.umbrellapanel.core.PanelUser;
import ms.idrea.umbrellapanel.util.Address;

public interface GameServer {
	
	public int getId();
	
	public Address getAddress();
	
	public PanelUser getPanelUser();
	
	/**
	 * Creates the unix user and dirs for this server
	 */
	public void setup();
	
	public boolean start();
	
	public boolean forceStop();
	
	public boolean dispatchCommand(String str);
	
	public boolean isRunning();
	
	public String getStartCommand();
}
