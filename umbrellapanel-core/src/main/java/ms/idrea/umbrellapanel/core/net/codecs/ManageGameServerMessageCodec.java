package ms.idrea.umbrellapanel.core.net.codecs;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import ms.idrea.umbrellapanel.core.net.messages.ManageGameServerMessage;
import ms.idrea.umbrellapanel.core.net.messages.ManageGameServerMessage.Action;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;

public class ManageGameServerMessageCodec implements Codec<ManageGameServerMessage> {

	@Override
	public ManageGameServerMessage decode(ByteBuf buf) throws IOException {
		String action = ByteBufUtils.readUTF8(buf);
		int id = buf.readInt();
		
		return new ManageGameServerMessage(Action.valueOf(action), id);
	}

	@Override
	public ByteBuf encode(ByteBuf buf, ManageGameServerMessage message) throws IOException {
		ByteBufUtils.writeUTF8(buf, message.getAction().toString());
		buf.writeInt(message.getId());
		return buf;
	}
}
