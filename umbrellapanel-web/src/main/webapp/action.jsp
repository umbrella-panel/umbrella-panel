<%@page import="ms.idrea.umbrellapanel.api.util.Utils"%>
<%@page import="ms.idrea.umbrellapanel.api.core.permissions.Permission"%>
<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="com.mongodb.BasicDBObject"%>
<%@page import="ms.idrea.umbrellapanel.api.chief.gameserver.GameServer.ServerLog"%>
<%@page import="ms.idrea.umbrellapanel.api.chief.gameserver.GameServer"%>
<%@page import="ms.idrea.umbrellapanel.api.util.Address"%>
<%@page import="ms.idrea.umbrellapanel.web.UmbrellaWeb"%>
<%@page import="ms.idrea.umbrellapanel.api.core.permissions.PanelUser"%>
<%@page language="java" contentType="text/pain; charset=UTF-8" pageEncoding="UTF-8"%>
<%

out.clear();

UmbrellaWeb main = UmbrellaWeb.getInstance();

String action = request.getParameter("do");
if (action.equalsIgnoreCase("login")) {
	String name = request.getParameter("name");
	String password = request.getParameter("password");

	if (name != null && password != null) {
		PanelUser user = main.getChief().getPanelUserDatabase().getUser(name);
		if (user != null && user.canLogin(name, password)) {
			session.setAttribute("userId", user.getId());
			response.sendRedirect("/");
			return;
		}
	}
	response.sendRedirect("/?show=1");
	return;
}

// STUFF THAT NEEDS LOGIN
if (session.getAttribute("userId") == null) {
	response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	return;
}

PanelUser user = main.getChief().getPanelUserDatabase().getUser((Integer) session.getAttribute("userId"));

if (action.equalsIgnoreCase("logout")) {
	session.removeAttribute("userId");
	session.invalidate();
	response.sendRedirect("/");
	return;
}

if (action.equalsIgnoreCase("usercreate") && request.getParameter("name") != null && request.getParameter("password") != null && request.getParameter("password2") != null) {
	if (!user.hasGlobalPermission(Permission.ADMIN)) {
		return;
	}
	if (main.getChief().getPanelUserDatabase().getUser(request.getParameter("name")) != null) {
		response.sendRedirect("/?users&show=2");
		return;
	}
	if (request.getParameter("password").isEmpty()) {
		response.sendRedirect("/?users&show=3");
		return;
	}
	if (!request.getParameter("password").equals(request.getParameter("password2"))) {
		response.sendRedirect("/?users&show=1");
	} else {
		PanelUser u = main.getChief().getPanelUserDatabase().createUser(request.getParameter("name"), request.getParameter("password"));
		response.sendRedirect("/?user=" + u.getId());
	}
	return;
}

if (request.getParameter("user") != null) {
	if (!user.hasGlobalPermission(Permission.ADMIN)) {
		return;
	}
	PanelUser editingUser = main.getChief().getPanelUserDatabase().getUser(Integer.valueOf(request.getParameter("user")));
	if (action.equalsIgnoreCase("updateuserrights")) {
		editingUser.getPermissions().clear();
		if (request.getParameter("serverId") != null && request.getParameter("permission") != null) {
			String[] serverIds = request.getParameterValues("serverId");
			String[] permissions = request.getParameterValues("permission");
			int i = 0;
			for (String server : serverIds) {
				if (server.isEmpty() || permissions[i].isEmpty() || !Utils.isInteger(server) || !Utils.isInteger(permissions[i])) {
					i++;
					continue;
				}
				int serverId = Integer.valueOf(server);
				editingUser.grantPermission(serverId, Integer.valueOf(permissions[i]));
				i++;
			}
		}
		response.sendRedirect("/?user=" + editingUser.getId());
		main.getChief().getPanelUserDatabase().updateUser(editingUser);
		return;
	} else if (action.equalsIgnoreCase("updateuser") && request.getParameter("name") != null && request.getParameter("password") != null && request.getParameter("password2") != null) {
		if (!request.getParameter("password").isEmpty() && !request.getParameter("password2").isEmpty()) {
			if (!request.getParameter("password").equals(request.getParameter("password2"))) {
				response.sendRedirect("/?user=" + editingUser.getId() + "&show=2");
				return;
			} else {
				editingUser.setPassword(request.getParameter("password"));
			}
		}
		if (!request.getParameter("name").isEmpty()) {
			editingUser.setName(request.getParameter("name"));
		}
		response.sendRedirect("/?user=" + editingUser.getId() + "&show=1");
		return;
	} else if (action.equalsIgnoreCase("deleteuser")) {
		if (user.getId() == editingUser.getId()) {
			session.invalidate();
		}
		main.getChief().getPanelUserDatabase().deleteUser(editingUser);
		response.sendRedirect("/?users");
		return;
	}
}

// SERVER BASED STUFF
if (request.getParameter("server") != null) {
	GameServer server = main.getChief().getServerManager().getServer(Integer.valueOf(request.getParameter("server")));
	if (!user.hasPermission(server.getId(), Permission.PANEL_ACCESS)) {
		return;
	}
	if (action.equalsIgnoreCase("getLog")) {
		StringBuilder sb = new StringBuilder();
		for (ServerLog log : server.getLogBuffer()) {
			if (log.getTimestamp() > Long.valueOf(request.getParameter("time"))) {
				sb.append(StringEscapeUtils.escapeHtml4(log.getMessage())).append('\n');
			}
		}
		BasicDBObject json = new BasicDBObject();
		json.append("time", System.currentTimeMillis());
		json.append("data", sb.toString());
		out.print(json.toString());
	} else if (action.equalsIgnoreCase("sendCmd")) {
		server.dispatchCommand(request.getParameter("command"));
	} else if (action.equalsIgnoreCase("start") && !server.isRunning()) {
		server.start();
	} else if (action.equalsIgnoreCase("force-stop") && server.isRunning()) {
		server.forceStop();
	} else if (action.equalsIgnoreCase("isRunning")) {
		out.print(String.valueOf(server.isRunning()));
	} else if (action.equalsIgnoreCase("update") && user.hasPermission(server.getId(), Permission.SERVER_SETTINGS)) {
		server.setName(request.getParameter("servername"));
		server.getAddress().setHost(request.getParameter("ip"));
		server.getAddress().setPort(Integer.valueOf(request.getParameter("port")));
		server.setStartCommand(request.getParameter("startcommand"));
		server.update();
		response.sendRedirect("/?server=" + server.getId());
	}
	return;
}

if (action.equalsIgnoreCase("create") && user.hasGlobalPermission(Permission.ADMIN)) {
	GameServer server = main.getChief().getServerManager().createServer(new Address(request.getParameter("ip"), Integer.valueOf(request.getParameter("port"))), request.getParameter("startcommand"), Integer.valueOf(request.getParameter("worker")));
	server.setName(request.getParameter("servername"));
	response.sendRedirect("/?server=" + server.getId());
	return;
}

%>