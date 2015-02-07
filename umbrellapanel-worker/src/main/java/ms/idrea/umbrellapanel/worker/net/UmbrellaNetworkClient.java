package ms.idrea.umbrellapanel.worker.net;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import lombok.Getter;
import ms.idrea.umbreallapanel.core.net.UmbrellaProtocol;
import ms.idrea.umbreallapanel.core.net.DynamicSession;

import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;
import com.flowpowered.networking.NetworkClient;
import com.flowpowered.networking.session.Session;

public class UmbrellaNetworkClient extends NetworkClient implements MessageHandler<DynamicSession, Message> {

	@Getter
	private DynamicSession session;
	private final Runnable startup;

	public UmbrellaNetworkClient(InetSocketAddress address, Runnable startup) {
		this.startup = startup;
		connect(address);
	}

	@Override
	public Session newSession(Channel c) {
		session = new DynamicSession(c, new UmbrellaProtocol(UmbrellaNetworkClient.class)) {

			@Override
			public void onReady() {
				if (startup != null)
					startup.run();
			}
		};
		return session;
	}

	@Override
	public void onConnectFailure(SocketAddress address, Throwable t) {
		throw new RuntimeException(t);
	}

	@Override
	public void sessionInactivated(Session session) {
		session.disconnect();
	}

	@Override
	public void handle(DynamicSession session, Message message) {
		System.out.println("[CLIENT-IN " + session + "]: " + message);
	}
}
