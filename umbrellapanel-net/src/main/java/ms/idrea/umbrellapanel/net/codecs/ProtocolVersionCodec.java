package ms.idrea.umbrellapanel.net.codecs;

import java.io.IOException;

import com.flowpowered.networking.Codec;

import io.netty.buffer.ByteBuf;

import ms.idrea.umbrellapanel.net.messages.ProtocolVersionMessage;

public class ProtocolVersionCodec implements Codec<ProtocolVersionMessage> {

	@Override
	public ProtocolVersionMessage decode(ByteBuf buf) throws IOException {
		int version = buf.readInt();
		return new ProtocolVersionMessage(version);
	}

	@Override
	public ByteBuf encode(ByteBuf buf, ProtocolVersionMessage message) throws IOException {
		buf.writeInt(message.getVersion());
		return buf;
	}
}
