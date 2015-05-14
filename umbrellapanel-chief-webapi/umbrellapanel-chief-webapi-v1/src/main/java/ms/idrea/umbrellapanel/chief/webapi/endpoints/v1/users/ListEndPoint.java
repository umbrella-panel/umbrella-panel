package ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.users;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.chief.webapi.endpoint.LoginRequiredEndPoint;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.core.permissions.Permission;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.V1EndPoints;

public class ListEndPoint extends LoginRequiredEndPoint {

	public ListEndPoint(EndPointManager manager) {
		super(manager, "v1/users/list");
	}

	@Override
	public boolean _isValid(HttpServletRequest request) {
		return true;
	}

	@Override
	public EndPointResponse _getResponse(HttpServletRequest request, PanelUser user) {
		if (!user.hasGlobalPermission(Permission.ADMIN)) {
			return INSUFFICIENT_PERMISSIONS_RESPONSE;
		}
		BasicDBObject object = new BasicDBObject();
		BasicDBList list = new BasicDBList();
		for (PanelUser o : chief.getPanelUserDatabase().getAllUsers()) {
			list.add(V1EndPoints.convertPanelUser(o));
		}
		object.append("users", list);
		return new EndPointResponse(HttpServletResponse.SC_OK, object.toString());
	}
}
