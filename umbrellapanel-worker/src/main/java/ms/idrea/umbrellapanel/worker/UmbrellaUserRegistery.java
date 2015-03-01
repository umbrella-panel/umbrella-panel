package ms.idrea.umbrellapanel.worker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.worker.UserRegistery;

public class UmbrellaUserRegistery implements UserRegistery {

	private ConcurrentMap<Integer, PanelUser> users = new ConcurrentHashMap<>();

	@Override
	public void update(PanelUser user) {
		users.put(user.getId(), user);
	}

	@Override
	public void delete(PanelUser user) {
		users.remove(user.getId());
	}

	@Override
	public PanelUser getPanelUser(int id) {
		return users.get(id);
	}

	@Override
	public PanelUser getByName(String name) {
		for (int id : users.keySet()) {
			PanelUser o = getPanelUser(id);
			if (o.getName().equals(name)) {
				return o;
			}
		}
		return null;
	}
}
