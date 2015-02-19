package ms.idrea.umbrellapanel.chief.net;

import ms.idrea.umbrellapanel.api.chief.Chief;
import ms.idrea.umbrellapanel.chief.UmbrellaChief;
import ms.idrea.umbrellapanel.core.net.messages.LogMessage;
import ms.idrea.umbrellapanel.core.net.messages.WorkerMessage;
import ms.idrea.umbrellapanel.core.net.messages.WorkerMessage.Action;

import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;

public class ServerMessageHandler implements MessageHandler<Worker, Message> {
	
	private Chief chief;
	
	public ServerMessageHandler() {
		chief = UmbrellaChief.getInstance();
	}
	
	@Override
	public void handle(Worker worker, Message rawMessage) {
	//	System.out.println("[SERVER-IN " + worker + "]: " + rawMessage);
		try {
			if (rawMessage instanceof WorkerMessage) {
				WorkerMessage message = (WorkerMessage) rawMessage;
				if (message.getSharedPassword().equals("123")) {
					switch (message.getAction()) {
						case REGISTER:
							chief.getWorkerManager().onRegister(worker);
							worker.send(new WorkerMessage(message.getAction(), worker.getId(), message.getSharedPassword()));
							break;
						case STARTED:
							chief.getWorkerManager().onStart(worker, message.getId());
							worker.send(new WorkerMessage(message.getAction(), worker.getId(), message.getSharedPassword()));
							break;
						case STOPPED:
							chief.getWorkerManager().onStop(worker);
							break;
						default:
							throw new UnsupportedOperationException();
					}
					// send them the servers.
				} else {
					worker.send(new WorkerMessage(Action.REGISTER, -1, ""));
				}
			} else if (rawMessage instanceof LogMessage) {
				LogMessage message = (LogMessage) rawMessage;
				for (String line : message.getLines()) {
					System.out.println("[" + message.getId() + "]: " + line);
				}
			}
		} catch (Throwable e) {
			System.out.println("Error while processing network messsage: \"" + rawMessage.getClass() + "\" (\"" + rawMessage.toString() + "\"), send by \"" + worker.getClass() + "\" (\"" + worker.toString() + "\")");
			e.printStackTrace();
		}
	}
	
}
