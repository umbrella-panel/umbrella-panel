package ms.idrea.umbrellapanel.core.net.messages;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import com.flowpowered.networking.Message;

@Getter
@ToString
@AllArgsConstructor
public class LogMessage implements Message {

	private int id;
	private List<String> lines;
}
