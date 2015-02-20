package ms.idrea.umbrellapanel.api.chief;

import java.util.List;

import com.flowpowered.networking.session.Session;

public interface WorkerManager {

	public List<? extends Session> getAllWorkers();

	public Object getWorker(int id);

	public Session getRunningWorker(int id);

	public void onRegister(Session worker);

	public void onStart(Session worker, int id);

	public void onStop(Session worker);

	public int getNextId();
}
