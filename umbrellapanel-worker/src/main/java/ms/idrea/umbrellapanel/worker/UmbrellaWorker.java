package ms.idrea.umbrellapanel.worker;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;

import ms.idrea.umbrellapanel.core.net.messages.WorkerMessage;
import ms.idrea.umbrellapanel.core.net.messages.WorkerMessage.Action;
import ms.idrea.umbrellapanel.util.LoggerHelper;
import ms.idrea.umbrellapanel.worker.conf.UmbrellaWorkerProperties;
import ms.idrea.umbrellapanel.worker.conf.WorkerProperties;
import ms.idrea.umbrellapanel.worker.gameserver.UmbrellaServerManager;
import ms.idrea.umbrellapanel.worker.net.NetworkClient;
import ms.idrea.umbrellapanel.worker.net.UmbrellaNetworkClient;

@Getter
public class UmbrellaWorker implements Worker {
	
	@Getter
	private static Worker instance;

	public static void main(String... args) {
		instance = new UmbrellaWorker();
		instance.start();
	}
	
	// ---------------
	
	private Logger logger;
	private NetworkClient networkClient;
	private ServerManager serverManager;
	private LogHandler logHandler;
	private UserRegistery userRegistery;
	private FTPServer ftpServer;
	private WorkerProperties workerProperties;
	
	private boolean isStopping = false;
	
	// loadup the good stuffzz
	public void start() {
		logger = Logger.getLogger("UmbrellaWorker");
		LoggerHelper.setConsoleLogger(logger, Level.FINEST); // -> INFO
		LoggerHelper.setFileLogger(logger, Level.ALL, "UmbrellaWorker.log");
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {
				shutdown();
			}
		}));
		
		workerProperties = new UmbrellaWorkerProperties(this);
		workerProperties.load();
		
		userRegistery = new UmbrellaUserRegistery(this);
		ftpServer = new UmbrellaFTPServer(this);
		ftpServer.start();
		logHandler = new UmbrellaLogHandler(this);
		serverManager = new UmbrellaServerManager();
		networkClient = new UmbrellaNetworkClient(new InetSocketAddress("localhost", 30000), new Runnable() {
			
			@Override
			public void run() {
				if (workerProperties.getWorkerId() == -1) {
					networkClient.send(new WorkerMessage(Action.REGISTER, workerProperties.getSharedPassword()));
				} else {
					networkClient.send(new WorkerMessage(Action.STARTED, workerProperties.getWorkerId(), workerProperties.getSharedPassword()));
				}
				// the umbrella server should now send all the data.
			}
		});
		
		Scanner scanner = new Scanner(System.in);
		String input;
		
		while ((input = scanner.next()) != null) {
			if (input.equalsIgnoreCase("exit")) {
				break;
			}
			logger.info("Type \"exit\" to exit the program!");
		}
		
		scanner.close();
		shutdown();
	}
	
	public void shutdown() {
		if (isStopping) {
			return;
		}
		isStopping = true;
		logger.info("Worker is stopping!");
		workerProperties.save();
		ftpServer.shutdown();
		networkClient.shutdown();
		serverManager.shutdown();
	}	
}
