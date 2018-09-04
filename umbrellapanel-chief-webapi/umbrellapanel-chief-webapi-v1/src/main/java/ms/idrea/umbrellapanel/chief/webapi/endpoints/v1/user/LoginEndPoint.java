package ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;

import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.chief.webapi.endpoint.BasicEndPointHandler;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.V1EndPoints;

public class LoginEndPoint extends BasicEndPointHandler {

	public static final EndPointResponse INVALID_LOGIN = makeJSONResponse(HttpServletResponse.SC_UNAUTHORIZED, "error", "username or password are not correct");
	public static final EndPointResponse SESSID_NOT_VALID = makeJSONResponse(HttpServletResponse.SC_UNAUTHORIZED, "error", "provided sessId is not valid");

	public LoginEndPoint(EndPointManager manager) {
		super(manager, "v1/user/login");
	}

	@Override
	public boolean isVaild(HttpServletRequest request) {
		return (request.getParameter("name") != null && request.getParameter("password") != null) || request.getParameter("sessId") != null;
	}

	@Override
	public EndPointResponse getResponse(HttpServletRequest request) {
		if (request.getParameter("sessId") != null) {
			String sessId = request.getParameter("sessId");
			PanelUser user = manager.getSession(sessId);
			if (user != null) {
				return new EndPointResponse(HttpServletResponse.SC_OK, new BasicDBObject("user", V1EndPoints.convertPanelUser(user)).append("sessId", sessId).toString());
			} else {
				return SESSID_NOT_VALID;
			}
		} else {
			String name = request.getParameter("name");
			String password = request.getParameter("password");
			PanelUser user = chief.getPanelUserDatabase().getUser(name);
			if (user != null && user.canLogin(name, password)) {
				String sessId = manager.createSession(user);
				return new EndPointResponse(HttpServletResponse.SC_OK, new BasicDBObject("user", V1EndPoints.convertPanelUser(user)).append("sessId", sessId).toString());
			}
			return INVALID_LOGIN;
		}
	}
}
