package ms.idrea.umbrellapanel.chief.gameserver;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import ms.idrea.umbrellapanel.api.chief.PanelUserDatabase;
import ms.idrea.umbrellapanel.api.chief.WorkerManager;
import ms.idrea.umbrellapanel.api.gameserver.ManagedServerInstance;
import ms.idrea.umbrellapanel.api.gameserver.ServerLog;
import ms.idrea.umbrellapanel.api.gameserver.SingleInstanceServer;
import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.net.messages.DispatchCommandMessage;
import ms.idrea.umbrellapanel.net.messages.ManageGameServerMessage;
import ms.idrea.umbrellapanel.net.messages.UpdateGameServerMessage;

@ToString(exclude = { "logBuffer" }, callSuper = true)
public class UmbrellaSingleInstanceGameServer extends AbstractServer implements SingleInstanceServer, ManagedServerInstance {

	final List<ServerLog> logBuffer = Collections.synchronizedList(new LinkedList<ServerLog>());
	@Getter
	@Setter
	Address address;

	public UmbrellaSingleInstanceGameServer(int id, int workerId, Address address, String startCommand, WorkerManager workerManager, PanelUserDatabase panelUserDatabase) {
		super(id, workerId, startCommand, workerManager, panelUserDatabase);
		this.address = address;
	}

	@Override
	public void delete() {
		getWorkerOrThrow().send(new ManageGameServerMessage(ManageGameServerMessage.Action.DELETE, id));
	}

	@Override
	public void update() {
		getWorkerOrThrow().send(new UpdateGameServerMessage(UpdateGameServerMessage.Action.UPDATE, id, address, startCommand));
	}

	@Override
	public boolean start() {
		if (isRunning()) {
			return false;
		}
		logBuffer.clear(); // clear log on restart
		getWorkerOrThrow().send(new ManageGameServerMessage(ManageGameServerMessage.Action.START, id));
		return true;
	}

	@Override
	public boolean forceStop() {
		if (!isRunning()) {
			return false;
		}
		getWorkerOrThrow().send(new ManageGameServerMessage(ManageGameServerMessage.Action.FORCE_STOP, id));
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
	public void appendLog(String log) {
		if (logBuffer.size() >= 1000) {
			logBuffer.remove(0);
		}
		logBuffer.add(new ServerLog(System.currentTimeMillis(), log));
	}

	@Override
	public List<ServerLog> getLogBuffer() {
		return new LinkedList<>(logBuffer);
	}
}
