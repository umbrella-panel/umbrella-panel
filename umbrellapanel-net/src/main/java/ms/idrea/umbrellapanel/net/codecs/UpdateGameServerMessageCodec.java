package ms.idrea.umbrellapanel.net.codecs;

import java.io.IOException;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;

import io.netty.buffer.ByteBuf;

import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.net.messages.UpdateGameServerMessage;
import ms.idrea.umbrellapanel.net.messages.UpdateGameServerMessage.Action;

public class UpdateGameServerMessageCodec implements Codec<UpdateGameServerMessage> {

	@Override
	public UpdateGameServerMessage decode(ByteBuf buf) throws IOException {
		String action = ByteBufUtils.readUTF8(buf);
		int id = buf.readInt();
		String ip = ByteBufUtils.readUTF8(buf);
		int port = buf.readInt();
		String startCommand = ByteBufUtils.readUTF8(buf);
		return new UpdateGameServerMessage(Action.valueOf(action), id, new Address(ip, port), startCommand);
	}

	@Override
	public ByteBuf encode(ByteBuf buf, UpdateGameServerMessage message) throws IOException {
		ByteBufUtils.writeUTF8(buf, message.getAction().toString());
		buf.writeInt(message.getId());
		ByteBufUtils.writeUTF8(buf, message.getAddress().getHost());
		buf.writeInt(message.getAddress().getPort());
		ByteBufUtils.writeUTF8(buf, message.getStartCommand());
		return buf;
	}
}
