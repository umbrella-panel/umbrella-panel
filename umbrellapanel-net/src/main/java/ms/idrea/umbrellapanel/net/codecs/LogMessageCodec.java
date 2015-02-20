package ms.idrea.umbrellapanel.net.codecs;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ms.idrea.umbrellapanel.net.messages.LogMessage;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;

public class LogMessageCodec implements Codec<LogMessage> {

	@Override
	public LogMessage decode(ByteBuf buf) throws IOException {
		int id = buf.readInt();
		int size = buf.readInt();
		List<String> lines = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			lines.add(ByteBufUtils.readUTF8(buf));
		}
		return new LogMessage(id, lines);
	}

	@Override
	public ByteBuf encode(ByteBuf buf, LogMessage message) throws IOException {
		buf.writeInt(message.getId());
		buf.writeInt(message.getLines().size());
		for (String line : message.getLines()) {
			ByteBufUtils.writeUTF8(buf, line);
		}
		return buf;
	}
}
