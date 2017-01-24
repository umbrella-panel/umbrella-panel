package ms.idrea.umbrellapanel.api.chief.net;

import com.flowpowered.networking.session.Session;

import ms.idrea.umbrellapanel.api.chief.Worker;

public interface RunningWorker extends Session {

	int getId();

	Worker getOfflineWorker();
}
