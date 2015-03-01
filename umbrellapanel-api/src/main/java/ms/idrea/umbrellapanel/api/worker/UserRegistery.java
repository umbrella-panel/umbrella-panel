package ms.idrea.umbrellapanel.api.worker;

import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;

public interface UserRegistery {

	public void delete(PanelUser user);

	public void update(PanelUser user);

	public PanelUser getPanelUser(int id);

	public PanelUser getByName(String name);
}
