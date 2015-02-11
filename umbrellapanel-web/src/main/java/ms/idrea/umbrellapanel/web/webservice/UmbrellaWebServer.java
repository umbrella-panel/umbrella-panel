package ms.idrea.umbrellapanel.web.webservice;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;

import ms.idrea.umbrellapanel.util.Address;
import ms.idrea.umbrellapanel.web.UmbrellaWebHandler;
import ms.idrea.umbrellapanel.web.UmbrellaWebRequest;

public class UmbrellaWebServer {
	
	private Server server;
	private WebHandler handler; 
	private List<UmbrellaWebHandler> handlers;
	
	public UmbrellaWebServer(){
		handlers = new ArrayList<UmbrellaWebHandler>();
		server = new Server();
	}
	
	public void start(Address address) throws Exception{
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(address.getPort());
		if(!address.getHost().equalsIgnoreCase("*")){
			connector.setHost(address.getHost());
		}
		server.setConnectors(new Connector[]{connector});
		HandlerList handlers = new HandlerList();
		handler = new WebHandler(this);
		handlers.addHandler(handler);
		server.setHandler(handlers);
		server.start();
	}
	
	public void registerWebHandler(UmbrellaWebHandler handler){
		handlers.add(handler);
	}
	
	public void request(UmbrellaWebRequest request){
		for(UmbrellaWebHandler handler : handlers){
			handler.onWebRequest(request);
		}
	}
	
}
