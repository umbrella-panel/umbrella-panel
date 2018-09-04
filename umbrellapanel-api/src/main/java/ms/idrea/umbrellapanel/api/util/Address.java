package ms.idrea.umbrellapanel.api.util;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Address {

	private String host;
	private int port;

	@Override
	public String toString() {
		return host + ":" + port;
	}
}
