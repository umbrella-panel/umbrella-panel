package ms.idrea.umbrellapanel.web;

import java.util.Scanner;

import lombok.Getter;
import ms.idrea.umbrellapanel.core.PanelUser;
import ms.idrea.umbrellapanel.core.net.DynamicSession;
import ms.idrea.umbrellapanel.core.net.messages.UpdateGameServerMessage;
import ms.idrea.umbrellapanel.core.net.messages.DispatchCommandMessage;
import ms.idrea.umbrellapanel.core.net.messages.ManageGameServerMessage;
import ms.idrea.umbrellapanel.core.net.messages.ManageGameServerMessage.Action;
import ms.idrea.umbrellapanel.core.net.messages.UpdatePanelUserMessage;
import ms.idrea.umbrellapanel.util.Address;
import ms.idrea.umbrellapanel.web.net.UmbrellaNetworkServer;
import ms.idrea.umbrellapanel.web.webservice.UmbrellaWebServer;

public class UmbrellaWeb {
	
	@Getter
	private static UmbrellaWeb instance;
	
	public static void main(String... args) {
		instance = new UmbrellaWeb();
		instance.start();
	}
	
	// ---------------
	@Getter
	private UmbrellaWebServer webServer;
	
	private UmbrellaNetworkServer networkServer;
	
	private void start() {
		networkServer = new UmbrellaNetworkServer();
		webServer = new UmbrellaWebServer();
		try {
			webServer.start(new Address("*", 80));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean b = true;
		Scanner scanner = new Scanner(System.in);
		while (!scanner.next().equalsIgnoreCase("exit")){
			System.out.println("Type \"exit\" to exit the program!");
			if (b) {
				b = false;
				a();
			} else {
				b();
			}
			
		}
		
		scanner.close();
		networkServer.shutdown();
	}
	
	public void a() {
		DynamicSession session = null;;
		
		session.send(new UpdatePanelUserMessage(new PanelUser(0, "paul", "icanhaz")));
		session.send(new UpdateGameServerMessage(ms.idrea.umbrellapanel.core.net.messages.UpdateGameServerMessage.Action.CREATE, 0, 0, new Address("0.0.0.0", 25565), "java -jar server.jar"));
		session.send(new ManageGameServerMessage(Action.START, 0));
	}
	
	public void b() {
		DynamicSession session = null;
		session.send(new DispatchCommandMessage(0, "stop"));
	}
}
