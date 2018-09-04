package ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.users;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;

import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.chief.webapi.endpoint.LoginRequiredEndPoint;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.core.permissions.Permission;
import ms.idrea.umbrellapanel.api.util.Utils;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.V1EndPoints;

public class ViewEndPoint extends LoginRequiredEndPoint {

	public ViewEndPoint(EndPointManager manager) {
		super(manager, "v1/users/view");
	}

	@Override
	public boolean _isValid(HttpServletRequest request) {
		return Utils.isInteger(request.getParameter("id"));
	}

	@Override
	public EndPointResponse _getResponse(HttpServletRequest request, PanelUser user) {
		if (!user.hasGlobalPermission(Permission.ADMIN)) {
			return INSUFFICIENT_PERMISSIONS_RESPONSE;
		}
		PanelUser o = chief.getPanelUserDatabase().getUser(Integer.valueOf(request.getParameter("id")));
		if (o != null) {
			return new EndPointResponse(HttpServletResponse.SC_OK, new BasicDBObject("user", V1EndPoints.convertPanelUser(o)).toString());
		} else {
			return USER_NOT_FOUND;
		}
	}
}
