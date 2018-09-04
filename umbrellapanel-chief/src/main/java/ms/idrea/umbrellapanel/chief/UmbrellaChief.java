package ms.idrea.umbrellapanel.chief;

import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;

import ms.idrea.umbrellapanel.api.chief.Chief;
import ms.idrea.umbrellapanel.api.chief.PanelUserDatabase;
import ms.idrea.umbrellapanel.api.chief.WorkerManager;
import ms.idrea.umbrellapanel.api.chief.conf.ChiefProperties;
import ms.idrea.umbrellapanel.api.chief.net.NetworkServer;
import ms.idrea.umbrellapanel.api.chief.net.RunningWorker;
import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.gameserver.ManagedServer;
import ms.idrea.umbrellapanel.api.gameserver.MultiInstanceServer;
import ms.idrea.umbrellapanel.api.gameserver.ServerInstance;
import ms.idrea.umbrellapanel.api.gameserver.ServerManager;
import ms.idrea.umbrellapanel.api.gameserver.SingleInstanceServer;
import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.api.util.LoggerHelper;
import ms.idrea.umbrellapanel.api.worker.gameserver.GameServer;
import ms.idrea.umbrellapanel.chief.conf.UmbrellaChiefProperties;
import ms.idrea.umbrellapanel.chief.gameserver.UmbrellaServerManager;
import ms.idrea.umbrellapanel.chief.net.UmbrellaNetworkServer;
import ms.idrea.umbrellapanel.chief.webapi.UmbrellaEndPointManager;

@Getter
public class UmbrellaChief implements Chief {

	@Getter
	private static Chief instance;

	public static Chief createInstance() {
		instance = new UmbrellaChief();
		return instance;
	}

	public static void main(String... args) {
		createInstance();
		instance.start();
		instance.enableConsole();
	}

	private UmbrellaChief() {
	}

	private ChiefProperties chiefProperties;
	private FileManager fileManager;
	private NetworkServer networkServer;
	private PanelUserDatabase panelUserDatabase;
	private ServerManager serverManager;
	private WorkerManager workerManager;
	private EndPointManager webapi;
	private Logger logger;
	private boolean isRunning;

