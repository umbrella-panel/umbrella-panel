package ms.idrea.umbrellapanel.api.chief.webapi.endpoint;

import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;

import ms.idrea.umbrellapanel.api.chief.Chief;
import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.util.Utils;

public abstract class BasicEndPointHandler implements EndPointHandler {

	public static final EndPointResponse INSUFFICIENT_PERMISSIONS_RESPONSE = makeJSONResponse(HttpServletResponse.SC_FORBIDDEN, "error", "insufficient permissions");
	public static final EndPointResponse INVALID_PARAMETERS = makeJSONResponse(HttpServletResponse.SC_BAD_REQUEST, "error", "invalid parameters");
	private final String endpoint;
	protected final EndPointManager manager;
	protected final Chief chief;

	/**
	 * Will register the {@link EndPointHandler} in the {@link EndPointManager}
	 */
	public BasicEndPointHandler(EndPointManager manager, String endpoint) {
		this.endpoint = Utils.fixEndPointString(endpoint);
		this.manager = manager;
		chief = manager.getChief();
		manager.registerEndPointHander(this);
	}

	@Override
	public String getEndPoint() {
		return endpoint;
	}

	@Override
	public EndPointResponse getInValidResponse() {
		return INVALID_PARAMETERS;
	}

	protected static EndPointResponse makeJSONResponse(int code, String... strings) {
		if (strings.length % 2 != 0 || strings.length == 0) {
			throw new IllegalArgumentException();
		}
		BasicDBObject object = new BasicDBObject();
		for (int i = 0; i < strings.length; i += 2) {
			object.append(strings[i], strings[i + 1]);
		}
		return new EndPointResponse(code, object.toString());
	}
}
