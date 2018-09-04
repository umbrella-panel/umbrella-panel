package ms.idrea.umbrellapanel.net.codecs;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
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
		PanelUser user = new PanelUser(id, name, password, true);
		int permissionsSize = buf.readInt();
		for (int i = 0; i < permissionsSize; i++) {
			int serverId = buf.readInt();
			int permission = buf.readInt();
			user.grantPermission(serverId, permission);
		}
		return new UpdatePanelUserMessage(Action.valueOf(action), user);
	}

	@Override
	public ByteBuf encode(ByteBuf buf, UpdatePanelUserMessage message) throws IOException {
		ByteBufUtils.writeUTF8(buf, message.getAction().toString());
		buf.writeInt(message.getPanelUser().getId());
		ByteBufUtils.writeUTF8(buf, message.getPanelUser().getName());
		ByteBufUtils.writeUTF8(buf, message.getPanelUser().getPassword());
		buf.writeInt(message.getPanelUser().getPermissions().size());
		for (int serverId : message.getPanelUser().getPermissions().keySet()) {
			buf.writeInt(serverId);
			buf.writeInt(message.getPanelUser().getPermission(serverId));
		}
		return buf;
	}
}
