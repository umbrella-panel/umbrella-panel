package ms.idrea.umbrellapanel.web.net;

import ms.idrea.umbrellapanel.core.net.DynamicSession;
import ms.idrea.umbrellapanel.core.net.messages.WorkerMessage;
import ms.idrea.umbrellapanel.core.net.messages.WorkerMessage.Action;

import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;

public class ServerMessageHandler implements MessageHandler<DynamicSession, Message> {
	
	@Override
	public void handle(DynamicSession session, Message rawMessage) {
		System.out.println("[SERVER-IN " + session + "]: " + rawMessage);
		try {
			if (rawMessage instanceof WorkerMessage) {
				WorkerMessage message = (WorkerMessage) rawMessage;
				if (message.getSharedPassword().equals("123") && (message.getAction() == Action.REGISTER || message.getAction() == Action.STARTED)) {
					session.send(new WorkerMessage(message.getAction(), 1, message.getSharedPassword()));
					// send them the servers.
				} else {
					session.send(new WorkerMessage(Action.REGISTER, -1, ""));
				}
			}
		} catch (Throwable e) {
			System.out.println("Error while processing network messsage: \"" + rawMessage.getClass() + "\" (\"" + rawMessage.toString() + "\"), send by \"" + session.getClass() + "\" (\"" + session.toString() + "\")");
			e.printStackTrace();
		}
	}
	
}
