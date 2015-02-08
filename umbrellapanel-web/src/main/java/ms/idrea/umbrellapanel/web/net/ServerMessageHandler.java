package ms.idrea.umbrellapanel.web.net;

import ms.idrea.umbrellapanel.core.net.DynamicSession;

import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;

public class ServerMessageHandler implements MessageHandler<DynamicSession, Message> {
	
	@Override
	public void handle(DynamicSession session, Message message) {
		System.out.println("[SERVER-IN " + session + "]: " + message);
	}
	
}
