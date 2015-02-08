package ms.idrea.umbrellapanel.core.net;

import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;
import io.netty.channel.Channel;

import com.flowpowered.networking.protocol.AbstractProtocol;
import com.flowpowered.networking.session.BasicSession;

public class DynamicSession extends BasicSession {

	public DynamicSession(Channel channel, AbstractProtocol bootstrapProtocol) {
		super(channel, bootstrapProtocol);
	}

	@Override
	public void setProtocol(AbstractProtocol protocol) {
		super.setProtocol(protocol);
	}

	@Override
	public void onHandlerThrowable(Message message, MessageHandler<?, ?> handle, Throwable throwable) {
		throwable.printStackTrace();
	}

	@Override
	public void onOutboundThrowable(Throwable throwable) {
		throwable.printStackTrace();
	}

	@Override
	public void onInboundThrowable(Throwable throwable) {
		throwable.printStackTrace();
	}
}
