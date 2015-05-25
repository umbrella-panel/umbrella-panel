package ms.idrea.umbrellapanel.api.core.permissions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Permissible {

	private ConcurrentMap<Integer, Integer> permissions;

	public Permissible() {
		permissions = new ConcurrentHashMap<>();
	}

	public void grantGlobalPermission(int permission) {
		if (permission <= 0) {
			permissions.remove(-1);
		} else {
			permissions.put(-1, permission);
		}
	}

	public void grantPermission(int serverId, int permission) {
		if (permission <= 0) {
			permissions.remove(serverId);
		} else {
			permissions.put(serverId, permission);
		}
	}

	public boolean hasGlobalPermission(int permission) {
		return getPermission(-1) >= permission;
	}

	public boolean hasPermission(int serverId, int permission) {
		return getPermission(serverId) >= permission || hasGlobalPermission(permission);
	}

	public int getPermission(int serverId) {
		if (permissions.containsKey(serverId)) {
			return permissions.get(serverId);
		} else {
			return 0;
		}
	}

	public Map<Integer, Integer> getPermissions() {
		return permissions;
	}
}
