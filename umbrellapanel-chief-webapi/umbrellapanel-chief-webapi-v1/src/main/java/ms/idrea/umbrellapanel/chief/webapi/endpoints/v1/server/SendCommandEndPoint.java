package ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.chief.webapi.endpoint.ServerEndPoint;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.core.permissions.Permission;
import ms.idrea.umbrellapanel.api.gameserver.ManagedServer;
import ms.idrea.umbrellapanel.api.gameserver.ManagedServerInstance;

public class SendCommandEndPoint extends ServerEndPoint {

	public static final EndPointResponse COMMAND_DISPATCHED = makeJSONResponse(HttpServletResponse.SC_OK, "ok", "command dispatched");

	public SendCommandEndPoint(EndPointManager manager) {
		super(manager, "v1/server/sendcommand", Permission.PANEL_ACCESS);
	}

	@Override
	public boolean __isValid(HttpServletRequest request) {
		return request.getParameter("command") != null;
	}

	@Override
	public EndPointResponse __getResponse(HttpServletRequest request, PanelUser user, ManagedServer server) {
		if (server.getOnlineWorker() == null) {
			return WORKER_OFFLINE;
		} else if (((ManagedServerInstance) server).dispatchCommand(request.getParameter("command"))) {
			return COMMAND_DISPATCHED;
		} else {
			return SERVER_NOT_RUNNING;
		}
	}
}
