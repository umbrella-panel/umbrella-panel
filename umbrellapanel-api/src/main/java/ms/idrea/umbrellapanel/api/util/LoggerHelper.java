package ms.idrea.umbrellapanel.api.util;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggerHelper {

	private static final SimpleDateFormat COMMON_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	private static final Formatter COMMON_FORMATER = new Formatter() {

		@Override
		public String format(LogRecord record) {
			return new StringBuilder().append('[').append(COMMON_DATE_FORMAT.format(new Date())).append(' ').append(Thread.currentThread().getName()).append('/').append(record.getLevel()).append("] ").append(record.getMessage()).append("\n").toString();
		}
	};

	public static Logger getCommonLogger(String name, Level consoleLevel, String logFile, Level fileLevel) {
		Logger log = Logger.getLogger(name);
		setConsoleLogger(log, consoleLevel);
		setFileLogger(log, fileLevel, logFile);
		PrintStream ps = new PrintStream(new LogOutputStream(log, Level.SEVERE), true);
		System.setErr(ps);
		System.setOut(ps);
		return log;
	}

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
		Handler console = new UmbrellaConsoleHandler();
		console.setFormatter(COMMON_FORMATER);
		console.setLevel(level);
		logger.addHandler(console);
		logger.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
	}

	public static void worker(Logger log, Level level) {
		umbrella(log, level);
		log.log(level, "__          __        _             ");
		log.log(level, "\\ \\        / /       | |            ");
		log.log(level, " \\ \\  /\\  / /__  _ __| | _____ _ __ ");
		log.log(level, "  \\ \\/  \\/ / _ \\| '__| |/ / _ \\ '__|");
		log.log(level, "   \\  /\\  / (_) | |  |   <  __/ |   ");
		log.log(level, "    \\/  \\/ \\___/|_|  |_|\\_\\___|_|   ");
		log.log(level, "");
	}

	public static void chief(Logger log, Level level) {
		umbrella(log, level);
		log.log(level, "   _____ _     _       __ ");
		log.log(level, "  / ____| |   (_)     / _|");
		log.log(level, " | |    | |__  _  ___| |_ ");
		log.log(level, " | |    | '_ \\| |/ _ \\  _|");
		log.log(level, " | |____| | | | |  __/ |  ");
		log.log(level, "  \\_____|_| |_|_|\\___|_|  ");
		log.log(level, "");
	}

	public static void umbrella(Logger log, Level level) {
		log.log(level, "");
		log.log(level, "                 MM  :::::::::....::::::::  MM");
		log.log(level, "               MMMM  :::::::::::::::::::  MMMM");
		log.log(level, "              MMMMMM  :::::::::::::::::  MMMMMMM");
		log.log(level, "            MMMMMMMMM  :::::::::::::::  MMMMMMMMM");
		log.log(level, "          MMMMMMMMMMMM  :::::::::::::  MMMMMMMMMMMM");
		log.log(level, "        MMMMMMMMMMMMMM   :::::::::::   MMMMMMMMMMMMMM");
		log.log(level, "     MMMMMMMMMMMMMMMMMM   ::::::::::  MMMMMMMMMMMMMMMMMM");
		log.log(level, "  MMMMMMMMMMMMMMMMMMMMMM   ::::::::  MMMMMMMMMMMMMMMMMMMMM");
		log.log(level, "     MMMMMMMMMMMMMMMMMMMM   ::::::  MMMMMMMMMMMMMMMMNMMMMM");
		log.log(level, "  ....    MMMMMMMMMMMMMMM   :::::  MMMMMMMMMMMMMMMMMM   ..::");
		log.log(level, "   :::::..     MMMMMMMMMMM   :::   MMMMMMMMMMMMMM  ..::::::");
		log.log(level, "   :::::::::...   MMMMMMMMM   :   MMMNMMMMM   ...::::::::::");
		log.log(level, "   ::::::::::::::...   MMMMM     MMMMM   ...::::::::::::::");
		log.log(level, "   :::::::::::::::::::..   M     M     :::::::::::::::::::");
		log.log(level, "   :::::::::::::::::::::.           .:::::::::::::::::::::");
		log.log(level, "   :::::::::::::::::      MM     M        ::::::::::::::::");
		log.log(level, "   ::::::::::::      MMMMMMM  .  MMMMMMM      ::::::::::::.");
		log.log(level, "   ::::::::      MMMMMMMMMM  .:.  MMMMMMMMMM     ::::::::::");
		log.log(level, "  .:::      MMMMMMMMMMMMMM  .:::.  MMMMMMMMMMMMM      ::::::");
		log.log(level, "        MMMMMMMMMMMMMMMMMM  :::::.  MMMMMMMMMMMMMMMMM     ");
		log.log(level, "   MMMMMMMMMMMMMMMMMMMMMM  :::::::.  MMMMMMMMMMMMMMMMMMMMMM");
		log.log(level, "    MMMMMMMMMMMMMMMMMMMM  :::::::::  MMMMMMMMMMMMMMMMMMMMMM");
		log.log(level, "      MMMMMMMMMMMMMMMMM   ::::::::::  MMMMMMMMMMMMMMMMM");
		log.log(level, "         MMMMMMMMMMMMM   :::::::::::.  MMMMMMMMMMMMM");
		log.log(level, "           MMMMMMMMMM  .:::::::::::::.  MMMMMMMMMM");
		log.log(level, "             MMMMMMMM .:::::::::::::::.  MMMMMMM");
		log.log(level, "              MMMMMM  :::::::::::::::::.  MMMMM");
		log.log(level, "                MMM  :::::::::::::::::::   MMM");
		log.log(level, "                 M  ::::....     ...:::::  MM");
		log.log(level, "");
	}
}
