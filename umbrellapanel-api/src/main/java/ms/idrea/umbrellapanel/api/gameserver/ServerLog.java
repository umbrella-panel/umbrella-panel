package ms.idrea.umbrellapanel.api.gameserver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ServerLog {

	private final long timestamp;
	private final String message;
}
