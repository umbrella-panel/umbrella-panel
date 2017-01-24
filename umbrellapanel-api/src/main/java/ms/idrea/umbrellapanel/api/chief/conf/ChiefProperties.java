package ms.idrea.umbrellapanel.api.chief.conf;

public interface ChiefProperties {

	void load();

	void save();

	String getSharedPassword();

	void setSharedPassword(String password);

	int getNetPort();

	void setNetPort(int port);

	int getWebPort();

	void setWebPort(int port);
}
