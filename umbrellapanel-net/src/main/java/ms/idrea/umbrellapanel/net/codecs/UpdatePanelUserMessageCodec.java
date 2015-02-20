package ms.idrea.umbrellapanel.net.codecs;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import ms.idrea.umbrellapanel.api.core.PanelUser;
import ms.idrea.umbrellapanel.net.messages.UpdatePanelUserMessage;
import ms.idrea.umbrellapanel.net.messages.UpdatePanelUserMessage.Action;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;

public class UpdatePanelUserMessageCodec implements Codec<UpdatePanelUserMessage> {

	@Override
	public UpdatePanelUserMessage decode(ByteBuf buf) throws IOException {
		String action = ByteBufUtils.readUTF8(buf);
		int id = buf.readInt();
		String name = ByteBufUtils.readUTF8(buf);
		String password = ByteBufUtils.readUTF8(buf);
		return new UpdatePanelUserMessage(Action.valueOf(action), new PanelUser(id, name, password));
	}

	@Override
	public ByteBuf encode(ByteBuf buf, UpdatePanelUserMessage message) throws IOException {
		ByteBufUtils.writeUTF8(buf, message.getAction().toString());
		buf.writeInt(message.getPanelUser().getId());
		ByteBufUtils.writeUTF8(buf, message.getPanelUser().getName());
		ByteBufUtils.writeUTF8(buf, message.getPanelUser().getPassword());
		return buf;
	}
}
