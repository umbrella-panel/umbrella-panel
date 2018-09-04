package ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.chief.webapi.endpoint.LoginRequiredEndPoint;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;

public class LogoutEndPoint extends LoginRequiredEndPoint {

	public static final EndPointResponse LOGGED_OUT = makeJSONResponse(HttpServletResponse.SC_OK, "ok", "logged out");

	public LogoutEndPoint(EndPointManager manager) {
		super(manager, "v1/user/logout");
	}

	@Override
	public boolean _isValid(HttpServletRequest request) {
		return true;
	}

	@Override
	public EndPointResponse _getResponse(HttpServletRequest request, PanelUser user) {
		manager.destroySession(request.getParameter("sessId"));
		return LOGGED_OUT;
	}
}
