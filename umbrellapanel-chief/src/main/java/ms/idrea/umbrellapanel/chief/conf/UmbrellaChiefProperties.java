package ms.idrea.umbrellapanel.chief.conf;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;

import ms.idrea.umbrellapanel.api.chief.Chief;
import ms.idrea.umbrellapanel.api.chief.conf.ChiefProperties;
import ms.idrea.umbrellapanel.api.core.UmbrellaProperties;

public class UmbrellaChiefProperties extends UmbrellaProperties implements ChiefProperties {

	public UmbrellaChiefProperties(Chief chief) {
		super("UmbrellaChief", new File("UmbrellaChief.conf"), chief.getLogger());
	}

	@Override
	public void onNewPropertiesCreated() {
		setSharedPassword(new BigInteger(130, new SecureRandom()).toString(32));
		setNetPort(35886);
	}

	@Override
	public String getSharedPassword() {
		return getString(Key.SHARED_PASSWORD, "");
	}

	@Override
	public void setSharedPassword(String password) {
		set(Key.SHARED_PASSWORD, password);
	}

	@Override
	public int getNetPort() {
		return getInt(Key.NET_PORT, 35886);
	}

	@Override
	public void setNetPort(int port) {
		set(Key.NET_PORT, port);
	}
}
