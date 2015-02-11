package ms.idrea.umbrellapanel.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggerHelper {

	private static final SimpleDateFormat COMMON_DATE_FORMAT = new SimpleDateFormat("[dd-MM-yyyy][HH:mm:ss]");;
	private static final Formatter COMMON_FORMATER = new Formatter() {

		public String format(LogRecord record) {
			return new StringBuilder().append(COMMON_DATE_FORMAT.format(new Date())).append('[').append(Thread.currentThread().getName()).append('/').append(record.getLevel()).append("] ").append(record.getMessage()).append("\n").toString();
		}
	};
	
	public static void setFileLogger(Logger logger, Level level, String file) {
		try {
			FileHandler fileHandler = new FileHandler(file);
			fileHandler.setFormatter(COMMON_FORMATER);
			fileHandler.setLevel(level);
			logger.addHandler(fileHandler);
			logger.setLevel(Level.ALL);
			logger.setUseParentHandlers(false);
		} catch (Exception e) {
			System.err.println("Error");
		}
	}

	public static void setConsoleLogger(Logger logger, Level level) {
		Handler console = new ConsoleHandler();
		console.setFormatter(COMMON_FORMATER);
		console.setLevel(level);
		logger.addHandler(console);
		logger.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
	}
}
