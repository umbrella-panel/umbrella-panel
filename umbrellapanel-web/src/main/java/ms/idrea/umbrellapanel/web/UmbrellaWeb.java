package ms.idrea.umbrellapanel.web;

import java.util.Scanner;

import lombok.Getter;

import ms.idrea.umbrellapanel.core.PanelUser;
import ms.idrea.umbrellapanel.core.net.DynamicSession;
import ms.idrea.umbrellapanel.core.net.messages.CreateGameServerMessage;
import ms.idrea.umbrellapanel.core.net.messages.DispatchCommandMessage;
import ms.idrea.umbrellapanel.core.net.messages.ManageGameServerMessage;
import ms.idrea.umbrellapanel.core.net.messages.ManageGameServerMessage.Action;
import ms.idrea.umbrellapanel.util.Address;
import ms.idrea.umbrellapanel.web.net.UmbrellaNetworkServer;

public class UmbrellaWeb {
	
	@Getter
	private static UmbrellaWeb instance;
	
	public static void main(String... args) {
		instance = new UmbrellaWeb();
		instance.start();
	}
	
	// ---------------
	
	private UmbrellaNetworkServer networkServer;
	
	private void start() {
		networkServer = new UmbrellaNetworkServer();
		
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
		DynamicSession session = networkServer.first();
		session.send(new CreateGameServerMessage(0, new PanelUser("paul", "icanhazall"), new Address("0.0.0.0", 25565), false));
		session.send(new ManageGameServerMessage(0, Action.START));
	}
	
	public void b() {
		DynamicSession session = networkServer.first();
		session.send(new DispatchCommandMessage(0, "stop"));
	}
}
