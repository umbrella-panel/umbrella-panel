package ms.idrea.umbrellapanel.net.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import com.flowpowered.networking.Message;

@Getter
@ToString
@AllArgsConstructor
public class WorkerMessage implements Message {

	public static enum Action {
		REGISTER,
		STARTED,
		STOPPED
	}

	public WorkerMessage(Action action, String sharedPassword) {
		this(action, -1, sharedPassword);
		if (action != Action.REGISTER) {
			throw new IllegalArgumentException("Action musst be \"" + Action.REGISTER + "\", but was \"" + action + "\"");
		}
	}

	private Action action;
	private int id;
	private String sharedPassword;
}
