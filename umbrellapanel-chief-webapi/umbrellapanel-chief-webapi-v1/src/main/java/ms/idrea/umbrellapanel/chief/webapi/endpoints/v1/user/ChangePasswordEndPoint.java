package ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.chief.webapi.endpoint.LoginRequiredEndPoint;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;

public class ChangePasswordEndPoint extends LoginRequiredEndPoint {

	public static final EndPointResponse INVALID_LOGIN = makeJSONResponse(HttpServletResponse.SC_UNAUTHORIZED, "error", "oldpassword is correct");
	public static final EndPointResponse PASSWORD_UPDATED = makeJSONResponse(HttpServletResponse.SC_OK, "ok", "password updated");

	public ChangePasswordEndPoint(EndPointManager manager) {
		super(manager, "v1/user/changepassword");
	}

	@Override
	public boolean _isValid(HttpServletRequest request) {
		return request.getParameter("oldpassword") != null && request.getParameter("newpassword") != null;
	}

	@Override
	public EndPointResponse _getResponse(HttpServletRequest request, PanelUser user) {
		if (!user.canLogin(user.getName(), request.getParameter("oldpassword"))) {
			return INVALID_LOGIN;
		} else {
			String newPassword = request.getParameter("newpassword");
			if (newPassword.length() < 8) {
				return PASSWORD_TOO_SHORT;
			}
			user.setPassword(newPassword);
			return PASSWORD_UPDATED;
		}
	}
}
