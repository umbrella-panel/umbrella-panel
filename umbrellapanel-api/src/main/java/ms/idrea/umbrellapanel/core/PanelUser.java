package ms.idrea.umbrellapanel.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PanelUser {

	private final int id;
	private String name;
	private String password;
}
