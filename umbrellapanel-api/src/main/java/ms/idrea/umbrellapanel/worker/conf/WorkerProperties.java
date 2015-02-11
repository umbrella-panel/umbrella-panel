package ms.idrea.umbrellapanel.worker.conf;

public interface WorkerProperties {

	public void load();

	public void save();

	public String getSharedPassword();

	public void setSharedPassword(String password);

	public int getWorkerId();

	public void setWorkerId(int id);
}
