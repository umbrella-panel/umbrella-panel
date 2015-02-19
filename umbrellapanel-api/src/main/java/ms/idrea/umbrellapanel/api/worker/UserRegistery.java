package ms.idrea.umbrellapanel.api.worker;

import ms.idrea.umbrellapanel.api.core.PanelUser;

import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;

public interface UserRegistery extends UserManager {

	public void delete(PanelUser user);

	public void update(PanelUser user);

	public User getUser(int id);

	public PanelUser getPanelUser(int id);

	public Object get(int id);
	
	public PanelUser getByName(String name);
}
