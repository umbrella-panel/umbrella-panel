package ms.idrea.umbrellapanel.chief.webapi;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import ms.idrea.umbrellapanel.api.chief.Chief;
import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.chief.webapi.endpoint.EndPointHandler;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.util.Utils;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.V1EndPoints;

import org.eclipse.jetty.server.Server;

public class UmbrellaEndPointManager implements EndPointManager {

	@Getter
	private final Chief chief;
	private Map<String, EndPointHandler> endpointHandlers = new HashMap<>();
	private Map<String, Integer> sessions = new HashMap<String, Integer>();
	private SecureRandom random = new SecureRandom();
	private Server server;

	public UmbrellaEndPointManager(Chief chief) throws Exception {
		this.chief = chief;
		server = new Server(8080);
		server.setHandler(new RequestHandler(this));
		server.start();
		V1EndPoints.init(this);
	}

	@Override
	public void shutdown() {
		try {
			server.stop();
		} catch (Exception e) {
			new Exception("Failed to stop EndPointHandler", e).printStackTrace();
		}
	}

	@Override
	public PanelUser getSession(String sessId) {
		Integer userId = sessions.get(sessId);
		if (userId != null) {
			return chief.getPanelUserDatabase().getUser(userId);
		} else {
			return null;
		}
	}

	@Override
	public String createSession(PanelUser user) {
		for (String sessId : sessions.keySet()) {
			PanelUser o = getSession(sessId);
			if (o != null && o.getId() == user.getId()) {
				sessions.remove(sessId);
			}
		}
		if (user == null) {
			return null;
		} else {
			String sessId = new BigInteger(130, random).toString(32);
			sessions.put(sessId, user.getId());
			return sessId;
		}
	}

	@Override
	public void destroySession(String sessId) {
		PanelUser user = getSession(sessId);
		for (String o : sessions.keySet()) {
			PanelUser otherUser = getSession(o);
			if (otherUser != null && otherUser.getId() == user.getId()) {
				sessions.remove(o);
			}
		}
		// make sure we are always removing it.
		sessions.remove(sessId);
	}

	@Override
	public void registerEndPointHander(EndPointHandler handler) {
		endpointHandlers.put(handler.getEndPoint(), handler);
	}

	@Override
	public EndPointHandler getEndPointHandler(String endpoint) {
		endpoint = Utils.fixEndPointString(endpoint);
		return endpointHandlers.get(endpoint.toLowerCase());
	}

	@Override
	public Collection<EndPointHandler> getAllEndPoints() {
		return endpointHandlers.values();
	}
}
