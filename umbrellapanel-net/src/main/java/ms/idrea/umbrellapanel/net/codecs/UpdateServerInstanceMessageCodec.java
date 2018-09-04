package ms.idrea.umbrellapanel.net.codecs;

import java.io.IOException;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;

import io.netty.buffer.ByteBuf;

import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.net.messages.UpdateGameServerMessage.Action;
import ms.idrea.umbrellapanel.net.messages.UpdateServerInstanceMessage;

public class UpdateServerInstanceMessageCodec implements Codec<UpdateServerInstanceMessage> {

	@Override
	public UpdateServerInstanceMessage decode(ByteBuf buf) throws IOException {
		String action = ByteBufUtils.readUTF8(buf);
		int serverId = buf.readInt();
		int instanceId = buf.readInt();
		String ip = ByteBufUtils.readUTF8(buf);
		int port = buf.readInt();
		return new UpdateServerInstanceMessage(Action.valueOf(action), serverId, instanceId, new Address(ip, port));
	}

	@Override
	public ByteBuf encode(ByteBuf buf, UpdateServerInstanceMessage message) throws IOException {
		ByteBufUtils.writeUTF8(buf, message.getAction().toString());
		buf.writeInt(message.getServerId());
		buf.writeInt(message.getInstanceId());
		ByteBufUtils.writeUTF8(buf, message.getAddress().getHost());
		buf.writeInt(message.getAddress().getPort());
		return buf;
	}
}
