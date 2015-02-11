package ms.idrea.umbrellapanel.worker;

import ms.idrea.umbrellapanel.core.PanelUser;

import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;

public interface UserRegistery extends UserManager {

	public void update(PanelUser user);

	public User getUser(int id);

	public PanelUser getPanelUser(int id);

	public Object get(int id);
	
	public PanelUser getByName(String name);
}
