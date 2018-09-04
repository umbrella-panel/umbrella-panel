package ms.idrea.umbrellapanel.worker.net;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;
import com.flowpowered.networking.NetworkClient;
import com.flowpowered.networking.session.Session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;

import ms.idrea.umbrellapanel.api.worker.Worker;
import ms.idrea.umbrellapanel.net.DynamicSession;
import ms.idrea.umbrellapanel.net.UmbrellaProtocol;
import ms.idrea.umbrellapanel.net.messages.ProtocolVersionMessage;

@RequiredArgsConstructor
public class TransportClient implements ms.idrea.umbrellapanel.api.worker.net.NetworkClient {

	private final Queue<Message> messages = new LinkedBlockingQueue<>();
	private final Class<? extends MessageHandler<DynamicSession, Message>> handlerClazz = ClientMessageHandler.class;
	private final Worker worker;
	private final InetSocketAddress address;
	private final Runnable startup;
	private UmbrellaNetworkClient client;
	private boolean shutdown = false;

	public void connect() throws InterruptedException {
		tryConnect();
	}

	public void shutdown() {
		shutdown = true;
		if (client != null) {
			client.shutdown();
		}
	}

	@Override
	public void send(Message... messages) {
		for (Message message : messages) {
			sendMessage(message);
		}
	}

	public synchronized void sendMessage(Message message) {
		if (client == null || client.getState() != ConnectionState.CONNECTED || (!(message instanceof ProtocolVersionMessage) && client.getSession().getRemoteProtocolVersion() < 0)) {
			messages.offer(message);
		} else {
			client.sendMessage(message);
		}
	}

	synchronized void onConnectionReady() {
		if (client.getSession().getRemoteProtocolVersion() > 0) {
			while (messages.peek() != null) {
				client.sendMessage(messages.poll());
			}
		}
	}

	protected ByteBufAllocator alloc() {
		if (client != null && client.getSession() != null && client.getSession().getChannel() != null) {
			return client.getSession().getChannel().alloc();
		}
		return null;
	}

	protected void onClientDisconnect(UmbrellaNetworkClient disconnected) {
		if (shutdown) {
			return;
		}
		if (disconnected != client) {
			disconnected.shutdown();
			return;
		}
		client.shutdown();
		try {
			tryConnect();
		} catch (Exception e) {
			worker.getLogger().log(Level.WARNING, "Interrupted: ", e);
		}
	}

	private void tryConnect() throws InterruptedException {
		UmbrellaNetworkClient c = new UmbrellaNetworkClient(this, address, handlerClazz);
		worker.getLogger().info("Trying to connect to: " + address);
		ConnectionState state = c.getState();
		if (state == ConnectionState.PENDING) {
			state = c.waitForStateChange();
		}
		if (state == ConnectionState.CONNECTED) {
			handleConnected(c);
			return;
		}
		c.shutdown();
		Thread.sleep(1000);
		tryConnect();
	}

	private void handleConnected(UmbrellaNetworkClient newClient) {
		worker.getLogger().info("Connected with: " + newClient.getSession().getAddress());
		boolean initialConnect = client == null;
		if (!initialConnect) {
			client.shutdown();
		}
		client = newClient;
		startup.run();
	}

	public boolean isConnected() {
		return client != null ? client.getState() == ConnectionState.CONNECTED : false;
	}

	public static enum ConnectionState {
		PENDING,
		CONNECTED,
		FAILED;
	};

	class UmbrellaNetworkClient extends NetworkClient {

		private final Object look = new Object[0];
		private final Class<? extends MessageHandler<DynamicSession, Message>> handlerClazz;
		private final TransportClient coreTransportClient;
		@Getter
		private ConnectionState state = ConnectionState.PENDING;
		@Getter
		private DynamicSession session;

		protected UmbrellaNetworkClient(TransportClient mgsClient, InetSocketAddress address, Class<? extends MessageHandler<DynamicSession, Message>> handlerClazz) {
			this.handlerClazz = handlerClazz;
			this.coreTransportClient = mgsClient;
			connect(address);
		}

		private void setState(ConnectionState state) {
			this.state = state;
			synchronized (look) {
				look.notifyAll();
			}
		}

		public ConnectionState waitForStateChange() {
			synchronized (look) {
				try {
					look.wait();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return state;
		}

		public void sendMessage(Message message) {
			session.send(message);
		}

		@Override
		public Session newSession(Channel c) {
			session = new DynamicSession(c, new UmbrellaProtocol(handlerClazz));
			setState(ConnectionState.CONNECTED);
			return session;
		}

		@Override
		public void onConnectSuccess(SocketAddress address) {
			// use newSession for connected
		}

		@Override
		public void onConnectFailure(SocketAddress address, Throwable t) {
			setState(ConnectionState.FAILED);
		}

		@Override
		public void sessionInactivated(Session session) {
			session.disconnect();
			setState(ConnectionState.FAILED);
			coreTransportClient.onClientDisconnect(this);
		}
	}
}
