package ms.idrea.umbrellapanel.chief;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import ms.idrea.umbrellapanel.api.core.LoadAndSaveable;

public class FileManager {

	private Map<LoadAndSaveable, File> files = new HashMap<>();

	public void register(LoadAndSaveable able, String file) {
		files.put(able, new File("data", file + ".dat"));
	}

	public void load() {
		for (LoadAndSaveable able : files.keySet()) {
			try {
				File file = files.get(able);
				if (file.getParent() != null) {
					new File(file.getParent()).mkdirs();
				}
				if (!file.exists()) {
					file.createNewFile();
				}
				Reader in = new FileReader(file);
				able.load(in);
				in.close();
			} catch (Exception e) {
				throw new RuntimeException("Unable to load " + able.getClass(), e);
			}
		}
	}

	public void save() {
		for (LoadAndSaveable able : files.keySet()) {
			try {
				Writer out = new FileWriter(files.get(able));
				able.save(out);
				out.flush();
				out.close();
			} catch (Exception e) {
				throw new RuntimeException("Unable to save " + able.getClass(), e);
			}
		}
	}
}
