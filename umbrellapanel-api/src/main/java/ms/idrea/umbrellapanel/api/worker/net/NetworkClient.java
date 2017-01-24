package ms.idrea.umbrellapanel.api.worker.net;

import com.flowpowered.networking.Message;

public interface NetworkClient {

	void send(Message... messages);

	void shutdown();
}
