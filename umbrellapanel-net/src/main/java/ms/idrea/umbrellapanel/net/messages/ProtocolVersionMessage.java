package ms.idrea.umbrellapanel.net.messages;

import com.flowpowered.networking.Message;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class ProtocolVersionMessage implements Message {

	private final int version;
}
