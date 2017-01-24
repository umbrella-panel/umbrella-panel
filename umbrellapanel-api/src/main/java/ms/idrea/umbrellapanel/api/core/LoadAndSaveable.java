package ms.idrea.umbrellapanel.api.core;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public interface LoadAndSaveable {

	void load(Reader in) throws IOException;

	void save(Writer out) throws IOException;
}
