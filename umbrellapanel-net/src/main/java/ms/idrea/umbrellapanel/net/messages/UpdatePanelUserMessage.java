package ms.idrea.umbrellapanel.net.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;

import com.flowpowered.networking.Message;

@Getter
@ToString
@AllArgsConstructor
public class UpdatePanelUserMessage implements Message {

	public static enum Action {
		UPDATE,
		DELETE;
	}

	private Action action;
	private PanelUser panelUser;
}
