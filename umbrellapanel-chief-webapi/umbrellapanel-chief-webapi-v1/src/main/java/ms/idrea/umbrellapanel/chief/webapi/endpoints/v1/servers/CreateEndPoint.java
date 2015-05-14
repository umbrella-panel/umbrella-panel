package ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.servers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;

import ms.idrea.umbrellapanel.api.chief.gameserver.GameServer;
import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.chief.webapi.endpoint.LoginRequiredEndPoint;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.core.permissions.Permission;
import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.api.util.Utils;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.V1EndPoints;

public class CreateEndPoint extends LoginRequiredEndPoint {

	public CreateEndPoint(EndPointManager manager) {
		super(manager, "v1/servers/create");
	}

	@Override
	public boolean _isValid(HttpServletRequest request) {
		return request.getParameter("ip") != null && Utils.isInteger(request.getParameter("port")) && request.getParameter("startcommand") != null && Utils.isInteger(request.getParameter("workerId"));
	}

	@Override
	public EndPointResponse _getResponse(HttpServletRequest request, PanelUser user) {
		if (!user.hasGlobalPermission(Permission.ADMIN)) {
			return INSUFFICIENT_PERMISSIONS_RESPONSE;
		}
		int workerId = Integer.valueOf(request.getParameter("workerId"));
		if (chief.getWorkerManager().getRunningWorker(workerId) == null) {
			return WORKER_OFFLINE;
		}
		GameServer server = chief.getServerManager().createServer(new Address(request.getParameter("ip"), Integer.valueOf(request.getParameter("port"))), request.getParameter("startcommand"), workerId);
		return new EndPointResponse(HttpServletResponse.SC_CREATED, new BasicDBObject("server", V1EndPoints.convertGameServer(server)).append("ok", "server created").toString());
	}
}
