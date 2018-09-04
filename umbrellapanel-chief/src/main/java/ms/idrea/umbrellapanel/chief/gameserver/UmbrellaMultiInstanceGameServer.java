package ms.idrea.umbrellapanel.chief.gameserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.ToString;

import ms.idrea.umbrellapanel.api.chief.PanelUserDatabase;
import ms.idrea.umbrellapanel.api.chief.WorkerManager;
import ms.idrea.umbrellapanel.api.gameserver.ManagedServer;
import ms.idrea.umbrellapanel.api.gameserver.ManagedServerInstance;
import ms.idrea.umbrellapanel.api.gameserver.MultiInstanceServer;
import ms.idrea.umbrellapanel.api.gameserver.ServerInstance;
import ms.idrea.umbrellapanel.api.gameserver.ServerLog;
import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.net.messages.DispatchMultiServerInstanceCommandMessage;
import ms.idrea.umbrellapanel.net.messages.ManageGameServerMessage;
import ms.idrea.umbrellapanel.net.messages.ManageMultiServerInstanceMessage;
import ms.idrea.umbrellapanel.net.messages.UpdateGameServerMessage;
import ms.idrea.umbrellapanel.net.messages.UpdateMultiInstanceServerMessage;
import ms.idrea.umbrellapanel.net.messages.UpdateServerInstanceMessage;

@ToString(callSuper = true)
public class UmbrellaMultiInstanceGameServer extends AbstractServer implements MultiInstanceServer, ManagedServer {

	private final AtomicInteger nextInstanceId;
	private final Map<Integer, ServerInstance> instances = new HashMap<>();

	public UmbrellaMultiInstanceGameServer(int id, int workerId, String startCommand, WorkerManager workerManager, PanelUserDatabase panelUserDatabase, int nextInstanceId) {
		super(id, workerId, startCommand, workerManager, panelUserDatabase);
		this.nextInstanceId = new AtomicInteger(nextInstanceId);
	}

	@Override
	public int getNextInstanceId() {
		return nextInstanceId.get();
	}

	@Override
	public void update() {
		getWorkerOrThrow().send(new UpdateMultiInstanceServerMessage(UpdateGameServerMessage.Action.UPDATE, id, startCommand));
	}

	@Override
	public void delete() {
		getWorkerOrThrow().send(new ManageGameServerMessage(ManageGameServerMessage.Action.DELETE, id));
	}

	@Override
	public List<ServerInstance> getInstances() {
		return new ArrayList<>(instances.values());
	}

	@Override
	public ServerInstance getInstance(int id) {
		return instances.get(id);
	}

	@Override
	public ServerInstance createNewInstance(int _, Address address) {
		UmbrellaServerInstance instance = new UmbrellaServerInstance(nextInstanceId.getAndIncrement(), address);
		instances.put(instance.getId(), instance);
		getWorkerOrThrow().send(new UpdateServerInstanceMessage(UpdateGameServerMessage.Action.CREATE, this.id, instance.getId(), instance.getAddress()));
		return instance;
	}

	@Override
	public void addInstance(int id, Address address) {
		UmbrellaServerInstance instance = new UmbrellaServerInstance(id, address);
		instances.put(instance.getId(), instance);
	}

	@ToString(exclude = { "logBuffer" }, callSuper = true)
	public class UmbrellaServerInstance implements ManagedServerInstance {

		final List<ServerLog> logBuffer = Collections.synchronizedList(new LinkedList<ServerLog>());
		@Getter
		final int instanceId;
		@Getter
		Address address;
		boolean running = false;

		public UmbrellaServerInstance(int instanceId, Address address) {
			this.instanceId = instanceId;
			this.address = address;
		}

		@Override
		public int getId() {
			return instanceId;
		}

		@Override
		public void setAddress(Address address) {
			this.address = address;
			update();
		}

		public void update() {
			getWorkerOrThrow().send(new UpdateServerInstanceMessage(UpdateGameServerMessage.Action.UPDATE, id, instanceId, getAddress()));
		}

		@Override
		public boolean start() {
			if (isRunning()) {
				return false;
			}
			getWorkerOrThrow().send(new ManageMultiServerInstanceMessage(ManageGameServerMessage.Action.START, id, instanceId));
			return true;
		}

		@Override
		public boolean forceStop() {
			if (!isRunning()) {
				return false;
			}
			getWorkerOrThrow().send(new ManageMultiServerInstanceMessage(ManageGameServerMessage.Action.FORCE_STOP, id, instanceId));
			return true;
		}

		@Override
		public boolean dispatchCommand(String str) {
			if (!isRunning()) {
				return false;
			}
			getWorkerOrThrow().send(new DispatchMultiServerInstanceCommandMessage(id, instanceId, str));
			return true;
		}

		@Override
		public boolean isRunning() {
			return isRunning;
		}

		@Override
		public void delete() {
			instances.remove(getInstanceId());
			getWorkerOrThrow().send(new ManageMultiServerInstanceMessage(ManageGameServerMessage.Action.DELETE, id, instanceId));
		}

		@Override
		public void appendLog(String log) {
			if (logBuffer.size() >= 1000) {
				logBuffer.remove(0);
			}
			logBuffer.add(new ServerLog(System.currentTimeMillis(), log));
		}

		@Override
		public void setRunning(boolean running) {
			this.running = running;
		}

		@Override
		public List<ServerLog> getLogBuffer() {
			return new LinkedList<>(logBuffer);
		}
	}
}
