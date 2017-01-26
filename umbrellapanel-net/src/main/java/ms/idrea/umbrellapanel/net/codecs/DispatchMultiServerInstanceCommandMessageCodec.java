package ms.idrea.umbrellapanel.net.codecs;

import java.io.IOException;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;

import io.netty.buffer.ByteBuf;

import ms.idrea.umbrellapanel.net.messages.DispatchMultiServerInstanceCommandMessage;

public class DispatchMultiServerInstanceCommandMessageCodec implements Codec<DispatchMultiServerInstanceCommandMessage> {

	@Override
	public DispatchMultiServerInstanceCommandMessage decode(ByteBuf buf) throws IOException {
		int serverId = buf.readInt();
		int instanceId = buf.readInt();
		String command = ByteBufUtils.readUTF8(buf);
		return new DispatchMultiServerInstanceCommandMessage(serverId, instanceId, command);
	}

	@Override
	public ByteBuf encode(ByteBuf buf, DispatchMultiServerInstanceCommandMessage message) throws IOException {
		buf.writeInt(message.getServerId());
		buf.writeInt(message.getInstanceId());
		ByteBufUtils.writeUTF8(buf, message.getCommand());
		return buf;
	}
}
