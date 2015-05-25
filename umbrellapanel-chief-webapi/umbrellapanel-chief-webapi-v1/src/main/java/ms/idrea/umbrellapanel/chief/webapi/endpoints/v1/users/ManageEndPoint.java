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

public class ManageEndPoint extends LoginRequiredEndPoint {

	public static final EndPointResponse PASSWORD_UPDATED = makeJSONResponse(HttpServletResponse.SC_OK, "ok", "password updated");
	public static final EndPointResponse USER_DELETED = makeJSONResponse(HttpServletResponse.SC_OK, "ok", "user deleted");

	public ManageEndPoint(EndPointManager manager) {
		super(manager, "v1/users/manage");
	}

	@Override
	public boolean _isValid(HttpServletRequest request) {
		return Utils.isInteger(request.getParameter("id")) && request.getParameter("action") != null;
	}

	@Override
	public EndPointResponse _getResponse(HttpServletRequest request, PanelUser user) {
		if (!user.hasGlobalPermission(Permission.ADMIN)) {
			return INSUFFICIENT_PERMISSIONS_RESPONSE;
		}
		PanelUser o = chief.getPanelUserDatabase().getUser(Integer.valueOf(request.getParameter("id")));
		if (o == null) {
			return USER_NOT_FOUND;
		}
		String action = request.getParameter("action");
		if (action.equalsIgnoreCase("updatename")) {
			String newName = request.getParameter("newname");
			if (newName != null) {
				if (chief.getPanelUserDatabase().getUser(newName) != null) {
					return NAME_ALREADY_TAKEN;
				}
				o.setName(newName);
				return new EndPointResponse(HttpServletResponse.SC_OK, new BasicDBObject("user", V1EndPoints.convertPanelUser(o)).append("ok", "name updated").toString());
			} else {
				return INVALID_PARAMETERS;
			}
		} else if (action.equalsIgnoreCase("setpassword")) {
			String newPassword = request.getParameter("newpassword");
			if (newPassword != null) {
				if (newPassword.length() < 8) {
					return NEW_PASSWORD_TOO_SHORT;
				} else {
					o.setPassword(newPassword);
					return PASSWORD_UPDATED;
				}
			} else {
				return INVALID_PARAMETERS;
			}
		} else if (action.equalsIgnoreCase("delete")) {
			chief.getPanelUserDatabase().deleteUser(o);
			return USER_DELETED;
		} else if (action.equalsIgnoreCase("setpermission")) {
			if (Utils.isInteger(request.getParameter("level"))) {
				int level = Integer.valueOf(request.getParameter("level"));
				int server = -1;
				if (Utils.isInteger(request.getParameter("server"))) {
					server = Integer.valueOf(request.getParameter("server"));
				}
				if (level <= 0) {
					o.getPermissions().remove(server);
				} else {
					o.grantPermission(server, level);
				}
				return new EndPointResponse(HttpServletResponse.SC_OK, new BasicDBObject("user", V1EndPoints.convertPanelUser(o)).append("ok", "permission set").toString());
			} else {
				return INVALID_PARAMETERS;
			}
		}
		return INVALID_PARAMETERS;
	}
}
