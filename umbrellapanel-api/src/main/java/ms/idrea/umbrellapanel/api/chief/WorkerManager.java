package ms.idrea.umbrellapanel.api.chief;

import java.util.List;

import com.flowpowered.networking.session.Session;

import ms.idrea.umbrellapanel.api.chief.net.RunningWorker;
import ms.idrea.umbrellapanel.api.core.LoadAndSaveable;

public interface WorkerManager extends LoadAndSaveable {

	List<RunningWorker> getAllWorkers();

	Worker getWorker(int id);

	Session getRunningWorker(int id);

	void onRegister(Session worker);

	void onStart(Session worker, int id);

	void onStop(Session worker);

	int getNextId();
}
