package ms.idrea.umbreallapanel.core.net;

import ms.idrea.umbreallapanel.core.net.codecs.TextMessageCodec;
import ms.idrea.umbrellapanel.core.net.messages.TextMessage;
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
		super("UmbrellaProtocol", 5);
		registerMessage(TextMessage.class, TextMessageCodec.class, handler, null);
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
