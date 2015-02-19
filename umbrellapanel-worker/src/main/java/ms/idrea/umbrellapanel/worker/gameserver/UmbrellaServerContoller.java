package ms.idrea.umbrellapanel.worker.gameserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import lombok.Getter;

/**
 * Controls and manages a single, running {@link UmbreallaGameServer} instance
 * 
 * @author Paul
 */
public class UmbrellaServerContoller extends Thread {

	public static enum ProcessState {
		PRE,
		RUNNING,
		STOPPED,
		STOPPING
	};

	public static final String STOP_COMMAND = "stop";
	private UmbrellaGameServer server;
	private Process process;
	private BufferedReader input;
	private PrintWriter output;
	@Getter
	private ProcessState processState = ProcessState.PRE;

	public UmbrellaServerContoller(UmbrellaGameServer server) {
		super("serverController-" + server.getId());
		this.server = server;
		super.start();
	}

	@Override
	public void start() {
		throw new UnsupportedOperationException("Cannot start " + getClass().getSimpleName());
	}

	private void setState(ProcessState state) {
		processState = state;
		server.updateProcessState(processState);
	}

	public void forceStop() {
		if (process != null)
			process.destroy();
		setState(ProcessState.STOPPED);
	}

	public void softStop() {
		dispatchCommand(STOP_COMMAND);
		setState(ProcessState.STOPPING);
	}

	public boolean dispatchCommand(String command) {
		if (processState != ProcessState.RUNNING) {
			return false;
		}
		output.println(command);
		output.flush();
		return true;
	}

	@Override
	public void run() {
		try {
			process = Runtime.getRuntime().exec(server.getStartCommand(), new String[] {}, server.getWorkingDirectory());
			setState(ProcessState.RUNNING);
			input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			output = new PrintWriter(process.getOutputStream());
			String line;
			while ((line = input.readLine()) != null) {
				server.appendServerLog(line);
			}
		} catch (IOException e) {
		}
		if (process != null) {
			process.destroy();
		}
		setState(ProcessState.STOPPED);
	}
}
