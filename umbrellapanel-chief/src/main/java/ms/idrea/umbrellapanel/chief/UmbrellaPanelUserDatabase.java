package ms.idrea.umbrellapanel.chief;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.idrea.umbrellapanel.api.chief.PanelUserDatabase;
import ms.idrea.umbrellapanel.api.chief.net.NetworkServer;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
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
	public PanelUser getUser(String name) {
		for (int id : users.keySet()) {
			PanelUser user = getUser(id);
			if (user.getName().equalsIgnoreCase(name)) {
				return user;
			}
		}
		return null;
	}

	@Override
	public PanelUser getUser(int id) {
		return users.get(id);
	}

	private void broadcastChange(PanelUser user, Action action) {
		Message message = new UpdatePanelUserMessage(action, user);
		networkServer.broadcast(message);
	}

	@Override
	public void save(Writer out) throws IOException {
		BufferedWriter writer = new BufferedWriter(out);
		writer.write(String.valueOf(nextId));
		writer.newLine();
		writer.write(String.valueOf(users.size()));
		writer.newLine();
		for (int id : users.keySet()) {
			PanelUser user = getUser(id);
			writer.write(String.valueOf(user.getId()));
			writer.newLine();
			writer.write(user.getName());
			writer.newLine();
			writer.write(user.getPassword());
			writer.newLine();
			writer.write(String.valueOf(user.getPermissions().size()));
			writer.newLine();
			for (int serverId : user.getPermissions().keySet()) {
				writer.write(serverId + ":" + user.getPermission(serverId));
				writer.newLine();
			}
		}
		writer.flush();
	}

	@Override
	public void load(Reader in) throws IOException {
		BufferedReader reader = new BufferedReader(in);
		nextId = Integer.valueOf(reader.readLine());
		int userSize = Integer.valueOf(reader.readLine());
		users = new HashMap<>(userSize);
		for (int i = 0; i < userSize; i++) {
			int id = Integer.valueOf(reader.readLine());
			PanelUser user = new PanelUser(id, reader.readLine(), reader.readLine(), true);
			int permissionSize = Integer.valueOf(reader.readLine());
			for (int j = 0; j < permissionSize; j++) {
				String[] l = reader.readLine().split(":");
				user.grantPermission(Integer.valueOf(l[0]), Integer.valueOf(l[1]));
			}
			users.put(id, user);
		}
	}
}
