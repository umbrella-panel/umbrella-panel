package ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.servers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.chief.webapi.endpoint.LoginRequiredEndPoint;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.core.permissions.Permission;
import ms.idrea.umbrellapanel.api.gameserver.ManagedServer;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.V1EndPoints;

public class ListEndPoint extends LoginRequiredEndPoint {

	public ListEndPoint(EndPointManager manager) {
		super(manager, "v1/servers/list");
	}

	@Override
	public boolean _isValid(HttpServletRequest request) {
		return true;
	}

	@Override
	public EndPointResponse _getResponse(HttpServletRequest request, PanelUser user) {
		BasicDBObject object = new BasicDBObject();
		BasicDBList servers = new BasicDBList();
		for (ManagedServer server : chief.getServerManager().getAllServers()) {
			if (user.hasPermission(server.getId(), Permission.PANEL_ACCESS)) {
				servers.add(V1EndPoints.convertGameServer(server));
			}
		}
		object.append("servers", servers);
		return new EndPointResponse(HttpServletResponse.SC_OK, object.toString());
	}
}
