package ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.users;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;

import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.chief.webapi.endpoint.LoginRequiredEndPoint;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.core.permissions.Permission;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.V1EndPoints;

public class CreateEndPoint extends LoginRequiredEndPoint {

	public CreateEndPoint(EndPointManager manager) {
		super(manager, "v1/users/create");
	}

	@Override
	public boolean _isValid(HttpServletRequest request) {
		return request.getParameter("name") != null && request.getParameter("password") != null;
	}

	@Override
	public EndPointResponse _getResponse(HttpServletRequest request, PanelUser user) {
		if (!user.hasGlobalPermission(Permission.ADMIN)) {
			return INSUFFICIENT_PERMISSIONS_RESPONSE;
		}
		String password = request.getParameter("password");
		if (password.length() < 8) {
			return PASSWORD_TOO_SHORT;
		}
		String name = request.getParameter("name");
		if (chief.getPanelUserDatabase().getUser(name) != null) {
			return NAME_ALREADY_TAKEN;
		}
		PanelUser u = chief.getPanelUserDatabase().createUser(name, password);
		return new EndPointResponse(HttpServletResponse.SC_OK, new BasicDBObject("user", V1EndPoints.convertPanelUser(u)).append("ok", "user created").toString());
	}
}
