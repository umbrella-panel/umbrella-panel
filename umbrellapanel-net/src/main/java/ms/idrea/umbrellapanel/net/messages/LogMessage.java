package ms.idrea.umbrellapanel.net.messages;

import com.flowpowered.networking.Message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class LogMessage implements Message {

	private int serverId;
	private int instanceId;
	private String line;
}
