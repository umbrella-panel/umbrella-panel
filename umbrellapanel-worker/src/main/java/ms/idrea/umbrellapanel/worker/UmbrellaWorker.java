package ms.idrea.umbrellapanel.worker;

import lombok.Getter;
import ms.idrea.umbrellapanel.worker.net.UmbrellaNetworkClient;

public class UmbrellaWorker implements Worker {
	
	@Getter
	private static UmbrellaWorker instance;
	
	public static void main(String... args) {
		instance = new UmbrellaWorker();
		instance.start();
	}
	
	
	// ---------------
	
	
	private UmbrellaNetworkClient networkClient;
	private UmbrellaServerManager serverManager;
	
	// loadup the good stuffzz
	public void start() {
		// TODO config -> loading.. now..
		serverManager = new UmbrellaServerManager();
		
		networkClient = new UmbrellaNetworkClient(null, new Runnable() {
			
			@Override
			public void run() {
				// send Hello, here are we message with shared key
				// send getServers message
			}
		});
		
		
		
	}
	
}
