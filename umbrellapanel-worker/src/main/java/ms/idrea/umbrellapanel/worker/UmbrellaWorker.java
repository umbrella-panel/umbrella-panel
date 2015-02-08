package ms.idrea.umbrellapanel.worker;

import java.net.InetSocketAddress;
import java.util.Scanner;

import lombok.Getter;
import ms.idrea.umbrellapanel.worker.gameserver.UmbrellaServerManager;
import ms.idrea.umbrellapanel.worker.net.NetworkClient;
import ms.idrea.umbrellapanel.worker.net.UmbrellaNetworkClient;

public class UmbrellaWorker implements Worker {
	
	@Getter
	private static Worker instance;
	
	public static void main(String... args) {
		instance = new UmbrellaWorker();
		instance.start();
	}
	
	
	// ---------------
	
	@Getter
	private NetworkClient networkClient;
	@Getter
	private ServerManager serverManager;
	@Getter
	private LogHandler logHandler;
	
	private boolean isStopping = false;
	
	// loadup the good stuffzz
	public void start() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {
				shutdown();
			}
		}));
		
		// TODO config -> loading.. now..
		logHandler = new UmbrellaLogHandler(this);
		serverManager = new UmbrellaServerManager();
		networkClient = new UmbrellaNetworkClient(new InetSocketAddress("localhost", 30000), new Runnable() {
			
			@Override
			public void run() {
				// send Hello, here are we message with shared key
				// send getServers message
			}
		});
		
		Scanner scanner = new Scanner(System.in);
		String input;
		
		while ((input = scanner.next()) != null) {
			if (input.equalsIgnoreCase("exit")) {
				break;
			}
			System.out.println("Type \"exit\" to exit the program!");
		}
		
		scanner.close();
		shutdown();
	}
	
	public void shutdown() {
		if (isStopping) {
			return;
		}
		isStopping = true;
		System.out.println("Worker is stopping!");
		networkClient.shutdown();
		serverManager.shutdown();
	}	
}
