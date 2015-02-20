package ms.idrea.umbrellapanel.net.messages;

import com.flowpowered.networking.Message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ManageGameServerMessage implements Message {

	public static enum Action {
		DELETE,
		START,
		FORCE_STOP
	}

	private Action action;
	private int id;
}
