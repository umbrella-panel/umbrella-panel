package ms.idrea.umbrellapanel.worker.ftp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ms.idrea.umbrellapanel.core.PanelUser;
import ms.idrea.umbrellapanel.worker.UserRegistery;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.FtpletContext;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginRequest;
import org.apache.ftpserver.usermanager.impl.WritePermission;

public class UmbrellaFTPServer {

	// 
	
	
	public static void main(String... args) {
		new UmbrellaFTPServer().start();
		while (true)
			;
	}
	
	public void start() {
		UserRegistery userRegistery = new UserRegistery();
		
		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory listener = new ListenerFactory();
		listener.setPort(21);
		serverFactory.addListener("default", listener.createListener());
		
		userRegistery.update(new PanelUser("paul", "****"));
		
		serverFactory.setUserManager(userRegistery);
		
		
		Map<String, Ftplet> m = new HashMap<String, Ftplet>();
		m.put("miaFtplet", new Ftplet() {

			public void init(FtpletContext ftpletContext) throws FtpException {
			}

			public void destroy() {
			}

			public FtpletResult beforeCommand(FtpSession session, FtpRequest request) throws FtpException, IOException {
				return FtpletResult.DEFAULT;
			}

			public FtpletResult afterCommand(FtpSession session, FtpRequest request, FtpReply reply) throws FtpException, IOException {
				return FtpletResult.DEFAULT;
			}

			public FtpletResult onConnect(FtpSession session) throws FtpException, IOException {
				return FtpletResult.DEFAULT;
			}

			public FtpletResult onDisconnect(FtpSession session) throws FtpException, IOException {
				return FtpletResult.DEFAULT;
			}
		});
		serverFactory.setFtplets(m);
		FtpServer server = serverFactory.createServer();
		try {
			server.start();
		} catch (FtpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Server ist gestartet! hoffe ich zmd.!");
	}
}
