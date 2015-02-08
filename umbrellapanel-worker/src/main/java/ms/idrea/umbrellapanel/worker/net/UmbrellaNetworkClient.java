package ms.idrea.umbrellapanel.worker.net;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import lombok.Getter;

import ms.idrea.umbrellapanel.core.net.DynamicSession;
import ms.idrea.umbrellapanel.core.net.UmbrellaProtocol;

import com.flowpowered.networking.Message;
import com.flowpowered.networking.NetworkClient;
import com.flowpowered.networking.session.Session;

public class UmbrellaNetworkClient extends NetworkClient implements ms.idrea.umbrellapanel.worker.net.NetworkClient {

	@Getter
	private DynamicSession session;
	private final Runnable startup;

	public UmbrellaNetworkClient(InetSocketAddress address, Runnable startup) {
		this.startup = startup;
		connect(address);
	}

	@Override
	public Session newSession(Channel c) {
		session = new DynamicSession(c, new UmbrellaProtocol(ClientMessageHandler.class)) {

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
	public void send(Message... messages) {
		for (Message message : messages) {
			session.send(message);;
		}
	}
}
