package ms.idrea.umbrellapanel.worker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UmbrellaServerManager implements ServerManager {

	// UmbrellaWeb -> UmbrellaWorker -> This -> (Commands)
	// OnStartup -> UmbrellaWeb -> SERVERS TO MANAGE -> This
	// 
	
	// Messages:
	// ManageGameServer -> Type (Create || Delete || Force-Stop)
	// GameServerCommandMessage
	// GameServerLogMessage
	// 
	// 
	
	private ConcurrentMap<Integer, UmbrellaGameServer> servers = new ConcurrentHashMap<>();
	
	
}
