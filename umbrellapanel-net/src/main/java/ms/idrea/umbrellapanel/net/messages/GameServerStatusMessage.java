package ms.idrea.umbrellapanel.net.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import com.flowpowered.networking.Message;

@Getter
@ToString
@AllArgsConstructor
public class GameServerStatusMessage implements Message {

	public static enum Status {
		RUNNING,
		STOPPED
	}

	private int id;
	private Status status;
}
