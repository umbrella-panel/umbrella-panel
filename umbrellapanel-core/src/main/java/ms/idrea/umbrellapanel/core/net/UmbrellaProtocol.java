package ms.idrea.umbrellapanel.core.net;

import ms.idrea.umbrellapanel.core.net.codecs.UpdateGameServerMessageCodec;
import ms.idrea.umbrellapanel.core.net.codecs.DispatchCommandMessageCodec;
import ms.idrea.umbrellapanel.core.net.codecs.LogMessageCodec;
import ms.idrea.umbrellapanel.core.net.codecs.ManageGameServerMessageCodec;
import ms.idrea.umbrellapanel.core.net.codecs.UpdatePanelUserMessageCodec;
import ms.idrea.umbrellapanel.core.net.codecs.WorkerMessageCodec;
import ms.idrea.umbrellapanel.core.net.messages.UpdateGameServerMessage;
import ms.idrea.umbrellapanel.core.net.messages.DispatchCommandMessage;
import ms.idrea.umbrellapanel.core.net.messages.LogMessage;
import ms.idrea.umbrellapanel.core.net.messages.ManageGameServerMessage;
import ms.idrea.umbrellapanel.core.net.messages.UpdatePanelUserMessage;
import ms.idrea.umbrellapanel.core.net.messages.WorkerMessage;
import io.netty.buffer.ByteBuf;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.Codec.CodecRegistration;
import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;
import com.flowpowered.networking.exception.IllegalOpcodeException;
import com.flowpowered.networking.exception.UnknownPacketException;
import com.flowpowered.networking.protocol.simple.SimpleProtocol;

public class UmbrellaProtocol extends SimpleProtocol {

	public UmbrellaProtocol(Class<? extends MessageHandler<DynamicSession, Message>> handler) {
		super("UmbrellaProtocol", 6);
		registerMessage(LogMessage.class, LogMessageCodec.class, handler, null);
		registerMessage(ManageGameServerMessage.class, ManageGameServerMessageCodec.class, handler, null);
		registerMessage(UpdateGameServerMessage.class, UpdateGameServerMessageCodec.class, handler, null);
		registerMessage(DispatchCommandMessage.class, DispatchCommandMessageCodec.class, handler, null);
		registerMessage(UpdatePanelUserMessage.class, UpdatePanelUserMessageCodec.class, handler, null);
		registerMessage(WorkerMessage.class, WorkerMessageCodec.class, handler, null);
	}

	@Override
	public Codec<?> readHeader(ByteBuf buf) throws UnknownPacketException {
		int id = buf.readUnsignedShort();
		int length = buf.readInt();
		try {
			return getCodecLookupService().find(id);
		} catch (IllegalOpcodeException e) {
			throw new UnknownPacketException("Packet not found!", id, length);
		}
	}

	@Override
	public ByteBuf writeHeader(ByteBuf header, CodecRegistration codec, ByteBuf data) {
		header.writeShort(codec.getOpcode());
		header.writeInt(data.writerIndex());
		return header;
	}

	@Override
	public <M extends Message> MessageHandler<?, M> getMessageHandle(Class<M> message) {
		MessageHandler<?, M> handle = super.getMessageHandle(message);
		if (handle == null) {
			System.out.println("Null handle");
			System.out.println(message);
			System.out.println(getHandlerLookupService());
		}
		return handle;
	}
}
