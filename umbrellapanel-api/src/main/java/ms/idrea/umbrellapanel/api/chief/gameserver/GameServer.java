package ms.idrea.umbrellapanel.api.chief.gameserver;

import java.util.List;

import com.flowpowered.networking.session.Session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import ms.idrea.umbrellapanel.api.chief.Worker;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;

public interface GameServer extends ms.idrea.umbrellapanel.api.worker.gameserver.GameServer {

	void setRunning(boolean running);

	Session getOnlineWorker();

	Worker getWorker();

	String getName();

	void setName(String name);

	void appendLog(String log);

	List<PanelUser> listUsers(int permission);

	List<ServerLog> getLogBuffer();

	void update();

	@Getter
	@ToString
	@AllArgsConstructor
	public static class ServerLog {

		private final long timestamp;
		private final String message;
	}
}
