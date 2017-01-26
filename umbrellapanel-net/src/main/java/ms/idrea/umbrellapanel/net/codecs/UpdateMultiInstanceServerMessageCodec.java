package ms.idrea.umbrellapanel.net.codecs;

import java.io.IOException;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;

import io.netty.buffer.ByteBuf;

import ms.idrea.umbrellapanel.net.messages.UpdateMultiInstanceServerMessage;
import ms.idrea.umbrellapanel.net.messages.UpdateGameServerMessage.Action;

public class UpdateMultiInstanceServerMessageCodec implements Codec<UpdateMultiInstanceServerMessage> {

	@Override
	public UpdateMultiInstanceServerMessage decode(ByteBuf buf) throws IOException {
		String action = ByteBufUtils.readUTF8(buf);
		int id = buf.readInt();
		String startCommand = ByteBufUtils.readUTF8(buf);
		return new UpdateMultiInstanceServerMessage(Action.valueOf(action), id, startCommand);
	}

	@Override
	public ByteBuf encode(ByteBuf buf, UpdateMultiInstanceServerMessage message) throws IOException {
		ByteBufUtils.writeUTF8(buf, message.getAction().toString());
		buf.writeInt(message.getId());
		ByteBufUtils.writeUTF8(buf, message.getStartCommand());
		return buf;
	}
}
