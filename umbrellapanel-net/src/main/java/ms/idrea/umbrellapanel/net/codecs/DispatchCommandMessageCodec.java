package ms.idrea.umbrellapanel.net.codecs;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import ms.idrea.umbrellapanel.net.messages.DispatchCommandMessage;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;

public class DispatchCommandMessageCodec implements Codec<DispatchCommandMessage> {

	@Override
	public DispatchCommandMessage decode(ByteBuf buf) throws IOException {
		int id = buf.readInt();
		String command = ByteBufUtils.readUTF8(buf);
		return new DispatchCommandMessage(id, command);
	}

	@Override
	public ByteBuf encode(ByteBuf buf, DispatchCommandMessage message) throws IOException {
		buf.writeInt(message.getId());
		ByteBufUtils.writeUTF8(buf, message.getCommand());
		return buf;
	}
}
