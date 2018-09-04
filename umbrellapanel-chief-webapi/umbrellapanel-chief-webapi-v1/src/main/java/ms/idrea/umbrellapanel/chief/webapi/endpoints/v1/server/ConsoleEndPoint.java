package ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.chief.webapi.endpoint.ServerEndPoint;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.core.permissions.Permission;
import ms.idrea.umbrellapanel.api.gameserver.ManagedServer;
import ms.idrea.umbrellapanel.api.gameserver.ManagedServerInstance;
import ms.idrea.umbrellapanel.api.gameserver.ServerLog;
import ms.idrea.umbrellapanel.api.util.Utils;

public class ConsoleEndPoint extends ServerEndPoint {

	public ConsoleEndPoint(EndPointManager manager) {
		super(manager, "v1/server/console", Permission.PANEL_ACCESS);
	}

	@Override
	public boolean __isValid(HttpServletRequest request) {
		return Utils.isLong(request.getParameter("time"));
	}

	@Override
	public EndPointResponse __getResponse(HttpServletRequest request, PanelUser user, ManagedServer server) {
		BasicDBList data = new BasicDBList();
		for (ServerLog log : ((ManagedServerInstance) server).getLogBuffer()) {
			if (log.getTimestamp() > Long.valueOf(request.getParameter("time"))) {
				data.add(StringEscapeUtils.escapeHtml4(log.getMessage()));
			}
		}
		BasicDBObject json = new BasicDBObject();
		json.append("time", System.currentTimeMillis());
		json.append("data", data);
		return new EndPointResponse(HttpServletResponse.SC_OK, json.toString());
	}
}
