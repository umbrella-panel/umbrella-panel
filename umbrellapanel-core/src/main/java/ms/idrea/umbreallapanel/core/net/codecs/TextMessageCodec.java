package ms.idrea.umbreallapanel.core.net.codecs;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import ms.idrea.umbrellapanel.core.net.messages.TextMessage;

import com.flowpowered.networking.Codec;

public class TextMessageCodec implements Codec<TextMessage> {

	@Override
	public TextMessage decode(ByteBuf buffer) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf encode(ByteBuf buf, TextMessage message) throws IOException {
		throw new UnsupportedOperationException();
	}
}
