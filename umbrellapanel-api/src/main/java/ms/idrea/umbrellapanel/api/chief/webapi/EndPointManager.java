package ms.idrea.umbrellapanel.api.chief.webapi;

import java.util.Collection;

import ms.idrea.umbrellapanel.api.chief.Chief;
import ms.idrea.umbrellapanel.api.chief.webapi.endpoint.EndPointHandler;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;

public interface EndPointManager {

	void shutdown();

	PanelUser getSession(String sessId);

	String createSession(PanelUser user);

	void destroySession(String sessId);

	void registerEndPointHander(EndPointHandler handler);

	EndPointHandler getEndPointHandler(String endpoint);

	Collection<EndPointHandler> getAllEndPoints();

	Chief getChief();
}
