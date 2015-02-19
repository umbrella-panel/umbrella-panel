package ms.idrea.umbrellapanel.core.net.codecs;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.core.net.messages.UpdateGameServerMessage;
import ms.idrea.umbrellapanel.core.net.messages.UpdateGameServerMessage.Action;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;

public class UpdateGameServerMessageCodec implements Codec<UpdateGameServerMessage> {

	@Override
	public UpdateGameServerMessage decode(ByteBuf buf) throws IOException {
		String action = ByteBufUtils.readUTF8(buf);
		int id = buf.readInt();
		int userId = buf.readInt();
		String ip = ByteBufUtils.readUTF8(buf);
		int port = buf.readInt();
		String startCommand = ByteBufUtils.readUTF8(buf);
		return new UpdateGameServerMessage(Action.valueOf(action), id, userId, new Address(ip, port), startCommand);
	}
	
	@Override
	public ByteBuf encode(ByteBuf buf, UpdateGameServerMessage message) throws IOException {
		ByteBufUtils.writeUTF8(buf, message.getAction().toString());
		buf.writeInt(message.getId());
		buf.writeInt(message.getUserId());
		ByteBufUtils.writeUTF8(buf, message.getAddress().getHost());
		buf.writeInt(message.getAddress().getPort());
		ByteBufUtils.writeUTF8(buf, message.getStartCommand());
		return buf;
	}
}
