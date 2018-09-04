package ms.idrea.umbrellapanel.worker.gameserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import ms.idrea.umbrellapanel.api.gameserver.MultiInstanceServer;
import ms.idrea.umbrellapanel.api.gameserver.ServerInstance;
import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.api.worker.LogHandler;
import ms.idrea.umbrellapanel.api.worker.gameserver.ServerManager;
import ms.idrea.umbrellapanel.api.worker.net.NetworkClient;
import ms.idrea.umbrellapanel.net.messages.GameServerStatusMessage;
import ms.idrea.umbrellapanel.net.messages.GameServerStatusMessage.Status;
import ms.idrea.umbrellapanel.worker.gameserver.UmbrellaServerContoller.ProcessState;

@ToString(exclude = { "logHandler" })
public class UmbrellaMultiInstanceGameServer extends AbstractServer implements MultiInstanceServer {

	private final Map<Integer, ServerInstance> instances = new HashMap<>();
	private final LogHandler logHandler;

	UmbrellaMultiInstanceGameServer(int id, String startCommand, LogHandler logHandler, ServerManager serverManager, NetworkClient networkClient) {
		super(id, startCommand, serverManager, networkClient);
		this.logHandler = logHandler;
	}

	@Override
	public int getNextInstanceId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ServerInstance createNewInstance(int id, Address address) {
		UmbrellaServerInstance instance = new UmbrellaServerInstance(id, address);
		instances.put(instance.getInstanceId(), instance);
		return instance;
	}

	@Override
	public void addInstance(int instanceId, Address address) {
		UmbrellaServerInstance instance = new UmbrellaServerInstance(instanceId, address);
		instances.put(instance.getInstanceId(), instance);
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
	public void delete() {
		for (ServerInstance instance : getInstances()) {
			instance.delete();
		}
		try {
			FileUtils.deleteDirectory(workingDirectory);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@ToString
	public class UmbrellaServerInstance implements ServerInstance {

		@Getter
		final int instanceId;
		UmbrellaServerContoller contoller;
		@Setter
		@Getter
		Address address;

		public UmbrellaServerInstance(int instanceId, Address address) {
			this.instanceId = instanceId;
			this.address = address;
		}

		@Override
		public int getId() {
			return instanceId;
		}

		@Override
		public boolean start() {
			if (isRunning()) {
				return false;
			}
			contoller = new UmbrellaServerContoller(this, UmbrellaMultiInstanceGameServer.this);
			return true;
		}

		@Override
		public boolean forceStop() {
			if (!isRunning()) {
				return false;
			}
			contoller.forceStop();
			return true;
		}

		@Override
		public boolean dispatchCommand(String str) {
			if (!isRunning()) {
				return false;
			}
			return contoller.dispatchCommand(str);
		}

		@Override
		public boolean isRunning() {
			return contoller != null;
		}

		@Override
		public void delete() {
			if (isRunning()) {
				forceStop();
			}
			instances.remove(getInstanceId());
		}

		@Override
		public void appendLog(String log) {
			logHandler.append(id, instanceId, log);
		}

		protected void updateProcessState(ProcessState state) {
			if (state == ProcessState.RUNNING) {
				networkClient.send(new GameServerStatusMessage(id, instanceId, Status.RUNNING));
			} else if (state == ProcessState.STOPPED) {
				logHandler.flush(); // TODO flush only this server
				contoller = null;
				if (instances.containsValue(this)) { // check if id is -1 because the server is removed
					networkClient.send(new GameServerStatusMessage(id, instanceId, Status.STOPPED));
				}
			}
		}
	}
}
