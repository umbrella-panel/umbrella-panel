package ms.idrea.umbrellapanel.worker.conf;

import java.io.File;

import ms.idrea.umbrellapanel.api.core.UmbrellaProperties;
import ms.idrea.umbrellapanel.api.worker.Worker;
import ms.idrea.umbrellapanel.api.worker.conf.WorkerProperties;

public class UmbrellaWorkerProperties extends UmbrellaProperties implements WorkerProperties {

	public UmbrellaWorkerProperties(Worker worker) {
		super("UmbrellaWorker", new File("UmbrellaWorker.conf"), worker.getLogger());
	}

	@Override
	public void onNewPropertiesCreated() {
		setSharedPassword("$SHAREDPASSWORD$");
		setWorkerId(-1);
		setChiefHost("localhost");
		setChiefPort(35886);
		setPassivePort("123");
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
	public int getWorkerId() {
		return getInt(Key.WORKER_ID, -1);
	}

	@Override
	public void setWorkerId(int id) {
		set(Key.WORKER_ID, id);
	}

	@Override
	public String getChiefHost() {
		return getString(Key.CHIEF_HOST, "localhost");
	}

	@Override
	public void setChiefHost(String host) {
		set(Key.CHIEF_HOST, host);
	}

	@Override
	public int getChiefPort() {
		return getInt(Key.CHIEF_PORT, 35886);
	}

	@Override
	public void setChiefPort(int port) {
		set(Key.CHIEF_PORT, port);
	}
	
	@Override
	public String getPassivePort() {
		return getString(Key.PASSIVE_PORT, "123");
	}
	
	@Override
	public void setPassivePort(String port) {
		set(Key.PASSIVE_PORT, port);
	}
}
