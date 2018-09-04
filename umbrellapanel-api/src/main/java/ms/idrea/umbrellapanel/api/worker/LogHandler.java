package ms.idrea.umbrellapanel.api.worker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

public interface LogHandler {

	void append(int serverId, int instanceId, String message);

	void append(int serverId, String message);

	void flush();

	void shutdown();

	@Getter
	@ToString
	@AllArgsConstructor
	public static class ServerLog {

		private int serverId;
		private int instanceId;
		private String message;
	}
}
