package ms.idrea.umbrellapanel.chief.gameserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ms.idrea.umbrellapanel.api.chief.PanelUserDatabase;
import ms.idrea.umbrellapanel.api.chief.Worker;
import ms.idrea.umbrellapanel.api.chief.WorkerManager;
import ms.idrea.umbrellapanel.api.chief.gameserver.GameServer;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.chief.UmbrellaWorkerManager.OfflineWorker;
import ms.idrea.umbrellapanel.chief.net.UmbrellaWorker;
import ms.idrea.umbrellapanel.net.messages.DispatchCommandMessage;
import ms.idrea.umbrellapanel.net.messages.ManageGameServerMessage;
import ms.idrea.umbrellapanel.net.messages.UpdateGameServerMessage;

@ToString(exclude = {"panelUserDatabase", "workerManager", "logBuffer"})
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
	private boolean isRunning = false;
	@Getter
	@Setter
	private String name;
	@Getter
	private int workerId;
	private PanelUserDatabase panelUserDatabase;
	private WorkerManager workerManager;
	private List<ServerLog> logBuffer;

	public UmbrellaGameServer(int id, int workerId, Address address, String startCommand, WorkerManager workerManager, PanelUserDatabase panelUserDatabase) {
		this.id = id;
		this.name = "SERVER-" + id + " ON " + address.getHost() + ":" + address.getPort();
		this.workerId = workerId;
		this.address = address;
		this.startCommand = startCommand;
		this.workerManager = workerManager;
		this.panelUserDatabase = panelUserDatabase;
		this.logBuffer = Collections.synchronizedList(new LinkedList<ServerLog>());
	}

	@Override
	public void setup() {
		getWorkerOrThrow().send(new UpdateGameServerMessage(ms.idrea.umbrellapanel.net.messages.UpdateGameServerMessage.Action.CREATE, id, address, startCommand));
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
		logBuffer.clear(); // clear log on restart
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
	public UmbrellaWorker getOnlineWorker() {
		return ((OfflineWorker) getWorker()).getOnlineWorker();
	}

	@Override
	public Worker getWorker() {
		return workerManager.getWorker(workerId);
	}
	
	private UmbrellaWorker getWorkerOrThrow() {
		UmbrellaWorker worker = getOnlineWorker();
		if (worker == null) {
			throw new IllegalStateException("Worker is offline!");
		}
		return worker;
	}

	@Override
	public void setRunning(boolean running) {
		this.isRunning = running;
	}

	@Override
	public void appendLog(String log) {
		logBuffer.add(new ServerLog(System.currentTimeMillis(), log));
	}

	@Override
	public List<ServerLog> getLogBuffer() {
		return new LinkedList<>(logBuffer);
	}

	@Override
	public void update() {
		getWorkerOrThrow().send(new UpdateGameServerMessage(ms.idrea.umbrellapanel.net.messages.UpdateGameServerMessage.Action.UPDATE, id, address, startCommand));
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
