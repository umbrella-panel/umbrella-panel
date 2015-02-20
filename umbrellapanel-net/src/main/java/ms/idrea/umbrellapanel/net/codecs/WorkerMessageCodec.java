package ms.idrea.umbrellapanel.net.codecs;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import ms.idrea.umbrellapanel.net.messages.WorkerMessage;
import ms.idrea.umbrellapanel.net.messages.WorkerMessage.Action;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;

public class WorkerMessageCodec implements Codec<WorkerMessage> {

	@Override
	public WorkerMessage decode(ByteBuf buf) throws IOException {
		int id = buf.readInt();
		String action = ByteBufUtils.readUTF8(buf);
		String sharedPassword = ByteBufUtils.readUTF8(buf);
		return new WorkerMessage(Action.valueOf(action), id, sharedPassword);
	}

	@Override
	public ByteBuf encode(ByteBuf buf, WorkerMessage message) throws IOException {
		buf.writeInt(message.getId());
		ByteBufUtils.writeUTF8(buf, message.getAction().toString());
		ByteBufUtils.writeUTF8(buf, message.getSharedPassword());
		return buf;
	}
}
