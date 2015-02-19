package ms.idrea.umbrellapanel.api.worker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

public interface LogHandler {

	public void append(int id, String message);

	public void append(ServerLog log);

	public void flush();

	public void shutdown();

	@Getter
	@ToString
	@AllArgsConstructor
	public static class ServerLog {

		private int id;
		private String message;
	}
}
