package ms.idrea.umbrellapanel.core.net.messages;

import com.flowpowered.networking.Message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@AllArgsConstructor
public class TextMessage implements Message {

	public TextMessage(String subChannel, String text) {
		this(text);
	}

	private String text;
}
