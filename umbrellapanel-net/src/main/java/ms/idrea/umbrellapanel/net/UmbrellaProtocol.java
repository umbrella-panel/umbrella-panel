package ms.idrea.umbrellapanel.net;

import io.netty.buffer.ByteBuf;

import ms.idrea.umbrellapanel.net.codecs.*;
import ms.idrea.umbrellapanel.net.messages.*;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.Codec.CodecRegistration;
import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;
import com.flowpowered.networking.exception.IllegalOpcodeException;
import com.flowpowered.networking.exception.UnknownPacketException;
import com.flowpowered.networking.protocol.simple.SimpleProtocol;

public class UmbrellaProtocol extends SimpleProtocol {

	public static final int PROTOCOL_VERSION = 1;

	public UmbrellaProtocol(Class<? extends MessageHandler<? extends DynamicSession, Message>> handler) {
		super("UmbrellaProtocol", 12);
		registerMessage(LogMessage.class, LogMessageCodec.class, handler, null);
		registerMessage(ManageGameServerMessage.class, ManageGameServerMessageCodec.class, handler, null);
		registerMessage(UpdateGameServerMessage.class, UpdateGameServerMessageCodec.class, handler, null);
		registerMessage(DispatchCommandMessage.class, DispatchCommandMessageCodec.class, handler, null);
		registerMessage(UpdatePanelUserMessage.class, UpdatePanelUserMessageCodec.class, handler, null);
		registerMessage(WorkerMessage.class, WorkerMessageCodec.class, handler, null);
		registerMessage(GameServerStatusMessage.class, GameServerStatusMessageCodec.class, handler, null);
		registerMessage(ProtocolVersionMessage.class, ProtocolVersionCodec.class, handler, null);
		registerMessage(UpdateMultiInstanceServerMessage.class, UpdateMultiInstanceServerMessageCodec.class, handler, null);
		registerMessage(UpdateServerInstanceMessage.class, UpdateServerInstanceMessageCodec.class, handler, null);
		registerMessage(ManageMultiServerInstanceMessage.class, ManageMultiServerInstanceMessageCodec.class, handler, null);
		registerMessage(DispatchMultiServerInstanceCommandMessage.class, DispatchMultiServerInstanceCommandMessageCodec.class, handler, null); // 12
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
