package ms.idrea.umbrellapanel.chief;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.flowpowered.networking.Message;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ms.idrea.umbrellapanel.api.chief.PanelUserDatabase;
import ms.idrea.umbrellapanel.api.chief.net.NetworkServer;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.net.messages.UpdatePanelUserMessage;
import ms.idrea.umbrellapanel.net.messages.UpdatePanelUserMessage.Action;

public class UmbrellaPanelUserDatabase implements PanelUserDatabase {

	private static final JsonParser PARSER = new JsonParser();
	private NetworkServer networkServer;
	private final List<PanelUser> users = new CopyOnWriteArrayList<PanelUser>();
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
		return Collections.unmodifiableList(users);
	}

	@Override
	public PanelUser createUser(String name, String password) {
		int id = getNextId();
		PanelUser user = new PanelUser(id, name, password);
		users.add(user);
		broadcastChange(user, Action.UPDATE);
		return user;
	}

	@Override
	/* 
	 * To update an user create a new PanelUser object with the same id of the old user 
	 * 
	 */
	public void updateUser(PanelUser user) {
		if (getUser(user.getId()) == null) {
			throw new IllegalArgumentException("User not found in system!");
		}
		PanelUser oldUser = getUser(user.getId());
		users.remove(oldUser);
		users.add(user);
		broadcastChange(user, Action.UPDATE);
	}

	@Override
	public void deleteUser(PanelUser user) {
		users.remove(user.getId());
		broadcastChange(user, Action.DELETE);
	}

	@Override
	public PanelUser getUser(String name) {
		for (PanelUser user : users) {
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
		JsonObject userSaveData = new JsonObject();
		JsonArray usersArray = new JsonArray();
		for (PanelUser user : users) {
			JsonObject obj = new JsonObject();
			obj.addProperty("id", user.getId());
			obj.addProperty("name", user.getName());
			obj.addProperty("password", user.getPassword());
			JsonArray permissionsArray = new JsonArray();
			for (int serverId : user.getPermissions().keySet()) {
				permissionsArray.add(serverId + ":" + user.getPermission(serverId));
			}
			obj.add("permissions", permissionsArray);
			usersArray.add(obj);
			userSaveData.add("users", usersArray);
			writer.write(userSaveData.toString());
		}
		writer.flush();
	}

	@Override
	public void load(Reader in) throws IOException {
		BufferedReader reader = new BufferedReader(in);
		String readed = reader.readLine();
		if (readed == null) {
			return;
		}
		JsonObject obj = PARSER.parse(readed).getAsJsonObject();
		JsonArray userArray = obj.get("users").getAsJsonArray();
		for (JsonElement element : userArray) {
			JsonObject userData = (JsonObject) element;
			PanelUser user = new PanelUser(userData.get("id").getAsInt(), userData.get("name").getAsString(),
					userData.get("password").getAsString(), true);
			users.add(user);
		}
	}
}
