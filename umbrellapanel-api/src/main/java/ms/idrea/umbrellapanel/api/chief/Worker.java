package ms.idrea.umbrellapanel.api.chief;

import ms.idrea.umbrellapanel.api.util.Address;

public interface Worker {

	int getId();

	/**
	 * May be <code>null</code> is the worker is not {@link #isOnline()}
	 */
	Address getAddress();

	boolean isOnline();
}
