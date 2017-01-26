package ms.idrea.umbrellapanel.net;

import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;
import com.flowpowered.networking.protocol.AbstractProtocol;
import com.flowpowered.networking.session.BasicSession;

import lombok.Getter;
import lombok.Setter;

import io.netty.channel.Channel;

import ms.idrea.umbrellapanel.net.messages.ProtocolVersionMessage;

public class DynamicSession extends BasicSession {

	@Getter
	@Setter
	private int remoteProtocolVersion;

	public DynamicSession(Channel channel, AbstractProtocol bootstrapProtocol) {
		super(channel, bootstrapProtocol);
	}

	@Override
	public void onReady() {
		super.onReady();
		// tell the client what version we are on.
		send(new ProtocolVersionMessage(UmbrellaProtocol.PROTOCOL_VERSION));
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

	@Override
	public String toString() {
		return getAddress().toString();
	}
}
