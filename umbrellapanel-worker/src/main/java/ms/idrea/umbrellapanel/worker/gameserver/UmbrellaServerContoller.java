package ms.idrea.umbrellapanel.worker.gameserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.commons.io.IOUtils;

import lombok.Getter;

import ms.idrea.umbrellapanel.api.gameserver.ServerInstance;
import ms.idrea.umbrellapanel.worker.gameserver.UmbrellaMultiInstanceGameServer.UmbrellaServerInstance;

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
		STOPPING;
	};

	private final AbstractServer server;
	private final ServerInstance instance;
	private Process process;
	private BufferedReader input;
	private BufferedReader errorInput;
	private PrintWriter output;
	@Getter
	private ProcessState processState = ProcessState.PRE;

	public UmbrellaServerContoller(ServerInstance instance, AbstractServer server) {
		super("serverController-" + server.getId());
		this.instance = instance;
		this.server = server;
		super.start();
	}

	@Override
	public void start() {
		throw new UnsupportedOperationException("Cannot start " + getClass().getSimpleName());
	}

	private void setState(ProcessState state) {
		processState = state;
		if (instance instanceof UmbrellaSingleInstanceGameServer) {
			((UmbrellaSingleInstanceGameServer) instance).updateProcessState(state);
		} else if (instance instanceof UmbrellaServerInstance) {
			((UmbrellaServerInstance) instance).updateProcessState(state);
		}
	}

	public void forceStop() {
		if (process != null) {
			process.destroy();
		} else {
			setState(ProcessState.STOPPED);
		}
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
			process = Runtime.getRuntime().exec(server.getStartCommand() + " --port " + instance.getAddress().getPort() + " --host " + instance.getAddress().getHost(), new String[] {}, server.getWorkingDirectory());
			setState(ProcessState.RUNNING);
			input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			errorInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			output = new PrintWriter(process.getOutputStream());
			String line;
			while ((line = input.readLine()) != null || (line = errorInput.readLine()) != null) {
				instance.appendLog(line);
			}
		} catch (IOException e) {
		}
		IOUtils.closeQuietly(input);
		IOUtils.closeQuietly(errorInput);
		if (process != null) {
			process.destroy();
		}
		setState(ProcessState.STOPPED);
	}
}
