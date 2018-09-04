package ms.idrea.umbrellapanel.net.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import ms.idrea.umbrellapanel.net.messages.ManageGameServerMessage.Action;

import com.flowpowered.networking.Message;

@Getter
@ToString
@AllArgsConstructor
public class ManageMultiServerInstanceMessage implements Message {

	private Action action;
	private int serverId;
	private int instanceId;
}
