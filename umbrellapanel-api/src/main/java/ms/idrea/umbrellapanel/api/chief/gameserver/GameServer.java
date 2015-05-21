package ms.idrea.umbrellapanel.api.chief.gameserver;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.util.Address;

import com.flowpowered.networking.session.Session;

public interface GameServer {

	public int getId();

	public Address getAddress();

	public void setAddress(Address address);

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

	public String getName();

	public void setName(String name);

	public int getWorkerId();
	
	public Address getWorkerAddress();

	public void appendLog(String log);

	public List<PanelUser> listUsers(int permission);

	public List<ServerLog> getLogBuffer();

	public void update();

	@Getter
	@ToString
	@AllArgsConstructor
	public static class ServerLog {

		private final long timestamp;
		private final String message;
	}
}
