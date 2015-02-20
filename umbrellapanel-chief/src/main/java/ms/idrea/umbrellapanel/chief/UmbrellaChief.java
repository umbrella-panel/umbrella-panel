package ms.idrea.umbrellapanel.chief;

import java.util.Arrays;
import java.util.Scanner;

import lombok.Getter;

import ms.idrea.umbrellapanel.api.chief.Chief;
import ms.idrea.umbrellapanel.api.chief.PanelUserDatabase;
import ms.idrea.umbrellapanel.api.chief.WorkerManager;
import ms.idrea.umbrellapanel.api.chief.gameserver.GameServer;
import ms.idrea.umbrellapanel.api.chief.gameserver.ServerManager;
import ms.idrea.umbrellapanel.api.chief.net.NetworkServer;
import ms.idrea.umbrellapanel.api.core.PanelUser;
import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.chief.gameserver.UmbrellaServerManager;
import ms.idrea.umbrellapanel.chief.net.UmbrellaNetworkServer;

@Getter
public class UmbrellaChief implements Chief {

	@Getter
	private static Chief instance;

	public static void main(String... args) {
		instance = new UmbrellaChief();
		instance.start();
	}

	// ---------------
	private NetworkServer networkServer;
	private PanelUserDatabase panelUserDatabase;
	private ServerManager serverManager;
	private WorkerManager workerManager;

	@Override
	public void start() {
		workerManager = new UmbrellaWorkerManager();
		networkServer = new UmbrellaNetworkServer(workerManager);
		panelUserDatabase = new UmbrellaPanelUserDatabase(networkServer);
		serverManager = new UmbrellaServerManager(panelUserDatabase, workerManager);
		/*
			webServer = new UmbrellaWebServer();
			try {
				webServer.start(new Address("*", 80));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		*/
		Scanner scanner = new Scanner(System.in);
		String line;
		while ((line = scanner.nextLine()) != null) {
			if (line.equalsIgnoreCase("exit")) {
				break;
			}
			processCommand(line);
		}
		System.out.println("Exit..");
		scanner.close();
		networkServer.shutdown();
	}

	private void processCommand(String cmd) {
		try {
			String[] temp = cmd.split(" ");
			String base = temp[0];
			String[] args = Arrays.copyOfRange(temp, 1, temp.length);
			
			if (base.equalsIgnoreCase("adduser")) {
				PanelUser user = panelUserDatabase.createUser(args[0], args[1]);
				System.out.println(user);
			} else if (base.equalsIgnoreCase("createserver")) {
				PanelUser user = panelUserDatabase.getUser(Integer.valueOf(args[0]));
				GameServer server = serverManager.createServer(user, new Address(args[1], Integer.valueOf(args[2])), "java -jar server.jar", Integer.valueOf(args[3]));
				System.out.println(server);
			} else if (base.equalsIgnoreCase("manageserver")) {
				GameServer server = serverManager.getServer(Integer.valueOf(args[1]));
				switch (args[0]) {
					case "start":
						server.start();
						break;
					case "force-stop":
						server.forceStop();
						break;
					case "delete":
						serverManager.deleteServer(server);
						break;
					default:
						break;
				}
				System.out.println("OK");
			} else if (base.equalsIgnoreCase("sendCmd")) {
				GameServer server = serverManager.getServer(Integer.valueOf(args[0]));
				StringBuilder s = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					s.append(args[i]).append(' ');
				}
				server.dispatchCommand(s.substring(0, s.length() - 1));
				System.out.println("OK");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	/*	
		PanelUser user = panelUserDatabase.createUser("paul", "icanhaz");
		GameServer gameServer = serverManager.createServer(user, new Address("0.0.0.0", 25565), "java -jar server.jar", ((List<Worker>) workerManager.getAllWorkers()).get(0).getId()); // \o/
		gameServer.start();
		try {
			Thread.sleep(5000 * 10);
		} catch (Exception e) {
			
		}
		gameServer.dispatchCommand("stop");
		System.out.println(gameServer.toString());
		*/
	}
}
