package ms.idrea.umbrellapanel.api.worker.conf;

public interface WorkerProperties {

	public void load();

	public void save();

	public String getSharedPassword();

	public void setSharedPassword(String password);

	public int getWorkerId();

	public void setWorkerId(int id);

	public String getChiefHost();

	public void setChiefHost(String host);

	public int getChiefPort();

	public void setChiefPort(int port);
}
