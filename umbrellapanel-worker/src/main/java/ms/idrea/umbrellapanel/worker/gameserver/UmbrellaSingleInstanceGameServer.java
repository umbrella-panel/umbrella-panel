package ms.idrea.umbrellapanel.worker.gameserver;

import java.io.IOException;

import org.apache.commons.io.FileUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import ms.idrea.umbrellapanel.api.gameserver.SingleInstanceServer;
import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.api.worker.LogHandler;
import ms.idrea.umbrellapanel.api.worker.gameserver.ServerManager;
import ms.idrea.umbrellapanel.api.worker.net.NetworkClient;
import ms.idrea.umbrellapanel.net.messages.GameServerStatusMessage;
import ms.idrea.umbrellapanel.net.messages.GameServerStatusMessage.Status;
import ms.idrea.umbrellapanel.worker.gameserver.UmbrellaServerContoller.ProcessState;

@ToString(exclude = { "logHandler" })
public class UmbrellaSingleInstanceGameServer extends AbstractServer implements SingleInstanceServer {

	private LogHandler logHandler;
	private UmbrellaServerContoller contoller;
	@Getter
	@Setter
	private Address address;

	UmbrellaSingleInstanceGameServer(int id, Address address, String startCommand, LogHandler logHandler, ServerManager serverManager, NetworkClient networkClient) {
		super(id, startCommand, serverManager, networkClient);
		this.address = address;
		this.logHandler = logHandler;
	}

	@Override
	public void delete() {
		forceStop();
		try {
			FileUtils.deleteDirectory(workingDirectory);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean start() {
		if (isRunning()) {
			return false;
		}
		contoller = new UmbrellaServerContoller(this, this);
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
		contoller.dispatchCommand(str);
		return true;
	}

	@Override
	public boolean isRunning() {
		return contoller != null;
	}

	public void joinProcess() throws InterruptedException {
		if (contoller != null) {
			contoller.join();
		}
	}

	@Override
	public void appendLog(String message) {
		logHandler.append(id, message);
	}

	protected void updateProcessState(ProcessState state) {
		if (state == ProcessState.RUNNING) {
			networkClient.send(new GameServerStatusMessage(id, id, Status.RUNNING));
		} else if (state == ProcessState.STOPPED) {
			logHandler.flush(); // TODO flush only this server
			contoller = null;
			if (id != -1) { // check if id is -1 because the server is removed
				networkClient.send(new GameServerStatusMessage(id, id, Status.STOPPED));
			}
		}
	}
}
