package ms.idrea.umbrellapanel.api.chief.net;

import com.flowpowered.networking.Message;

public interface NetworkServer {

	void broadcast(Message... messages);

	void shutdown();
}
