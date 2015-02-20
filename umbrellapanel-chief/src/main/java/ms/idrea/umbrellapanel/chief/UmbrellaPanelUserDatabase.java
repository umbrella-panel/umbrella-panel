package ms.idrea.umbrellapanel.chief;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.idrea.umbrellapanel.api.chief.PanelUserDatabase;
import ms.idrea.umbrellapanel.api.chief.net.NetworkServer;
import ms.idrea.umbrellapanel.api.core.PanelUser;
import ms.idrea.umbrellapanel.net.messages.UpdatePanelUserMessage;
import ms.idrea.umbrellapanel.net.messages.UpdatePanelUserMessage.Action;

import com.flowpowered.networking.Message;

public class UmbrellaPanelUserDatabase implements PanelUserDatabase {

	private NetworkServer networkServer;
	private Map<Integer, PanelUser> users = new HashMap<>();
	private int nextId = 0;

	public UmbrellaPanelUserDatabase(NetworkServer networkServer) {
		this.networkServer = networkServer;
	}

	@Override
	public synchronized int getNextId() {
		return nextId++;
	}

	@Override
	public List<PanelUser> getAllUsers() {
		List<PanelUser> list = new ArrayList<>();
		for (Integer id : users.keySet()) {
			list.add(getUser(id));
		}
		return list;
	}

	@Override
	public PanelUser createUser(String name, String password) {
		int id = getNextId();
		PanelUser user = new PanelUser(id, name, password);
		users.put(id, user);
		broadcastChange(user, Action.UPDATE);
		return user;
	}

	@Override
	public void updateUser(PanelUser user) {
		if (users.containsKey(user.getId())) {
			users.put(user.getId(), user);
			broadcastChange(user, Action.UPDATE);
		} else {
			throw new NullPointerException("User not found!");
		}
	}

	@Override
	public void deleteUser(PanelUser user) {
		users.remove(user.getId());
		broadcastChange(user, Action.DELETE);
	}

	@Override
	public PanelUser getUser(int id) {
		return users.get(id);
	}

	private void broadcastChange(PanelUser user, Action action) {
		Message message = new UpdatePanelUserMessage(action, user);
		networkServer.broadcast(message);
	}
}
