package ms.idrea.umbrellapanel.api.chief;

import ms.idrea.umbrellapanel.api.core.PanelUser;

public interface PanelUserDatabase {

	public PanelUser createUser(String name, String password);
	
	public void updateUser(PanelUser user);

	public void deleteUser(PanelUser user);

	public PanelUser getUser(int id);

	public int getNextId();
}
