package ms.idrea.umbrellapanel.net.messages;

import com.flowpowered.networking.Message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import ms.idrea.umbrellapanel.api.util.Address;

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
	private Address address;
	private String startCommand;
}
