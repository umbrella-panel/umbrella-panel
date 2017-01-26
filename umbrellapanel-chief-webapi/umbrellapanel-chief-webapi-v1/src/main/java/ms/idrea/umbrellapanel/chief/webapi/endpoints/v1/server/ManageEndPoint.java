package ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;

import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.chief.webapi.endpoint.ServerEndPoint;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.core.permissions.Permission;
import ms.idrea.umbrellapanel.api.gameserver.ManagedServer;
import ms.idrea.umbrellapanel.api.gameserver.ServerInstance;
import ms.idrea.umbrellapanel.api.util.Utils;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.V1EndPoints;

public class ManageEndPoint extends ServerEndPoint {

	public static final EndPointResponse ALREADY_RUNNING = makeJSONResponse(HttpServletResponse.SC_CONFLICT, "error", "server is already running");
	public static final EndPointResponse SERVER_DELETED = makeJSONResponse(HttpServletResponse.SC_OK, "ok", "server deleted");
	public static final EndPointResponse SERVER_STOPPED = makeJSONResponse(HttpServletResponse.SC_OK, "ok", "server stopped");
	public static final EndPointResponse SERVER_STARTED = makeJSONResponse(HttpServletResponse.SC_OK, "ok", "server started");
	public static final EndPointResponse INVALID_ACTION = makeJSONResponse(HttpServletResponse.SC_NOT_FOUND, "error", "action not found");
	public static final EndPointResponse CANT_UPDATE_WHILE_RUNNING = makeJSONResponse(HttpServletResponse.SC_CONFLICT, "error", "unable to update server while running");
	public static final EndPointResponse NOTHING_EDITED = makeJSONResponse(HttpServletResponse.SC_BAD_REQUEST, "error", "nothing to update specified");

	public ManageEndPoint(EndPointManager manager) {
		super(manager, "v1/server/manage", Permission.PANEL_ACCESS);
	}

	@Override
	public boolean __isValid(HttpServletRequest request) {
		return request.getParameter("action") != null;
	}

	@Override
	public EndPointResponse __getResponse(HttpServletRequest request, PanelUser user, ManagedServer server) {
		String action = request.getParameter("action");
		if (action.equalsIgnoreCase("update")) {
			if (!user.hasPermission(server.getId(), Permission.SERVER_SETTINGS)) {
				return INSUFFICIENT_PERMISSIONS_RESPONSE;
			}
			if (!((ServerInstance) server).isRunning()) {
				boolean edited = false;
				if (request.getParameter("servername") != null) {
					server.setName(request.getParameter("servername"));
					edited = true;
				}
				if (request.getParameter("ip") != null) {
					((ServerInstance) server).getAddress().setHost(request.getParameter("ip"));
					edited = true;
				}
				if (Utils.isInteger(request.getParameter("port"))) {
					((ServerInstance) server).getAddress().setPort(Integer.valueOf(request.getParameter("port")));
					edited = true;
				}
				if (request.getParameter("startcommand") != null) {
					server.setStartCommand(request.getParameter("startcommand"));
					edited = true;
				}
				if (edited) {
					// update only if the worker is only, if not he will get the information on next startup
					if (server.getOnlineWorker() != null) {
						server.update();
					}
					return new EndPointResponse(HttpServletResponse.SC_OK, new BasicDBObject("server", V1EndPoints.convertGameServer(server)).append("ok", "server updated").toString());
				} else {
					return NOTHING_EDITED;
				}
			} else {
				return CANT_UPDATE_WHILE_RUNNING;
			}
		}
		if (server.getOnlineWorker() == null) {
			return WORKER_OFFLINE;
		}
		if (action.equalsIgnoreCase("delete")) {
			if (!user.hasPermission(server.getId(), Permission.ADMIN)) {
				return INSUFFICIENT_PERMISSIONS_RESPONSE;
			}
			chief.getServerManager().deleteServer(server);
			return SERVER_DELETED;
		} else if (action.equalsIgnoreCase("stop")) {
			if (((ServerInstance) server).forceStop()) {
				return SERVER_STOPPED;
			} else {
				return SERVER_NOT_RUNNING;
			}
		} else if (action.equalsIgnoreCase("start")) {
			if (((ServerInstance) server).start()) {
				return SERVER_STARTED;
			} else {
				return ALREADY_RUNNING;
			}
		}
		return INVALID_ACTION;
	}
}
