package ms.idrea.umbrellapanel.net.codecs;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import ms.idrea.umbrellapanel.net.messages.LogMessage;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;

public class LogMessageCodec implements Codec<LogMessage> {

	@Override
	public LogMessage decode(ByteBuf buf) throws IOException {
		int serverId = buf.readInt();
		int instanceId = buf.readInt();
		String line = ByteBufUtils.readUTF8(buf);
		return new LogMessage(serverId, instanceId, line);
	}

	@Override
	public ByteBuf encode(ByteBuf buf, LogMessage message) throws IOException {
		buf.writeInt(message.getServerId());
		buf.writeInt(message.getInstanceId());
		ByteBufUtils.writeUTF8(buf, message.getLine());
		return buf;
	}
}
