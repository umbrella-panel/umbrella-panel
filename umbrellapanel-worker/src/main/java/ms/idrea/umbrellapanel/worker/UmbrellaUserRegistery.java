package ms.idrea.umbrellapanel.worker;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.worker.UserRegistery;

public class UmbrellaUserRegistery implements UserRegistery {

	private List<PanelUser> users = new CopyOnWriteArrayList<>();

	@Override
	public void update(PanelUser user) {
		users.add(user);
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
		for (PanelUser o : users) {
			if (o.getName().equals(name)) {
				return o;
			}
		}
		return null;
	}
}
