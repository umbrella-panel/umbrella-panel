package ms.idrea.umbrellapanel.chief.gameserver;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import ms.idrea.umbrellapanel.api.chief.PanelUserDatabase;
import ms.idrea.umbrellapanel.api.chief.Worker;
import ms.idrea.umbrellapanel.api.chief.WorkerManager;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.gameserver.ManagedServer;
import ms.idrea.umbrellapanel.chief.UmbrellaWorkerManager.OfflineWorker;
import ms.idrea.umbrellapanel.chief.net.UmbrellaWorker;

@ToString(exclude = { "panelUserDatabase", "workerManager" })
abstract class AbstractServer implements ManagedServer {

	@Getter
	final int id;
	@Getter
	final int workerId;
	final PanelUserDatabase panelUserDatabase;
	final WorkerManager workerManager;
	@Setter
	@Getter
	String startCommand;
	@Setter
	@Getter
	boolean isRunning = false;
	@Getter
	@Setter
	String name;

	public AbstractServer(int id, int workerId, String startCommand, WorkerManager workerManager, PanelUserDatabase panelUserDatabase) {
		this.id = id;
		this.name = "SERVER-" + id;
		this.workerId = workerId;
		this.startCommand = startCommand;
		this.workerManager = workerManager;
		this.panelUserDatabase = panelUserDatabase;
	}

	@Override
	public UmbrellaWorker getOnlineWorker() {
		return ((OfflineWorker) getWorker()).getOnlineWorker();
	}

	@Override
	public Worker getWorker() {
		return workerManager.getWorker(workerId);
	}

	UmbrellaWorker getWorkerOrThrow() {
		UmbrellaWorker worker = getOnlineWorker();
		if (worker == null) {
			throw new IllegalStateException("Worker is offline!");
		}
		return worker;
	}

	@Override
	public List<PanelUser> listUsers(int permission) {
		List<PanelUser> temp = new ArrayList<>();
		for (PanelUser user : panelUserDatabase.getAllUsers()) {
			if (user.hasPermission(id, permission)) {
				temp.add(user);
			}
		}
		return temp;
	}
}
