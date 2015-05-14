package ms.idrea.umbrellapanel.api.chief.webapi;

import java.util.Collection;

import ms.idrea.umbrellapanel.api.chief.Chief;
import ms.idrea.umbrellapanel.api.chief.webapi.endpoint.EndPointHandler;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;

public interface EndPointManager {

	public void shutdown();

	public PanelUser getSession(String sessId);

	public String createSession(PanelUser user);

	public void destroySession(String sessId);

	public void registerEndPointHander(EndPointHandler handler);

	public EndPointHandler getEndPointHandler(String endpoint);

	public Collection<EndPointHandler> getAllEndPoints();

	public Chief getChief();
}
