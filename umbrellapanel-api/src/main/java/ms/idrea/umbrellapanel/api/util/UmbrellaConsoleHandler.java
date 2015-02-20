package ms.idrea.umbrellapanel.api.util;

import java.util.logging.ConsoleHandler;

public class UmbrellaConsoleHandler extends ConsoleHandler {

	public UmbrellaConsoleHandler() {
		super();
		setOutputStream(System.out);
	}
}
