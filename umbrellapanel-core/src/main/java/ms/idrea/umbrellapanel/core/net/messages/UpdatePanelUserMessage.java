package ms.idrea.umbrellapanel.core.net.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import ms.idrea.umbrellapanel.core.PanelUser;

import com.flowpowered.networking.Message;

@Getter
@ToString
@AllArgsConstructor
public class UpdatePanelUserMessage implements Message {

	private PanelUser panelUser;
}
