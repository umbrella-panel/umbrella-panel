package ms.idrea.umbrellapanel.worker.ftp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ms.idrea.umbrellapanel.api.worker.Worker;
import ms.idrea.umbrellapanel.api.worker.ftp.FTPServer;

import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.DataConnectionFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.FtpletContext;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.apache.ftpserver.listener.ListenerFactory;

public class UmbrellaFTPServer implements FTPServer {

	private Worker worker;
	private FtpServer ftpServer;

	public UmbrellaFTPServer(Worker worker) {
		this.worker = worker;
	}

	@Override
	public void start() {
		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory listener = new ListenerFactory();
		listener.setPort(1221);
		DataConnectionConfigurationFactory dccf = new DataConnectionConfigurationFactory();
		dccf.setPassivePorts(worker.getWorkerProperties().getPassivePort());
		
		dccf.setActiveEnabled(listener.getDataConnectionConfiguration().isActiveEnabled());
		dccf.setActiveIpCheck(listener.getDataConnectionConfiguration().isActiveIpCheck());
		dccf.setImplicitSsl(listener.getDataConnectionConfiguration().isImplicitSsl());
		dccf.setActiveLocalAddress(listener.getDataConnectionConfiguration().getActiveLocalAddress());
		dccf.setActiveLocalPort(listener.getDataConnectionConfiguration().getActiveLocalPort());
		dccf.setIdleTime(listener.getDataConnectionConfiguration().getIdleTime());
		dccf.setPassiveAddress(listener.getDataConnectionConfiguration().getPassiveAddress());
		dccf.setPassiveExternalAddress(listener.getDataConnectionConfiguration().getPassiveExernalAddress());
		dccf.setSslConfiguration(listener.getDataConnectionConfiguration().getSslConfiguration());
		
		System.out.println("------------");
		System.out.println(listener.getDataConnectionConfiguration().getPassivePorts());
		System.out.println("------------");
		
		listener.setDataConnectionConfiguration(dccf.createDataConnectionConfiguration());
		serverFactory.addListener("default", listener.createListener());
		serverFactory.setUserManager(worker.getFTPUserWrapper());
		serverFactory.setFtplets(bakeFtplets());
		ftpServer = serverFactory.createServer();
		try {
			ftpServer.start();
		} catch (FtpException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void shutdown() {
		ftpServer.stop();
	}

	private Map<String, Ftplet> bakeFtplets() {
		Map<String, Ftplet> map = new HashMap<String, Ftplet>();
		map.put("miaFtplet", new Ftplet() {

			@Override
			public void init(FtpletContext ftpletContext) throws FtpException {
			}

			@Override
			public void destroy() {
			}

			@Override
			public FtpletResult beforeCommand(FtpSession session, FtpRequest request) throws FtpException, IOException {
				return FtpletResult.DEFAULT;
			}

			@Override
			public FtpletResult afterCommand(FtpSession session, FtpRequest request, FtpReply reply) throws FtpException, IOException {
				return FtpletResult.DEFAULT;
			}

			@Override
			public FtpletResult onConnect(FtpSession session) throws FtpException, IOException {
				return FtpletResult.DEFAULT;
			}

			@Override
			public FtpletResult onDisconnect(FtpSession session) throws FtpException, IOException {
				return FtpletResult.DEFAULT;
			}
		});
		return map;
	}
}
