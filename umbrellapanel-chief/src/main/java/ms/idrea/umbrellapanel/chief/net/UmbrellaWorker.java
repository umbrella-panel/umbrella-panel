package ms.idrea.umbrellapanel.chief.net;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import ms.idrea.umbrellapanel.api.chief.Worker;
import ms.idrea.umbrellapanel.api.chief.WorkerManager;
import ms.idrea.umbrellapanel.api.chief.net.RunningWorker;
import ms.idrea.umbrellapanel.net.DynamicSession;

import com.flowpowered.networking.protocol.AbstractProtocol;

public class UmbrellaWorker extends DynamicSession implements RunningWorker {

	@Setter
	@Getter
	private int id = -1;
	private WorkerManager workerManager;

	public UmbrellaWorker(WorkerManager workerManager, Channel channel, AbstractProtocol bootstrapProtocol) {
		super(channel, bootstrapProtocol);
		this.workerManager = workerManager;
	}

	@Override
	public Worker getOfflineWorker() {
		return workerManager.getWorker(id);
	}
}
