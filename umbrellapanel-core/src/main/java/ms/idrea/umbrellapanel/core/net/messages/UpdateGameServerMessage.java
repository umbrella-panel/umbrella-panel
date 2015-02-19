package ms.idrea.umbrellapanel.core.net.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import ms.idrea.umbrellapanel.api.util.Address;

import com.flowpowered.networking.Message;

@Getter
@ToString
@AllArgsConstructor
public class UpdateGameServerMessage implements Message {

	public static enum Action {
		CREATE,
		UPDATE;
	}

	private Action action;
	private int id;
	private int userId;
	private Address address;
	private String startCommand;
}
