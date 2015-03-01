package ms.idrea.umbrellapanel.api.core.permissions;

import java.security.MessageDigest;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PanelUser extends Permissible {

	private final int id;
	private String name;
	private String password;

	public PanelUser(int id, String name, String password) {
		this(id, name, password, false);
	}

	public PanelUser(int id, String name, String password, boolean isHashed) {
		super();
		this.id = id;
		this.name = name;
		if (isHashed) {
			this.password = password;
		} else {
			setPassword(password);
		}
	}

	public boolean canLogin(String name, String password) {
		return this.name.equals(name) && this.password.equals(hashPassword(password));
	}

	public void setPassword(String password) {
		this.password = hashPassword(password);
	}

	private static String hashPassword(String password) {
		String digest = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(password.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder(2 * hash.length);
			for (byte b : hash) {
				sb.append(String.format("%02x", b & 0xff));
			}
			digest = sb.toString();
		} catch (Exception e) {
			new Exception("Could not hash password", e);
		}
		return digest;
	}
}
