package ms.idrea.umbrellapanel.api.worker.conf;

public interface WorkerProperties {

	void load();

	void save();

	String getSharedPassword();

	void setSharedPassword(String password);

	int getWorkerId();

	void setWorkerId(int id);

	String getChiefHost();

	void setChiefHost(String host);

	int getChiefPort();

	void setChiefPort(int port);
}
