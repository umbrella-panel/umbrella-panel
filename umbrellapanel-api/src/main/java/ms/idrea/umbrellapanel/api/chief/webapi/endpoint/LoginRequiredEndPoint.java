package ms.idrea.umbrellapanel.api.chief.webapi.endpoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;

public abstract class LoginRequiredEndPoint extends BasicEndPointHandler {

	public static final EndPointResponse NAME_ALREADY_TAKEN = makeJSONResponse(HttpServletResponse.SC_CONFLICT, "error", "name is already taken");
	public static final EndPointResponse USER_NOT_FOUND = makeJSONResponse(HttpServletResponse.SC_NOT_FOUND, "error", "user not found");
	public static final EndPointResponse NEW_PASSWORD_TOO_SHORT = makeJSONResponse(HttpServletResponse.SC_BAD_REQUEST, "error", "new password is too short");
	public static final EndPointResponse WORKER_OFFLINE = makeJSONResponse(HttpServletResponse.SC_CONFLICT, "error", "worker is offline");
	public static final EndPointResponse SERVER_NO_FOUND_RESPONSE = makeJSONResponse(HttpServletResponse.SC_NOT_FOUND, "error", "server not found");
	public static final EndPointResponse FORBIDDEN_RESPONSE = makeJSONResponse(HttpServletResponse.SC_FORBIDDEN, "error", "authentication required");

	public LoginRequiredEndPoint(EndPointManager manager, String endpoint) {
		super(manager, endpoint);
	}

	@Override
	public final boolean isVaild(HttpServletRequest request) {
		return request.getParameter("sessId") != null && _isValid(request);
	}

	@Override
	public final EndPointResponse getResponse(HttpServletRequest request) {
		PanelUser user = manager.getSession(request.getParameter("sessId"));
		if (user != null) {
			return _getResponse(request, user);
		} else {
			return FORBIDDEN_RESPONSE;
		}
	}

	public abstract boolean _isValid(HttpServletRequest request);

	public abstract EndPointResponse _getResponse(HttpServletRequest request, PanelUser user);
}
