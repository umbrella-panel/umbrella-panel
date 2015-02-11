package ms.idrea.umbrellapanel.worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.idrea.umbrellapanel.core.net.messages.LogMessage;

public class UmbrellaLogHandler implements LogHandler {

	private Map<Integer, List<String>> logBuffer = new HashMap<>();
	private Worker worker;
	
	public UmbrellaLogHandler(Worker worker) {
		this.worker = worker;
	}
	
	public void append(int id, String message) {
		append(new ServerLog(id, message));
	}

	public void append(ServerLog log) {
		if (logBuffer.get(log.getId()) == null) {
			logBuffer.put(log.getId(), new ArrayList<String>());
		}
		logBuffer.get(log.getId()).add(log.getMessage());
		
		worker.getLogger().fine("[" + log.getId() + "]: " + log.getMessage());
	}

	public void flush() {
		for (int id : logBuffer.keySet()) {
			worker.getNetworkClient().send(new LogMessage(id, logBuffer.get(id)));
		}
		logBuffer.clear();
	}
}
