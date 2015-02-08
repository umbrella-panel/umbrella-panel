package ms.idrea.umbrellapanel.worker.gameserver;

import java.io.File;

import lombok.Getter;
import ms.idrea.umbrellapanel.core.PanelUser;
import ms.idrea.umbrellapanel.util.Address;
import ms.idrea.umbrellapanel.worker.GameServer;
import ms.idrea.umbrellapanel.worker.LogHandler;
import ms.idrea.umbrellapanel.worker.gameserver.ServerContoller.ProcessState;

public class UmbrellaGameServer implements GameServer {

	@Getter
	private int id;
	@Getter
	private Address address;
	@Getter
	private PanelUser panelUser;
	private ServerContoller serverContoller;
	@Getter
	private File workingDirectory;
	private LogHandler logHandler;

	public UmbrellaGameServer(int id, PanelUser panelUser, Address address, LogHandler logHandler) {
		this.id = id;
		this.panelUser = panelUser;
		this.address = address;
		this.logHandler = logHandler;
	}

	@Override
	public void setup() {
		// TODO
		// assign work dir
		// download spigot
		workingDirectory = new File("servers", String.valueOf(id));
		if (workingDirectory.getParent() != null) {
			new File(workingDirectory.getParent()).mkdirs();
		}
		workingDirectory.mkdir();
	}

	@Override
	public boolean start() {
		if (isRunning()) {
			return false;
		}
		serverContoller = new ServerContoller(this);
		return true;
	}

	@Override
	public boolean forceStop() {
		if (!isRunning()) {
			return false;
		}
		serverContoller.forceStop();
		return true;
	}

	@Override
	public boolean dispatchCommand(String str) {
		if (!isRunning()) {
			return false;
		}
		serverContoller.dispatchCommand(str);
		return true;
	}

	@Override
	public boolean isRunning() {
		return serverContoller != null;
	}

	@Override
	public String getStartCommand() {
		return "java -jar server.jar -h " + address.getIp() + " -p " + address.getPort(); // TODO
	}

	protected void appendServerLog(String message) {
		logHandler.append(id, message);
	}

	protected void updateProcessState(ProcessState state) {
		if (state == ProcessState.STOPPED) {
			serverContoller = null;
		}
	}
}
