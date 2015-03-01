package ms.idrea.umbrellapanel.api.chief;

import java.util.List;

import ms.idrea.umbrellapanel.api.core.LoadAndSaveable;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;

public interface PanelUserDatabase extends LoadAndSaveable {

	public PanelUser createUser(String name, String password);

	public PanelUser getUser(String name);

	public void updateUser(PanelUser user);

	public void deleteUser(PanelUser user);

	public PanelUser getUser(int id);

	public int getNextId();

	public List<PanelUser> getAllUsers();
}