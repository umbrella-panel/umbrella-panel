package ms.idrea.umbrellapanel.api.chief.webapi.endpoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ms.idrea.umbrellapanel.api.chief.gameserver.GameServer;
import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.util.Utils;

public abstract class ServerEndPoint extends LoginRequiredEndPoint {

	public static final EndPointResponse SERVER_NOT_RUNNING = makeJSONResponse(HttpServletResponse.SC_CONFLICT, "error", "server is not running");
	private int permission;

	public ServerEndPoint(EndPointManager manager, String endpoint, int permission) {
		super(manager, endpoint);
		this.permission = permission;
	}

	@Override
	public final boolean _isValid(HttpServletRequest request) {
		return Utils.isInteger(request.getParameter("id")) && __isValid(request);
	}

	@Override
	public final EndPointResponse _getResponse(HttpServletRequest request, PanelUser user) {
		GameServer server = manager.getChief().getServerManager().getServer(Integer.valueOf(request.getParameter("id")));
		if (server == null) {
			return SERVER_NO_FOUND_RESPONSE;
		}
		if (!user.hasPermission(server.getId(), permission)) {
			return INSUFFICIENT_PERMISSIONS_RESPONSE;
		}
		return __getResponse(request, user, server);
	}

	public abstract EndPointResponse __getResponse(HttpServletRequest request, PanelUser user, GameServer server);

	public abstract boolean __isValid(HttpServletRequest request);
}
