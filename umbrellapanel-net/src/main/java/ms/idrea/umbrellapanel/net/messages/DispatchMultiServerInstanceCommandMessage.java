package ms.idrea.umbrellapanel.net.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import com.flowpowered.networking.Message;

@Getter
@ToString
@AllArgsConstructor
public class DispatchMultiServerInstanceCommandMessage implements Message {

	private int serverId;
	private int instanceId;
	private String command;
}
