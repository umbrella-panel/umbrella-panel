package ms.idrea.umbrellapanel.web.net;

import io.netty.channel.Channel;

import com.flowpowered.networking.protocol.AbstractProtocol;

import ms.idrea.umbrellapanel.core.net.DynamicSession;

public class Worker extends DynamicSession {

	public Worker(Channel channel, AbstractProtocol bootstrapProtocol) {
		super(channel, bootstrapProtocol);
		// TODO Auto-generated constructor stub
	}

}
