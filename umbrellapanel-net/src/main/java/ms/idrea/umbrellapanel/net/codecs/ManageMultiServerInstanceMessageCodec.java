package ms.idrea.umbrellapanel.net.codecs;

import java.io.IOException;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;

import io.netty.buffer.ByteBuf;

import ms.idrea.umbrellapanel.net.messages.ManageGameServerMessage.Action;
import ms.idrea.umbrellapanel.net.messages.ManageMultiServerInstanceMessage;

public class ManageMultiServerInstanceMessageCodec implements Codec<ManageMultiServerInstanceMessage> {

	@Override
	public ManageMultiServerInstanceMessage decode(ByteBuf buf) throws IOException {
		String action = ByteBufUtils.readUTF8(buf);
		int serverId = buf.readInt();
		int instanceId = buf.readInt();
		return new ManageMultiServerInstanceMessage(Action.valueOf(action), serverId, instanceId);
	}

	@Override
	public ByteBuf encode(ByteBuf buf, ManageMultiServerInstanceMessage message) throws IOException {
		ByteBufUtils.writeUTF8(buf, message.getAction().toString());
		buf.writeInt(message.getServerId());
		buf.writeInt(message.getInstanceId());
		return buf;
	}
}
