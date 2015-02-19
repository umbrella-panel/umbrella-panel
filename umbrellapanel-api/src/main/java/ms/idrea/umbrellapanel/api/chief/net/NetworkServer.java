package ms.idrea.umbrellapanel.api.chief.net;

import com.flowpowered.networking.Message;

public interface NetworkServer {

	public void broadcast(Message... messages);

	public void shutdown();
}
