package ms.idrea.umbrellapanel.web.net;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ms.idrea.umbrellapanel.core.net.DynamicSession;
import ms.idrea.umbrellapanel.core.net.UmbrellaProtocol;

import com.flowpowered.networking.NetworkServer;
import com.flowpowered.networking.protocol.AbstractProtocol;
import com.flowpowered.networking.protocol.ProtocolRegistry;
import com.flowpowered.networking.session.Session;

public class UmbrellaNetworkServer extends NetworkServer {

	private ProtocolRegistry<AbstractProtocol> pr = new ProtocolRegistry<AbstractProtocol>();
	private ConcurrentMap<InetSocketAddress, DynamicSession> sessions = new ConcurrentHashMap<InetSocketAddress, DynamicSession>();

	public UmbrellaNetworkServer() {
		bindAndRegister(new InetSocketAddress(30000), new UmbrellaProtocol(ServerMessageHandler.class));
	}

	private void bindAndRegister(InetSocketAddress a, AbstractProtocol p) {
		bind(a);
		pr.registerProtocol(a.getPort(), p);
	}

	@Override
	public Session newSession(Channel c) {
		DynamicSession session = new DynamicSession(c, pr.getProtocol(c.localAddress()));
		sessions.put(session.getAddress(), session);
		return session;
	}

	@Override
	public void sessionInactivated(Session session) {
		if (!(session instanceof DynamicSession)) {
			throw new IllegalArgumentException("Session is not a dynamicSession!");
		}
		sessions.remove(((DynamicSession) session).getAddress());
	}

	public DynamicSession first() {
		for (InetSocketAddress address : sessions.keySet()) {
			return sessions.get(address);
		}
		return null;
	}
}
