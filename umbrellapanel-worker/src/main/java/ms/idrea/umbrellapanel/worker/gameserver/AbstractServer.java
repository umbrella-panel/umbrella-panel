package ms.idrea.umbrellapanel.worker.gameserver;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

import ms.idrea.umbrellapanel.api.worker.gameserver.GameServer;
import ms.idrea.umbrellapanel.api.worker.gameserver.ServerManager;
import ms.idrea.umbrellapanel.api.worker.net.NetworkClient;

public abstract class AbstractServer implements GameServer {

	@Getter
	final int id;
	final NetworkClient networkClient;
	@Getter
	final File workingDirectory;
	@Setter
	String startCommand;

	public AbstractServer(int id, String startCommand, ServerManager serverManager, NetworkClient networkClient) {
		this.id = id;
		this.startCommand = startCommand;
		this.networkClient = networkClient;
		// make sure workingDirectory is set
		workingDirectory = new File(serverManager.getGameServerDirectory(), String.valueOf(id));
		if (workingDirectory.getParent() != null) {
			new File(workingDirectory.getParent()).mkdirs();
		}
		workingDirectory.mkdir();
	}

	@Override
	public String getStartCommand() {
		return startCommand;
	}
}
