package ms.idrea.umbrellapanel.chief.gameserver;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ms.idrea.umbrellapanel.api.chief.PanelUserDatabase;
import ms.idrea.umbrellapanel.api.chief.WorkerManager;
import ms.idrea.umbrellapanel.api.chief.gameserver.GameServer;
import ms.idrea.umbrellapanel.api.core.PanelUser;
import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.chief.net.Worker;
import ms.idrea.umbrellapanel.net.messages.DispatchCommandMessage;
import ms.idrea.umbrellapanel.net.messages.ManageGameServerMessage;
import ms.idrea.umbrellapanel.net.messages.UpdateGameServerMessage;

@ToString
public class UmbrellaGameServer implements GameServer {

	@Getter
	private int id;
	@Getter
	@Setter
	private Address address;
	@Setter
	@Getter
	private String startCommand;
	@Getter
	private int userId;
	@Getter
	private boolean isRunning = false;
	private int workerId;
	private PanelUserDatabase panelUserDatabase;
	private WorkerManager workerManager;

	public UmbrellaGameServer(int id, int userId, int workerId, Address address, String startCommand, WorkerManager workerManager, PanelUserDatabase panelUserDatabase) {
		this.id = id;
		this.userId = userId;
		this.workerId = workerId;
		this.address = address;
		this.startCommand = startCommand;
		this.workerManager = workerManager;
		this.panelUserDatabase = panelUserDatabase;
	}

	@Override
	public void setup() {
		getWorkerOrThrow().send(new UpdateGameServerMessage(ms.idrea.umbrellapanel.net.messages.UpdateGameServerMessage.Action.CREATE, id, userId, address, startCommand));
	}

	@Override
	public void delete() {
		getWorkerOrThrow().send(new ManageGameServerMessage(ms.idrea.umbrellapanel.net.messages.ManageGameServerMessage.Action.DELETE, id));
	}

	@Override
	public boolean start() {
		if (isRunning()) {
			return false;
		}
		getWorkerOrThrow().send(new ManageGameServerMessage(ms.idrea.umbrellapanel.net.messages.ManageGameServerMessage.Action.START, id));
		return true;
	}

	@Override
	public boolean forceStop() {
		if (!isRunning()) {
			return false;
		}
		getWorkerOrThrow().send(new ManageGameServerMessage(ms.idrea.umbrellapanel.net.messages.ManageGameServerMessage.Action.FORCE_STOP, id));
		return true;
	}

	@Override
	public boolean dispatchCommand(String str) {
		if (!isRunning()) {
			return false;
		}
		getWorkerOrThrow().send(new DispatchCommandMessage(id, str));
		return true;
	}

	@Override
	public PanelUser getPanelUser() {
		return panelUserDatabase.getUser(id);
	}

	@Override
	public Worker getWorker() {
		return (Worker) workerManager.getRunningWorker(workerId);
	}

	private Worker getWorkerOrThrow() {
		Worker worker = getWorker();
		if (worker == null) {
			throw new IllegalStateException("Worker is offline!");
		}
		return worker;
	}

	@Override
	public void setRunning(boolean running) {
		this.isRunning = running;
		System.err.println("NEW STATE: " + running);
	}
}
