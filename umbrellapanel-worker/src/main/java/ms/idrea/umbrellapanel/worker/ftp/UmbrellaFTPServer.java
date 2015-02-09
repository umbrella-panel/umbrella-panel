package ms.idrea.umbrellapanel.worker.ftp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
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
import org.apache.ftpserver.usermanager.impl.BaseUser;

public class UmbrellaFTPServer {

	// 
	
	public void start() {
		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory listener = new ListenerFactory();
		listener.setPort(21);
		serverFactory.addListener("default", listener.createListener());
		
		UserManager um = new UserManager() {
			
			@Override
			public void save(User user) throws FtpException {
				System.out.println("UmbrellaFTPServer.start().new UserManager() {...}.save()");
				// TODO Auto-generated method stub
			}
			
			@Override
			public boolean isAdmin(String login) throws FtpException {
				System.out.println("UmbrellaFTPServer.start().new UserManager() {...}.isAdmin()");
				return false;
			}
			
			@Override
			public User getUserByName(String login) throws FtpException {
				System.out.println("UmbrellaFTPServer.start().new UserManager() {...}.getUserByName()");
				// TODO Auto-generated method stub
				return new BaseUser();
			}
			
			@Override
			public String[] getAllUserNames() throws FtpException {
				System.out.println("UmbrellaFTPServer.start().new UserManager() {...}.getAllUserNames()");
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getAdminName() throws FtpException {
				System.out.println("UmbrellaFTPServer.start().new UserManager() {...}.getAdminName()");
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean doesExist(String login) throws FtpException {
				System.out.println("UmbrellaFTPServer.start().new UserManager() {...}.doesExist()");
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void delete(String login) throws FtpException {
				System.out.println("UmbrellaFTPServer.start().new UserManager() {...}.delete()");
				// TODO Auto-generated method stub
			}
			
			@Override
			public User authenticate(Authentication authentication) throws AuthenticationFailedException {
				System.out.println("UmbrellaFTPServer.start().new UserManager() {...}.authenticate()");
				System.out.println(authentication.toString());
				return null;
			}
		};
		
		serverFactory.setUserManager(um);
		
		
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
