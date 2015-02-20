package ms.idrea.umbrellapanel.worker;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.Getter;
import ms.idrea.umbrellapanel.api.core.PanelUser;
import ms.idrea.umbrellapanel.api.worker.UserRegistery;
import ms.idrea.umbrellapanel.api.worker.Worker;
import ms.idrea.umbrellapanel.worker.ftp.UmbrellaWritePermission;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;

public class UmbrellaUserRegistery implements UserManager, UserRegistery {

	private Worker worker;
	private ConcurrentMap<Integer, MultiUser> users = new ConcurrentHashMap<>();

	public UmbrellaUserRegistery(Worker worker) {
		this.worker = worker;
	}

	@Override
	public void update(PanelUser user) {
		update(user.getId(), user.getName(), user.getPassword());
	}

	@Override
	public void delete(PanelUser user) {
		users.remove(user.getId());
	}

	@Override
	public User getUser(int id) {
		return getUser(id);
	}

	@Override
	public PanelUser getPanelUser(int id) {
		return get(id);
	}

	@Override
	public MultiUser get(int id) {
		return users.get(id);
	}

	@Override
	public MultiUser getByName(String name) {
		for (int id : users.keySet()) {
			MultiUser o = get(id);
			if (o.getName().equals(name)) {
				return o;
			}
		}
		return null;
	}

	private void update(int id, String name, String password) {
		users.put(id, new MultiUser(id, name, password, worker));
	}

	@Override
	public User authenticate(Authentication authentication) throws AuthenticationFailedException {
		if (authentication instanceof UsernamePasswordAuthentication) {
			UsernamePasswordAuthentication usernamePasswordAuthentication = (UsernamePasswordAuthentication) authentication;
			try {
				User user = getUserByName(usernamePasswordAuthentication.getUsername());
				if (user == null) {
					return null;
				}
				// make sure password + name are correct
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
		return getByName(name);
	}

	@Override
	public boolean isAdmin(String name) throws FtpException {
		return false;
	}

	@Override
	public void delete(String name) throws FtpException {
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
			temp.add(new UmbrellaWritePermission());
			temp.add(new ConcurrentLoginPermission(20, 2));
			DEFAULT_AUTHORITIES = Collections.unmodifiableList(temp);
		}
		@Getter
		private final String homeDirectory;

		public MultiUser(int id, String name, String password, Worker worker) {
			super(id, name, password);
			homeDirectory = new File(worker.getServerManager().getGameServerDirectory(), String.valueOf(getId())).getAbsolutePath();
		}

		@Override
		public AuthorizationRequest authorize(AuthorizationRequest request) {
			boolean someoneCouldAuthorize = false;
			for (Authority authority : DEFAULT_AUTHORITIES) {
				if (authority.canAuthorize(request)) {
					someoneCouldAuthorize = true;
					request = authority.authorize(request);
					// authorization failed, return null
					if (request == null) {
						return null;
					}
				}
			}
			if (someoneCouldAuthorize) {
				return request;
			} else {
				return null;
			}
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
		public int getMaxIdleTime() {
			return 0;
		}
	}
}
