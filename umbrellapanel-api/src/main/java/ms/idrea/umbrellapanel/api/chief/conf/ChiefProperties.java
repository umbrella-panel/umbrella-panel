package ms.idrea.umbrellapanel.api.chief.conf;

public interface ChiefProperties {

	public void load();

	public void save();

	public String getSharedPassword();

	public void setSharedPassword(String password);

	public int getNetPort();

	public void setNetPort(int port);

	public int getWebPort();

	public void setWebPort(int port);
}
