package ms.idrea.umbrellapanel.api.chief;

import java.util.List;

import ms.idrea.umbrellapanel.api.core.LoadAndSaveable;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;

public interface PanelUserDatabase extends LoadAndSaveable {

	PanelUser createUser(String name, String password);

	PanelUser getUser(String name);

	void updateUser(PanelUser user);

	void deleteUser(PanelUser user);

	PanelUser getUser(int id);

	int getNextId();

	List<PanelUser> getAllUsers();
}
