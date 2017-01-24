package ms.idrea.umbrellapanel.api.worker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

public interface LogHandler {

	void append(int id, String message);

	void append(ServerLog log);

	void flush();

	void shutdown();

	@Getter
	@ToString
	@AllArgsConstructor
	public static class ServerLog {

		private int id;
		private String message;
	}
}
