package ms.idrea.umbrellapanel.net.messages;

import com.flowpowered.networking.Message;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import ms.idrea.umbrellapanel.net.messages.UpdateGameServerMessage.Action;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class UpdateMultiInstanceServerMessage implements Message {

	private Action action;
	private int id;
	private String startCommand;
}
