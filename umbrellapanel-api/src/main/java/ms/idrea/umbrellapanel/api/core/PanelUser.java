package ms.idrea.umbrellapanel.api.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = { "password" })
@AllArgsConstructor
public class PanelUser {

	private final int id;
	private String name;
	private String password;
}
