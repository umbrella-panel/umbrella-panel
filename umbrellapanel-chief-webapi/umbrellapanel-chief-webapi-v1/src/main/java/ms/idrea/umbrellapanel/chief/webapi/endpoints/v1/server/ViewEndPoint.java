package ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;

import ms.idrea.umbrellapanel.api.chief.gameserver.GameServer;
import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.chief.webapi.endpoint.ServerEndPoint;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.core.permissions.Permission;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.V1EndPoints;

public class ViewEndPoint extends ServerEndPoint {

	public ViewEndPoint(EndPointManager manager) {
		super(manager, "v1/server/view", Permission.PANEL_ACCESS);
	}

	@Override
	public boolean __isValid(HttpServletRequest request) {
		return true;
	}

	@Override
	public EndPointResponse __getResponse(HttpServletRequest request, PanelUser user, GameServer server) {
		return new EndPointResponse(HttpServletResponse.SC_OK, new BasicDBObject("server", V1EndPoints.convertGameServer(server)).toString());
	}
}
