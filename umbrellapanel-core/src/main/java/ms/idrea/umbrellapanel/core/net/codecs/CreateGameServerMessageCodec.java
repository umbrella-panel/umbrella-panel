package ms.idrea.umbrellapanel.core.net.codecs;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import ms.idrea.umbrellapanel.core.PanelUser;
import ms.idrea.umbrellapanel.core.net.messages.CreateGameServerMessage;
import ms.idrea.umbrellapanel.util.Address;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;

public class CreateGameServerMessageCodec implements Codec<CreateGameServerMessage> {

	@Override
	public CreateGameServerMessage decode(ByteBuf buf) throws IOException {
		int id = buf.readInt();
		String name = ByteBufUtils.readUTF8(buf);
		String password = ByteBufUtils.readUTF8(buf);
		String ip = ByteBufUtils.readUTF8(buf);
		int port = buf.readInt();
		boolean noSetup = buf.readBoolean();
		return new CreateGameServerMessage(id, new PanelUser(name, password), new Address(ip, port), noSetup);
	}

	@Override
	public ByteBuf encode(ByteBuf buf, CreateGameServerMessage message) throws IOException {
		buf.writeInt(message.getId());
		ByteBufUtils.writeUTF8(buf, message.getPanelUser().getName());
		ByteBufUtils.writeUTF8(buf, message.getPanelUser().getPassword());
		ByteBufUtils.writeUTF8(buf, message.getAddress().getIp());
		buf.writeInt(message.getAddress().getPort());
		buf.writeBoolean(message.isNoSetup());
		return buf;
	}
}
