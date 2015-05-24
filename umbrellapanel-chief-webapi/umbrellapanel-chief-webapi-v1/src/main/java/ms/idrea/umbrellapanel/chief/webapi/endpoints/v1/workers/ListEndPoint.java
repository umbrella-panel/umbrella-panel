package ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.workers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import ms.idrea.umbrellapanel.api.chief.net.RunningWorker;
import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.chief.webapi.endpoint.LoginRequiredEndPoint;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.core.permissions.Permission;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.V1EndPoints;


public class ListEndPoint extends LoginRequiredEndPoint {

	public ListEndPoint(EndPointManager manager) {
		super(manager, "v1/workers/list");
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
		for (RunningWorker w : chief.getWorkerManager().getAllWorkers()) {
			list.add(V1EndPoints.convertWorker(w.getOfflineWorker()));
		}
		object.append("workers", list);
		return new EndPointResponse(HttpServletResponse.SC_OK, object.toString());
	}
}
