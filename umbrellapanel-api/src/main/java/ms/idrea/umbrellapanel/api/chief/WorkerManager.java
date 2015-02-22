package ms.idrea.umbrellapanel.api.chief;

import java.util.List;

import ms.idrea.umbrellapanel.api.core.LoadAndSaveable;

import com.flowpowered.networking.session.Session;

public interface WorkerManager extends LoadAndSaveable {

	public List<? extends Session> getAllWorkers();

	public Object getWorker(int id);

	public Session getRunningWorker(int id);

	public void onRegister(Session worker);

	public void onStart(Session worker, int id);

	public void onStop(Session worker);

	public int getNextId();
}
