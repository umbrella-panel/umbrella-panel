package ms.idrea.umbrellapanel.api.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.logging.Logger;

public abstract class UmbrellaProperties {

	public static enum Key {
		SHARED_PASSWORD,
		NET_PORT,
		WORKER_ID,
		CHIEF_HOST,
		CHIEF_PORT,
		WEB_PORT;

		public String toPath() {
			return toString().toLowerCase();
		}
	}

	private Properties properties;
	private String name;
	private Logger logger;
	private File file;

	public UmbrellaProperties(String name, File file, Logger logger) {
		this.name = name;
		this.file = file;
		this.logger = logger;
	}

	public void load() {
		properties = new Properties();
		try {
			boolean newFile = false;
			if (!file.exists()) {
				file.createNewFile();
				newFile = true;
			}
			FileInputStream stream = new FileInputStream(file);
			properties.load(stream);
			if (newFile) {
				onNewPropertiesCreated();
			}
			stream.close();
		} catch (Exception e) {
			throw new RuntimeException("Could not load properties!", e);
		}
	}

	public abstract void onNewPropertiesCreated();

	public void save() {
		try {
			FileOutputStream stream = new FileOutputStream(file);
			properties.store(stream, name);
			stream.close();
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}
	}

	protected String getString(Key key, String defaultValue) {
		String value = (String) properties.get(key.toPath());
		return value != null ? value : defaultValue;
	}

	protected int getInt(Key key, int defaultValue) {
		try {
			return Integer.valueOf(getString(key, null));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	protected void set(Key key, Object value) {
		properties.setProperty(key.toPath(), value.toString());
	}
}
