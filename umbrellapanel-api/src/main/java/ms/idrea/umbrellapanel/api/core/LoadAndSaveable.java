package ms.idrea.umbrellapanel.api.core;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public interface LoadAndSaveable {

	public void load(Reader in) throws IOException;
	
	public void save(Writer out) throws IOException;
}
