package ms.idrea.umbrellapanel.chief.net;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ms.idrea.umbrellapanel.api.chief.WorkerManager;
import ms.idrea.umbrellapanel.core.net.DynamicSession;
import ms.idrea.umbrellapanel.core.net.UmbrellaProtocol;

import com.flowpowered.networking.Message;
import com.flowpowered.networking.NetworkServer;
import com.flowpowered.networking.protocol.AbstractProtocol;
import com.flowpowered.networking.protocol.ProtocolRegistry;
import com.flowpowered.networking.session.Session;

public class UmbrellaNetworkServer extends NetworkServer implements ms.idrea.umbrellapanel.api.chief.net.NetworkServer {

	private ProtocolRegistry<AbstractProtocol> pr = new ProtocolRegistry<>();
	private ConcurrentMap<InetSocketAddress, Worker> workers = new ConcurrentHashMap<>();
	private WorkerManager workerManager;

	public UmbrellaNetworkServer(WorkerManager workerManager) {
		this.workerManager = workerManager;
		bindAndRegister(new InetSocketAddress(30000), new UmbrellaProtocol(ServerMessageHandler.class));
	}

	private void bindAndRegister(InetSocketAddress a, AbstractProtocol p) {
		bind(a);
		pr.registerProtocol(a.getPort(), p);
	}

	@Override
	public Session newSession(Channel c) {
		Worker session = new Worker(c, pr.getProtocol(c.localAddress()));
		workers.put(session.getAddress(), session);
		return session;
	}

	@Override
	public void sessionInactivated(Session session) {
		if (!(session instanceof Worker)) {
			throw new IllegalArgumentException("Session is not a Worker!");
		}
		workers.remove(((Worker) session).getAddress());
		workerManager.onStop(session);
	}

	@Override
	public void broadcast(Message... messages) {
		for (Session worker : workerManager.getAllWorkers()) {
			worker.sendAll(messages);
		}
	}
}
