package ms.idrea.umbrellapanel.net.codecs;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import ms.idrea.umbrellapanel.net.messages.GameServerStatusMessage;
import ms.idrea.umbrellapanel.net.messages.GameServerStatusMessage.Status;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;

public class GameServerStatusMessageCodec implements Codec<GameServerStatusMessage> {

	@Override
	public GameServerStatusMessage decode(ByteBuf buf) throws IOException {
		int id = buf.readInt();
		String status = ByteBufUtils.readUTF8(buf);
		return new GameServerStatusMessage(id, Status.valueOf(status));
	}

	@Override
	public ByteBuf encode(ByteBuf buf, GameServerStatusMessage message) throws IOException {
		buf.writeInt(message.getId());
		ByteBufUtils.writeUTF8(buf, message.getStatus().toString());
		return buf;
	}
}
