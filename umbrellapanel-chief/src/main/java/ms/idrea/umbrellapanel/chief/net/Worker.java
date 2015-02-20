package ms.idrea.umbrellapanel.chief.net;

import io.netty.channel.Channel;
import lombok.Getter;
import ms.idrea.umbrellapanel.net.DynamicSession;

import com.flowpowered.networking.protocol.AbstractProtocol;

public class Worker extends DynamicSession {

	@Getter
	private int id = -1;

	public Worker(Channel channel, AbstractProtocol bootstrapProtocol) {
		super(channel, bootstrapProtocol);
	}

	public void setId(int id) {
	//	new RuntimeException().printStackTrace();
		this.id = id;
	}
}
