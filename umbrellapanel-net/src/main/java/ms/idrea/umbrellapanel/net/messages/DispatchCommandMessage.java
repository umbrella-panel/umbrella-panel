package ms.idrea.umbrellapanel.net.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import com.flowpowered.networking.Message;

@Getter
@ToString
@AllArgsConstructor
public class DispatchCommandMessage implements Message {

	private int id;
	private String command;
}
