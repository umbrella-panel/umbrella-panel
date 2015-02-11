package ms.idrea.umbrellapanel.core.net.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import ms.idrea.umbrellapanel.util.Address;
import ms.idrea.umbrellapanel.worker.GameServer;
import ms.idrea.umbrellapanel.worker.Worker;

import com.flowpowered.networking.Message;

@Getter
@ToString
@AllArgsConstructor
public class CreateGameServerMessage implements Message {

	private int id;
	private int userId;
	private Address address;
	/**
	 * When the {@link Worker} was restarting and the {@link GameServer} was already assigned to this {@link Worker}, this should be <code>true</code>.
	 */
	private boolean noSetup;
}
