package ms.idrea.umbrellapanel.worker.ftp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.FtpletContext;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.apache.ftpserver.listener.ListenerFactory;

import ms.idrea.umbrellapanel.api.worker.Worker;
import ms.idrea.umbrellapanel.api.worker.ftp.FTPServer;

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
