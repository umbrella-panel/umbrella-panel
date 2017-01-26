package ms.idrea.umbrellapanel.api.gameserver;

import java.util.List;

import com.flowpowered.networking.session.Session;

import ms.idrea.umbrellapanel.api.chief.Worker;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.worker.gameserver.GameServer;

public interface ManagedServer extends GameServer {

	Session getOnlineWorker();

	Worker getWorker();

	String getName();

	void setName(String name);

	List<PanelUser> listUsers(int permission);

	void update();
}
