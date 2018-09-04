package ms.idrea.umbrellapanel.api.worker;

import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;

public interface UserRegistery {

	void delete(PanelUser user);

	void update(PanelUser user);

	PanelUser getPanelUser(int id);

	PanelUser getByName(String name);
}
