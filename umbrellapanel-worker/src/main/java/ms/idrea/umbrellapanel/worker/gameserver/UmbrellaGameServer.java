package ms.idrea.umbrellapanel.worker.gameserver;

import java.io.File;

import lombok.Getter;
import ms.idrea.umbrellapanel.core.PanelUser;
import ms.idrea.umbrellapanel.util.Address;
import ms.idrea.umbrellapanel.worker.GameServer;
import ms.idrea.umbrellapanel.worker.LogHandler;
import ms.idrea.umbrellapanel.worker.ServerManager;
import ms.idrea.umbrellapanel.worker.UserRegistery;
import ms.idrea.umbrellapanel.worker.gameserver.UmbrellaServerContoller.ProcessState;

public class UmbrellaGameServer implements GameServer {

	@Getter
	private int id;
	@Getter
	private Address address;
	@Getter
	private int userId;
	private UmbrellaServerContoller umbrellaServerContoller;
	@Getter
	private File workingDirectory;
	private LogHandler logHandler;
	private ServerManager serverManager;
	private UserRegistery userRegistery;

	public UmbrellaGameServer(int id, int userId, Address address, LogHandler logHandler, ServerManager serverManager, UserRegistery userRegistery) {
		this.id = id;
		this.userId = userId;
		this.address = address;
		this.logHandler = logHandler;
		this.serverManager = serverManager;
	}

	@Override
	public void setup() {
		// TODO
		// download spigot
		workingDirectory = new File(new File(serverManager.getGameServerDirectory(), String.valueOf(userId)), String.valueOf(id)); // TODO better dir handling
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
		return "java -jar server.jar -h " + address.getIp() + " -p " + address.getPort(); // TODO
	}

	@Override
	public PanelUser getPanelUser() {
		return userRegistery.getPanelUser(userId);
	}

	protected void appendServerLog(String message) {
		logHandler.append(id, message);
	}

	protected void updateProcessState(ProcessState state) {
		if (state == ProcessState.STOPPED) {
			umbrellaServerContoller = null;
		}
	}
}
