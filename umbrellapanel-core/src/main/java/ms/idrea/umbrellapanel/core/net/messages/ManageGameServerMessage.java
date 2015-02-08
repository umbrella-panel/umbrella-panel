package ms.idrea.umbrellapanel.core.net.messages;

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

	private int id;
	private Action action;
}
