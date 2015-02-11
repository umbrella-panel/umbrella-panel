package ms.idrea.umbrellapanel.web.net;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ms.idrea.umbrellapanel.core.net.UmbrellaProtocol;

import com.flowpowered.networking.NetworkServer;
import com.flowpowered.networking.protocol.AbstractProtocol;
import com.flowpowered.networking.protocol.ProtocolRegistry;
import com.flowpowered.networking.session.Session;

public class UmbrellaNetworkServer extends NetworkServer {

	private ProtocolRegistry<AbstractProtocol> pr = new ProtocolRegistry<>();
	private ConcurrentMap<InetSocketAddress, Worker> sessions = new ConcurrentHashMap<>();

	public UmbrellaNetworkServer() {
		bindAndRegister(new InetSocketAddress(30000), new UmbrellaProtocol(ServerMessageHandler.class));
	}

	private void bindAndRegister(InetSocketAddress a, AbstractProtocol p) {
		bind(a);
		pr.registerProtocol(a.getPort(), p);
	}

	@Override
	public Session newSession(Channel c) {
		Worker session = new Worker(c, pr.getProtocol(c.localAddress()));
		sessions.put(session.getAddress(), session);
		return session;
	}

	@Override
	public void sessionInactivated(Session session) {
		if (!(session instanceof Worker)) {
			throw new IllegalArgumentException("Session is not a Worker!");
		}
		sessions.remove(((Worker) session).getAddress());
	}
}
