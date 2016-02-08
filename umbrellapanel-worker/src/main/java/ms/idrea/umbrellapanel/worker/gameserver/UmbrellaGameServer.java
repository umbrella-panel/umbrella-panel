package ms.idrea.umbrellapanel.worker.gameserver;

import java.io.File;
import java.io.IOException;

import lombok.Getter;
import lombok.Setter;

import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.api.worker.LogHandler;
import ms.idrea.umbrellapanel.api.worker.gameserver.GameServer;
import ms.idrea.umbrellapanel.api.worker.gameserver.ServerManager;
import ms.idrea.umbrellapanel.api.worker.net.NetworkClient;
import ms.idrea.umbrellapanel.net.messages.GameServerStatusMessage;
import ms.idrea.umbrellapanel.net.messages.GameServerStatusMessage.Status;
import ms.idrea.umbrellapanel.worker.gameserver.UmbrellaServerContoller.ProcessState;

import org.apache.commons.io.FileUtils;

public class UmbrellaGameServer implements GameServer {

	@Getter
	private int id;
	@Getter
	@Setter
	private Address address;
	@Setter
	private String startCommand;
	private UmbrellaServerContoller umbrellaServerContoller;
	@Getter
	private File workingDirectory;
	private LogHandler logHandler;
	private NetworkClient networkClient;

	public UmbrellaGameServer(int id, Address address, String startCommand, LogHandler logHandler, ServerManager serverManager, NetworkClient networkClient) {
		this.id = id;
		this.address = address;
		this.startCommand = startCommand;
		this.logHandler = logHandler;
		this.networkClient = networkClient;
		// make sure workingDirectory is set
		workingDirectory = new File(serverManager.getGameServerDirectory(), String.valueOf(id));
		if (workingDirectory.getParent() != null) {
			new File(workingDirectory.getParent()).mkdirs();
		}
		workingDirectory.mkdir();
	}

	@Override
	public void setup() {
		// TODO
		// download spigot
	}

	@Override
	public void delete() {
		id = -1; // ID is no longer vaild
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
		umbrellaServerContoller = new UmbrellaServerContoller(this);
		return true;
	}

	@Override
	public boolean forceStop() {
		if (!isRunning()) {
			return false;
		}
		umbrellaServerContoller.forceStop();
		return true;
	}

	@Override
	public boolean dispatchCommand(String str) {
		if (!isRunning()) {
			return false;
		}
		umbrellaServerContoller.dispatchCommand(str);
		return true;
	}

	@Override
	public boolean isRunning() {
		return umbrellaServerContoller != null;
	}
	
	@Override
	public String getStartCommand() {
		return startCommand + " --host " + address.getHost() + " --port " + address.getPort();
	}

	@Override
	public void joinProcess() throws InterruptedException {
		if (umbrellaServerContoller != null) {
			umbrellaServerContoller.join();
		}
	}

	protected void appendServerLog(String message) {
		logHandler.append(id, message);
	}

	protected void updateProcessState(ProcessState state) {
		if (state == ProcessState.RUNNING) {
			networkClient.send(new GameServerStatusMessage(id, Status.RUNNING));
		} else if (state == ProcessState.STOPPED) {
			logHandler.flush(); // TODO flush only this server
			umbrellaServerContoller = null;
			if (id != -1) { // check if id is -1 because the server is removed
				networkClient.send(new GameServerStatusMessage(id, Status.STOPPED));
			}
		}
	}
}
