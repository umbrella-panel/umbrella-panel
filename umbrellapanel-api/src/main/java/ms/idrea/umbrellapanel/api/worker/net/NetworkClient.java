package ms.idrea.umbrellapanel.api.worker.net;

import com.flowpowered.networking.Message;

public interface NetworkClient {

	public void send(Message... messages);

	public void shutdown();
}
