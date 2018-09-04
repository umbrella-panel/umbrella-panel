package ms.idrea.umbrellapanel.api.gameserver;

import java.util.List;

public interface ManagedServerInstance extends ServerInstance {

	void setRunning(boolean running);

	List<ServerLog> getLogBuffer();

	void update();
}
