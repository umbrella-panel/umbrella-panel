package ms.idrea.umbrellapanel.worker;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.ftpserver.ftplet.UserManager;

import lombok.Getter;

import ms.idrea.umbrellapanel.api.util.LoggerHelper;
import ms.idrea.umbrellapanel.api.worker.LogHandler;
import ms.idrea.umbrellapanel.api.worker.UserRegistery;
import ms.idrea.umbrellapanel.api.worker.Worker;
import ms.idrea.umbrellapanel.api.worker.conf.WorkerProperties;
import ms.idrea.umbrellapanel.api.worker.ftp.FTPServer;
import ms.idrea.umbrellapanel.api.worker.gameserver.ServerManager;
import ms.idrea.umbrellapanel.net.messages.WorkerMessage;
import ms.idrea.umbrellapanel.net.messages.WorkerMessage.Action;
import ms.idrea.umbrellapanel.worker.conf.UmbrellaWorkerProperties;
import ms.idrea.umbrellapanel.worker.ftp.FTPUserWrapper;
import ms.idrea.umbrellapanel.worker.ftp.UmbrellaFTPServer;
import ms.idrea.umbrellapanel.worker.gameserver.UmbrellaServerManager;
import ms.idrea.umbrellapanel.worker.net.TransportClient;

@Getter
public class UmbrellaWorker implements Worker {

	@Getter
	private static Worker instance;

	public static void main(String... args) {
		instance = new UmbrellaWorker();
		instance.start();
		instance.enableConsole();
	}

	// ---------------
	private Logger logger;
	private TransportClient networkClient;
	private ServerManager serverManager;
	private LogHandler logHandler;
	private UserRegistery userRegistery;
	private FTPServer ftpServer;
	private WorkerProperties workerProperties;
	private UserManager ftpUserWrapper;
	private boolean isRunning;

	// loadup the good stuffzz
	@Override
	public void start() {
		isRunning = true;
		logger = LoggerHelper.getCommonLogger("UmbrellaWorker", Level.FINEST, "UmbrellaWorker.log", Level.ALL);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				shutdown();
			}
		}));
		LoggerHelper.worker(logger, Level.INFO);
		workerProperties = new UmbrellaWorkerProperties(this);
		workerProperties.load();
		userRegistery = new UmbrellaUserRegistery();
		serverManager = new UmbrellaServerManager(this);
		ftpUserWrapper = new FTPUserWrapper(userRegistery, serverManager);
		ftpServer = new UmbrellaFTPServer(this);
		try {
			ftpServer.start();
		} catch (Exception e) {
			new Exception("Failed to start ftp server!", e).printStackTrace();
		}
		logHandler = new UmbrellaLogHandler(this);
		networkClient = new TransportClient(this,
				new InetSocketAddress(workerProperties.getChiefHost(), workerProperties.getChiefPort()),
				new Runnable() {

					@Override
					public void run() {
						if (workerProperties.getSharedPassword().equals("$SHAREDPASSWORD$")) {
							logger.warning("Enter the shared password of the chief. DONT USE \"$SHAREDPASSWORD$\"!");
							System.exit(0);
						}
						if (workerProperties.getWorkerId() == -1) {
							networkClient
									.send(new WorkerMessage(Action.REGISTER, workerProperties.getSharedPassword()));
						} else {
							networkClient.send(new WorkerMessage(Action.STARTED, workerProperties.getWorkerId(),
									workerProperties.getSharedPassword()));
						}
						// the umbrella server should now send all the data.
					}
				});
		try {
			networkClient.connect();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void enableConsole() {
		Scanner scanner = new Scanner(System.in);
		String input;
		while ((input = scanner.next()) != null) {
			if (input.equalsIgnoreCase("exit")) {
				break;
			}
			logger.info("Type \"exit\" to exit the program!");
			System.out.println(serverManager);
		}
		scanner.close();
		shutdown();
	}

	@Override
	public void shutdown() {
		if (!isRunning) {
			return;
		}
		isRunning = false;
		logger.info("Worker is stopping!");
		serverManager.shutdown();
		logHandler.shutdown();
		workerProperties.save();
		ftpServer.shutdown();
		try {
			networkClient.send(new WorkerMessage(Action.STOPPED, workerProperties.getWorkerId(),
					workerProperties.getSharedPassword()));
		} catch (Exception e) {
			new Exception("Could not send stopped workermessage!", e).printStackTrace();
		}
		networkClient.shutdown();
	}

	@Override
	public UserManager getFTPUserWrapper() {
		return ftpUserWrapper;
	}
}
