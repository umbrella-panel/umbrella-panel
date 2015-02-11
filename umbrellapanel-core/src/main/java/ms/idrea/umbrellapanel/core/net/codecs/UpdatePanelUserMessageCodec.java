package ms.idrea.umbrellapanel.core.net.codecs;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import ms.idrea.umbrellapanel.core.PanelUser;
import ms.idrea.umbrellapanel.core.net.messages.UpdatePanelUserMessage;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;

public class UpdatePanelUserMessageCodec implements Codec<UpdatePanelUserMessage> {

	@Override
	public UpdatePanelUserMessage decode(ByteBuf buf) throws IOException {
		int id = buf.readInt();
		String name = ByteBufUtils.readUTF8(buf);
		String password = ByteBufUtils.readUTF8(buf);
		return new UpdatePanelUserMessage(new PanelUser(id, name, password));
	}

	@Override
	public ByteBuf encode(ByteBuf buf, UpdatePanelUserMessage message) throws IOException {
		buf.writeInt(message.getPanelUser().getId());
		ByteBufUtils.writeUTF8(buf, message.getPanelUser().getName());
		ByteBufUtils.writeUTF8(buf, message.getPanelUser().getPassword());
		return buf;
	}
}