	@Override
	public void start() {
		isRunning = true;
		logger = LoggerHelper.getCommonLogger("UmbrellaChief", Level.FINEST, "UmbrellaChief.log", Level.ALL);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				shutdown();
			}
		}));
		LoggerHelper.chief(logger, Level.INFO);
		chiefProperties = new UmbrellaChiefProperties(this);
		chiefProperties.load();
		fileManager = new FileManager();
		workerManager = new UmbrellaWorkerManager();
		networkServer = new UmbrellaNetworkServer(workerManager, chiefProperties.getNetPort());
		panelUserDatabase = new UmbrellaPanelUserDatabase(networkServer);
		serverManager = new UmbrellaServerManager(panelUserDatabase, workerManager);
		try {
			webapi = new UmbrellaEndPointManager(this);
		} catch (Exception e) {
			new Exception("Failed to start EndPointManager", e).printStackTrace();
		}
		//
		fileManager.register(serverManager, "servers");
		fileManager.register(workerManager, "workers");
		fileManager.register(panelUserDatabase, "users");
		fileManager.load();
	}

	public void enableConsole() {
		Scanner scanner = new Scanner(System.in);
		String line;
		while ((line = scanner.nextLine()) != null) {
			if (line.equalsIgnoreCase("exit")) {
				break;
			}
			processCommand(line);
		}
		logger.info("Exiting...");
		scanner.close();
		shutdown();
	}

	public void shutdown() {
		if (!isRunning) {
			return;
		}
		isRunning = false;
		if (webapi != null) {
			webapi.shutdown();
		}
		networkServer.shutdown();
		chiefProperties.save();
		fileManager.save();
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
				GameServer server = serverManager.createSingleInstanceServer(new Address(args[0], Integer.valueOf(args[1])), "java -jar server.jar", Integer.valueOf(args[2]));
				System.out.println(server);
			} else if (base.equalsIgnoreCase("createmultiserver")) {
				GameServer server = serverManager.createMultiInstanceServer("java -jar server.jar", Integer.valueOf(args[0]));
				System.out.println(server);
			} else if (base.equalsIgnoreCase("createinstance")) {
				MultiInstanceServer server = (MultiInstanceServer) serverManager.getServer(Integer.valueOf(args[0]));
				ServerInstance instance = server.createNewInstance(-1, new Address(args[1], Integer.valueOf(args[2])));
				System.out.println(instance);
			} else if (base.equalsIgnoreCase("manageserver")) {
				ManagedServer server = serverManager.getServer(Integer.valueOf(args[1]));
				if (args[0].equalsIgnoreCase("delete")) {
					serverManager.deleteServer(server);
				} else if (server instanceof SingleInstanceServer) {
					switch (args[0]) {
						case "start":
							((SingleInstanceServer) server).start();
							break;
						case "force-stop":
							((SingleInstanceServer) server).forceStop();
							break;
						default:
							System.out.println("not found");
							break;
					}
				} else {
					System.out.println("Only supported for single instance servers.");
				}
				System.out.println("OK");
			} else if (base.equalsIgnoreCase("manageinstance")) {
				MultiInstanceServer server = (MultiInstanceServer) serverManager.getServer(Integer.valueOf(args[1]));
				ServerInstance instance = server.getInstance(Integer.valueOf(args[2]));
				switch (args[0]) {
					case "start":
						instance.start();
						break;
					case "force-stop":
						instance.forceStop();
						break;
					case "delete":
						instance.delete();
						break;
					default:
						System.out.println("not found");
						break;
				}
				System.out.println("OK");
			} else if (base.equalsIgnoreCase("sendCmd")) {
				GameServer server = serverManager.getServer(Integer.valueOf(args[0]));
				StringBuilder s = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					s.append(args[i]).append(' ');
				}
				((SingleInstanceServer) server).dispatchCommand(s.substring(0, s.length() - 1));
				System.out.println("OK");
			} else if (base.equalsIgnoreCase("listworkers")) {
				System.out.println("---Workers---");
				for (RunningWorker worker : workerManager.getAllWorkers()) {
					System.out.println(worker.getId() + " on " + worker.getOfflineWorker().getAddress());
				}
				System.out.println("------");
			} else if (base.equalsIgnoreCase("listusers")) {
				System.out.println("---Users---");
				for (PanelUser user : panelUserDatabase.getAllUsers()) {
					System.out.println(user.toString());
				}
				System.out.println("------");
			} else if (base.equalsIgnoreCase("listservers")) {
				System.out.println("---Servers---");
				for (GameServer server : serverManager.getAllServers()) {
					System.out.println(server.toString());
				}
				System.out.println("------");
			} else if (base.equalsIgnoreCase("addperm")) {
				PanelUser user = panelUserDatabase.getUser(Integer.valueOf(args[0]));
				if (args.length == 2) {
					user.grantGlobalPermission(Integer.valueOf(args[1]));
				} else {
					user.grantPermission(Integer.valueOf(args[1]), Integer.valueOf(args[2]));
				}
				panelUserDatabase.updateUser(user);
				System.out.println("OK");
			} else if (base.equalsIgnoreCase("help")) {
				logger.info("commands:");
				logger.info("adduser <name> <password>");
				logger.info("crateserver <hostIp> <hostPort> <workerId>");
				logger.info("createmultiserver <workerId>");
				logger.info("createinstance <serverId> <hostIp> <hostPort>");
				logger.info("manageserver <start|force-stop|delete> <serverId>");
				logger.info("manageinstance <start|force-stop|delete> <serverId> <instanceId>");
				logger.info("sendcmd <serverId> <cmd...>");
				logger.info("listworkers");
				logger.info("listusers");
				logger.info("listservers");
				logger.info("addperm <userId> <value>");
				logger.info("addperm <userId> <serverId> <value>");
			} else {
				System.out.println(serverManager);
				logger.info("Unknown command! For help type \"help\"");
			}
		} catch (Exception e) {
			new Exception("Failed to process command \"" + cmd + "\"", e).printStackTrace();
		}
	}
}
