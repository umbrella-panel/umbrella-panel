package ms.idrea.umbrellapanel.worker.ftp;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.core.permissions.Permission;
import ms.idrea.umbrellapanel.api.util.Utils;
import ms.idrea.umbrellapanel.api.worker.UserRegistery;
import ms.idrea.umbrellapanel.api.worker.gameserver.GameServer;
import ms.idrea.umbrellapanel.api.worker.gameserver.ServerManager;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;

@AllArgsConstructor
public class FTPUserWrapper implements UserManager {

	private UserRegistery userRegistery;
	private ServerManager serverManager;

	private WrappedUser getWrapper(String loginName) {
		String[] split = loginName.split("-");
		if (split.length != 2 || !Utils.isInteger(split[0])) {
			return null;
		}
		PanelUser user = userRegistery.getByName(split[1]);
		if (user == null) {
			return null;
		}
		int serverId = Integer.valueOf(split[0]);
		GameServer server = serverManager.getServer(serverId);
		if (server == null) {
			return null;
		}
		if (!user.hasPermission(server.getId(), Permission.FTP_ACCESS)) {
			return null;
		}
		return new WrappedUser(user, server);
	}

	@Override
	public User authenticate(Authentication authentication) throws AuthenticationFailedException {
		if (authentication instanceof UsernamePasswordAuthentication) {
			UsernamePasswordAuthentication usernamePasswordAuthentication = (UsernamePasswordAuthentication) authentication;
			WrappedUser user = getWrapper(usernamePasswordAuthentication.getUsername());
			if (user == null) {
				return null;
			}
			// make sure password + name are correct
			if (user.getUser().canLogin(user.getName(), usernamePasswordAuthentication.getPassword())) {
				return user;
			}
		}
		return null;
	}

	@Override
	public boolean doesExist(String name) throws FtpException {
		System.out.println("FTPUserWrapper.doesExist()");
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getAllUserNames() throws FtpException {
		System.out.println("FTPUserWrapper.getAllUserNames()");
		throw new UnsupportedOperationException();
	}

	@Override
	public User getUserByName(String name) throws FtpException {
		return getWrapper(name);
	}

	@Override
	public boolean isAdmin(String name) throws FtpException {
		System.out.println("FTPUserWrapper.isAdmin()");
		throw new UnsupportedOperationException();
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

	public static final List<Authority> DEFAULT_AUTHORITIES;
	static {
		List<Authority> temp = new LinkedList<>();
		temp.add(new WritePermission());
		temp.add(new ConcurrentLoginPermission(10, 10));
		DEFAULT_AUTHORITIES = Collections.unmodifiableList(temp);
	}

	public class WrappedUser implements User {

		@Getter
		private final String homeDirectory;
		private final int userId;

		public WrappedUser(PanelUser user, GameServer server) {
			if (!user.hasPermission(server.getId(), Permission.FTP_ACCESS)) {
				throw new IllegalArgumentException("User does not have permissions for that server!");
			}
			userId = user.getId();
			homeDirectory = new File(serverManager.getGameServerDirectory(), String.valueOf(server.getId())).getAbsolutePath();
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

		@Override
		public String getName() {
			return getUser().getName();
		}

		@Override
		public String getPassword() {
			return getUser().getPassword();
		}

		public PanelUser getUser() {
			return userRegistery.getPanelUser(userId);
		}
	}
}
