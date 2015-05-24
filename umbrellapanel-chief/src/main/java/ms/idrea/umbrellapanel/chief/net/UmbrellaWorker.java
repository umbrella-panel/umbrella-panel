package ms.idrea.umbrellapanel.chief.net;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import ms.idrea.umbrellapanel.net.DynamicSession;

import com.flowpowered.networking.protocol.AbstractProtocol;

public class UmbrellaWorker extends DynamicSession {

	@Setter
	@Getter
	private int id = -1;

	public UmbrellaWorker(Channel channel, AbstractProtocol bootstrapProtocol) {
		super(channel, bootstrapProtocol);
	}
}
