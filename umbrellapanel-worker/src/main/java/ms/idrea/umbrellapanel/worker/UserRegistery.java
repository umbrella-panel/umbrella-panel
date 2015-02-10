package ms.idrea.umbrellapanel.worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ms.idrea.umbrellapanel.core.PanelUser;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginRequest;
import org.apache.ftpserver.usermanager.impl.WritePermission;

public class UserRegistery implements UserManager {

	private ConcurrentMap<String, MultiUser> users = new ConcurrentHashMap<>();

	public void update(PanelUser user) {
		update(user.getName(), user.getPassword());
	}

	public void update(User user) {
		update(user.getName(), user.getPassword());
	}

	public User getUser(PanelUser user) {
		update(user.getName(), user.getPassword());
		return (User) get(user.getName());
	}

	public PanelUser getPanelUser(User user) {
		update(user.getName(), user.getPassword());
		return (PanelUser) get(user.getName());
	}

	public MultiUser get(String name) {
		return users.get(name);
	}

	private void update(String name, String password) {
		users.put(name, new MultiUser(name, password));
	}

	@Override
	public User authenticate(Authentication authentication) throws AuthenticationFailedException {
		System.out.println(users);
		System.out.println("UmbrellaFTPServer.start().new UserManager() {...}.authenticate()");
		System.out.println(authentication.toString());
		if (authentication instanceof UsernamePasswordAuthentication) {
			UsernamePasswordAuthentication usernamePasswordAuthentication = (UsernamePasswordAuthentication) authentication;
			try {
				User user = getUserByName(usernamePasswordAuthentication.getUsername());
				if (user == null) {
					return null;
				}
				if (user.getPassword().equals(usernamePasswordAuthentication.getPassword()) && user.getName().equals(usernamePasswordAuthentication.getUsername())) {
					return user;
				}
			} catch (FtpException e) {
			}
		}
		return null;
	}

	@Override
	public boolean doesExist(String name) throws FtpException {
		return users.get(name) != null;
	}

	@Override
	public String[] getAllUserNames() throws FtpException {
		return users.keySet().toArray(new String[users.keySet().size()]);
	}

	@Override
	public User getUserByName(String name) throws FtpException {
		return get(name);
	}

	@Override
	public boolean isAdmin(String name) throws FtpException {
		return false;
	}

	@Override
	public void delete(String arg0) throws FtpException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void save(User name) throws FtpException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getAdminName() throws FtpException {
		throw new UnsupportedOperationException();
	}

	public static class MultiUser extends PanelUser implements User {

		public static final List<Authority> DEFAULT_AUTHORITIES;
		static {
			List<Authority> temp = new LinkedList<>();
			// TODO MORE
			temp.add(new WritePermission());
			DEFAULT_AUTHORITIES = Collections.unmodifiableList(temp);
		}

		public MultiUser(String name, String password) {
			super(name, password);
		}

		@Override
		public AuthorizationRequest authorize(AuthorizationRequest request) {
			if (request instanceof ConcurrentLoginRequest) {
				return request;
			}
			// TODO
			return null;
		}

		@Override
		public List<Authority> getAuthorities() {
			return DEFAULT_AUTHORITIES;
		}

		@Override
		public List<Authority> getAuthorities(Class<? extends Authority> clazz) {
			List<Authority> temp = new ArrayList<>(DEFAULT_AUTHORITIES.size());
			for (Authority authority : DEFAULT_AUTHORITIES) {
				if (authority.getClass().equals(clazz)) {
					temp.add(authority);
				}
			}
			return temp;
		}

		@Override
		public boolean getEnabled() {
			return true;
		}

		@Override
		public String getHomeDirectory() {
			return ""; // TODO
		}

		@Override
		public int getMaxIdleTime() {
			return 0;
		}
	}
}
