package ms.idrea.umbrellapanel.worker.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import ms.idrea.umbrellapanel.worker.Worker;

public class UmbrellaWorkerProperties implements WorkerProperties {

	public static enum Key {
		SHARED_PASSWORD,
		WORKER_ID;

		public String toPath() {
			return toString().toLowerCase();
		}
	}
	
	private Properties properties;
	private File file;
	private Worker worker;
	
	public UmbrellaWorkerProperties(Worker worker) {
		this.worker = worker;
	}

	@Override
	public void load() {
		properties = new Properties();
		try {
			file = new File("worker.properties");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileInputStream stream = new FileInputStream(file);
			properties.load(stream);
			stream.close();
		} catch (Exception e) {
			throw new RuntimeException("Could not load properties!", e);
		}
	}

	@Override
	public void save() {
		try {
			FileOutputStream stream = new FileOutputStream(file);
			properties.store(stream, "UmbrellaWorker");
			stream.close();
		} catch (Exception e) {
			worker.getLogger().warning(e.getMessage());
		}
	}
	
	private String getString(Key key, String defaultValue) {
		String value = (String) properties.get(key.toPath());
		return value != null ? value : defaultValue;
	}

	private int getInt(Key key, int defaultValue) {
		try {
			return Integer.valueOf(getString(key, null));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	private void set(Key key, Object value) {
		properties.setProperty(key.toPath(), value.toString());
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
}
