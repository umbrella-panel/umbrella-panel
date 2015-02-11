package ms.idrea.umbrellapanel.web.gameserver;

import lombok.Getter;
import lombok.Setter;
import ms.idrea.umbrellapanel.core.PanelUser;
import ms.idrea.umbrellapanel.core.net.messages.DispatchCommandMessage;
import ms.idrea.umbrellapanel.core.net.messages.ManageGameServerMessage;
import ms.idrea.umbrellapanel.core.net.messages.UpdateGameServerMessage;
import ms.idrea.umbrellapanel.util.Address;
import ms.idrea.umbrellapanel.web.net.Worker;
import ms.idrea.umbrellapanel.worker.GameServer;

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
	private boolean isRunning; // TODO
	private Worker worker;

	public UmbrellaGameServer(int id, int userId, Address address, String startCommand, Worker worker) {
		this.id = id;
		this.userId = userId;
		this.address = address;
		this.startCommand = startCommand;
		this.worker = worker;
	}

	@Override
	public void setup() {
		worker.send(new UpdateGameServerMessage(ms.idrea.umbrellapanel.core.net.messages.UpdateGameServerMessage.Action.CREATE, id, userId, address, startCommand));
	}

	@Override
	public void delete() {
		worker.send(new ManageGameServerMessage(ms.idrea.umbrellapanel.core.net.messages.ManageGameServerMessage.Action.DELETE, id));
	}

	@Override
	public boolean start() {
		if (isRunning()) {
			return false;
		}
		worker.send(new ManageGameServerMessage(ms.idrea.umbrellapanel.core.net.messages.ManageGameServerMessage.Action.START, id));
		return true;
	}

	@Override
	public boolean forceStop() {
		if (!isRunning()) {
			return false;
		}
		worker.send(new ManageGameServerMessage(ms.idrea.umbrellapanel.core.net.messages.ManageGameServerMessage.Action.FORCE_STOP, id));
		return true;
	}

	@Override
	public boolean dispatchCommand(String str) {
		if (!isRunning()) {
			return false;
		}
		worker.send(new DispatchCommandMessage(id, str));
		return true;
	}

	@Override
	public PanelUser getPanelUser() {
		// TODO
		return null;
	}
}
