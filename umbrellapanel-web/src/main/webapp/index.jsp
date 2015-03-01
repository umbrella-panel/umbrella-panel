<%@page import="ms.idrea.umbrellapanel.api.core.permissions.Permission"%>
<%@page import="com.flowpowered.networking.session.Session"%>
<%@page import="ms.idrea.umbrellapanel.chief.net.Worker"%>
<%@page import="ms.idrea.umbrellapanel.api.chief.gameserver.GameServer"%>
<%@page import="ms.idrea.umbrellapanel.web.UmbrellaWeb"%>
<%@page import="ms.idrea.umbrellapanel.api.core.permissions.PanelUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	out.clear();
	UmbrellaWeb main = UmbrellaWeb.getInstance();
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta name="description" content="">
	<meta name="author" content="">
	<title>UmbrellaPanel - iDreams</title>
	<link href="resources/css/bootstrap.min.css" rel="stylesheet">
	<link href="resources/css/web.css" rel="stylesheet">
	<link href="resources/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
	<script src="resources/js/jquery.js"></script>
	<script src="resources/js/bootstrap.min.js"></script>
</head>
<body>
	<%
		if (session.getAttribute("userId") == null) {
	%>
	<div id="loginModal" class="modal show" tabindex="-1" role="dialog" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="text-center">Login</h1>
				</div>
				<div class="modal-footer">
				<%
					if ("1".equalsIgnoreCase(request.getParameter("show"))) {
				%>
					<div class="alert alert-danger" style="text-align: left;">Falscher Benutzername oder falsches Passwort</div>
				<%
					}
				%>
					<form class="form col-md-12 center-block" method="POST" action="action.jsp?do=login">
						<div class="form-group">
							<input type="text" name="name" class="form-control input-lg" placeholder="Name">
						</div>
						<div class="form-group">
							<input type="password" name="password" class="form-control input-lg" placeholder="Password">
						</div>
						<div class="form-group">
							<button class="btn btn-primary btn-lg btn-block">Einloggen</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
	<%
		} else {
			PanelUser user = main.getChief().getPanelUserDatabase().getUser((Integer) session.getAttribute("userId"));
	%>
	<div id="wrapper">
		<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="/">UmbrellaPanel</a>
			</div>
			<ul class="nav navbar-right top-nav">
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-user"></i><% out.print(user.getName()); %> <b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li>
							<a href="/?profil"><i class="fa fa-fw fa-user"></i>Profil</a>
						</li>
						<li class="divider"></li>
						<li>
							<a href="/action.jsp?do=logout"><i class="fa fa-fw fa-power-off"></i>Ausloggen</a>
						</li>
					</ul>
				</li>
			</ul>
			<div class="collapse navbar-collapse navbar-ex1-collapse">
				<ul class="nav navbar-nav side-nav">
					<li>
						<a <%if (request.getParameterMap().isEmpty()) { out.print("class=\"active-menu\""); }%> href="/"><i class="fa fa-fw fa-dashboard"></i>Dashboard</a>
					</li>
					<li>
						<a href="javascript:;" data-toggle="collapse" data-target="#nav-servers"><i class="fa fa-tasks"></i>Server<i class="fa fa-fw fa-caret-down"></i></a>
						<ul id="nav-servers" class="nav nav-second-level in">
							<%
								for (GameServer server : main.getChief().getServerManager().getAllServers()) {
									if (user.hasPermission(server.getId(), Permission.PANEL_ACCESS)) {
										out.print("<li><a ");
										if (request.getParameter("server") != null && Integer.valueOf(request.getParameter("server")) == server.getId()) {
											out.print("class=\"active-menu\"");
										}
										out.print("href=\"/?server=" + server.getId() + "\"><i class=\"fa fa-fw fa-folder\"></i>" + server.getName() + "</a></li>");
									}
								}
							%>
						</ul>
					</li>
					<li>
						<a <%if (request.getParameter("profil") != null) { out.print("class=\"active-menu\""); }%> href="/?profil"><i class="fa fa-fw fa-user"></i>Profil</a>
					</li>
					<%
						if (user.hasGlobalPermission(Permission.ADMIN)) {
					%>
					<li>
						<a <%if (request.getParameter("users") != null || request.getParameter("user") != null) { out.print("class=\"active-menu\""); }%> href="/?users"><i class="fa fa-fw fa-users"></i>Benutzer</a>
					</li>
					<li>
						<a <%if (request.getParameter("create") != null) { out.print("class=\"active-menu\""); }%> href="/?create"><i class="fa fa-fw fa-save"></i>Server erstellen</a>
					</li>
					<%
						}
					%>
				</ul>
			</div>
		</nav>
		<%
			if (request.getParameter("create") != null && user.hasGlobalPermission(Permission.ADMIN)) {
		%>
			<div id="page-wrapper">
				<div class="container-fluid">
					<div class="row">
						<div class="col-lg-12">
							<h1 class="page-header">Server erstellen</h1>
							<form class="form col-lg-4" action="action.jsp?do=create" method="POST">
								<div class="form-group">
									<label>Name: </label><input name="servername" class="form-control"></input>
								</div>
								<div class="form-group">
									<label>IP: </label><input name="ip" class="form-control"></input>
								</div>
								<div class="form-group">
									<label>Port: </label><input name="port" class="form-control"></input>
								</div>
								<div class="form-group">
									<label>Startcommand: </label><input name="startcommand" class="form-control"></input>
								</div>
								<div class="form-group">
									<label>Worker: </label>
									<select class="form-control" name="worker">
									<%
										for (Session sessionWorker : main.getChief().getWorkerManager().getAllWorkers()) {
											Worker worker = (Worker) sessionWorker;
											out.print("<option value=\"" + worker.getId() + "\">" + worker.getAddress().toString() + "</option>");
										}
									%>
									</select>
								</div>
								<div class="form-group">
									<button class="btn btn-primary btn-lg btn-block">Erstellen</button>
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
		<%
			} else if (request.getParameter("profil") != null) {
		%>
			<div id="page-wrapper">
				<div class="container-fluid">
					<div class="row">
						<div class="col-lg-12">
							<h1 class="page-header">Profil</h1>
						</div>
						<div class="col-lg-6">
							<h3>Passwort ändern</h3>
							<%
								if ("2".equalsIgnoreCase(request.getParameter("show"))) {
							%>
								<div class="alert alert-danger">Neue Passwörter stimmen nicht überein!</div>
							<%
								} else if ("1".equalsIgnoreCase(request.getParameter("show"))) {
							%>
								<div class="alert alert-success">Passwort geändert!</div>
							<%
								} else if ("3".equalsIgnoreCase(request.getParameter("show"))) {
							%>
								<div class="alert alert-danger">Falsches Passwort!</div>
							<%
							} else if ("4".equalsIgnoreCase(request.getParameter("show"))) {
							%>
								<div class="alert alert-danger">Passwort ist zu kurz! (min 8 Zeichen)</div>
							<%
							}
							%>
							<form id="updateuser" action="action.jsp?do=updatepassword" method="POST">
								<div class="form-group">
									<label>Altes Passwort: </label><input name="oldpassword" type="password" class="form-control" autocomplete="off"></input>
								</div>
								<div class="form-group">
									<label>Neues Passwort: </label><input name="newpassword" type="password" class="form-control" autocomplete="off"></input>
								</div>
								<div class="form-group">
									<label>Neues Passwort wiederholen: </label><input name="newpassword2" type="password" class="form-control" autocomplete="off"></input>
								</div>
								<span class="btn-group">
									<button type="submit" form="updateuser" class="btn btn-success">Passwort ändern</button>
								</span>
							</form>
						</div>
					</div>
				</div>
			</div>
		
		<%	
			} else if (request.getParameter("user") != null && user.hasGlobalPermission(Permission.ADMIN)) {
				PanelUser editingUser = main.getChief().getPanelUserDatabase().getUser(Integer.valueOf(request.getParameter("user")));
		%>
		
			<script type="text/javascript">
			$( document ).ready(function() {
				$( "#add" ).click(function() {
					$ ( "#permissions" ).append( "<tr><td style=\"width: 20px;\"><i class=\"fa fa-trash-o\"onclick=\"$(this).parent().parent().remove();\"></i></td><td><input type=\"text\" class=\"form-control\" name=\"serverId\" list=\"serverValues\" autocomplete=\"off\"></td><td><input type=\"text\" class=\"form-control\" name=\"permission\" list=\"permissionValues\" autocomplete=\"off\"></td></tr>" );
				});
			});
			</script>
			<style type="text/css">
				td, th {
					font-size: 20px;
				}
				
				.fa-trash-o {
					cursor: pointer;
				}
			</style>
			<div id="page-wrapper">
				<div class="container-fluid">
					<div class="row">
						<div class="col-lg-12">
							<h1 class="page-header">Benutzer: <% out.print(editingUser.getName()); %></h1>
						</div>
						<div class="col-lg-6">
							<h3>Rechte</h3>
						<%
							if (user.getId() == editingUser.getId()) {
						%>
							<div class="alert alert-warning">Änderungen können deine Rechte einschränken!</div>
						<%
							}
						%>
							<form action="action.jsp?do=updateuserrights&user=<% out.print(editingUser.getId()); %>" method="POST">
								<datalist id="permissionValues">
									<option value="10">PANEL_ACCESS</option>
									<option value="20">FTP_ACCESS</option>
									<option value="30">SERVER_SETTINGS</option>
									<option value="999">ADMIN</option>
								</datalist>
								<datalist id="serverValues">
									<option value="-1">GLOBAL</option>
								<%
									for (GameServer server : main.getChief().getServerManager().getAllServers()) {
										out.print("<option value=\"" + server.getId() + "\">" + server.getName() + "</option>");
									}
								%>
								</datalist>
								<table id="permissions" class="col-lg-12">
									<tr>
										<th colspan="2">Server</th>
										<th colspan="1">Wert</th>
									</tr>
								<%
									for (int serverId : editingUser.getPermissions().keySet()) {
										out.print("<tr><td style=\"width: 20px;\">");
										out.print("<i class=\"fa fa-trash-o\"onclick=\"$(this).parent().parent().remove();\"></i>");
										out.print("</td><td>");
										out.print("<input type=\"text\" class=\"form-control\" name=\"serverId\" list=\"serverValues\" autocomplete=\"off\" value=\"" + serverId + "\">");
										out.print("</td><td>");
										out.print("<input type=\"text\" class=\"form-control\" name=\"permission\" list=\"permissionValues\" autocomplete=\"off\" value=\"" + editingUser.getPermission(serverId) + "\">");
										out.print("</td></tr>");
									}
								%>
								</table>
								<span style="margin-top: 10px;" class="btn-group">
									<button type="submit" class="btn btn-success">Speichern</button>
									<button type="button" class="btn btn-primary" id="add">Zeile hinzufügen</button>
								</span>
							</form>
						</div>
						<div class="col-lg-6">
							<h3>Daten</h3>
							<%
								if ("2".equalsIgnoreCase(request.getParameter("show"))) {
							%>
								<div class="alert alert-danger">Passwort ist nicht richtig! (min 8 Zeichen oder nicht gleich)</div>
							<%
								} else if ("1".equalsIgnoreCase(request.getParameter("show"))) {
							%>
								<div class="alert alert-success">Benutzer geändert!</div>
							<%
								}
							%>
							<form id="updateuser" action="action.jsp?do=updateuser&user=<% out.print(editingUser.getId()); %>" method="POST">
								<div class="form-group">
									<label>Name: </label><input name="name" type="text" class="form-control" autocomplete="off" value="<% out.print(editingUser.getName()); %>"></input>
								</div>
								<div class="form-group">
									<label>Passwort: </label><input name="password" type="password" class="form-control" autocomplete="off"></input>
								</div>
								<div class="form-group">
									<label>Passwort wiederholen: </label><input name="password2" type="password" class="form-control" autocomplete="off"></input>
								</div>
								<span class="btn-group">
									<button type="submit" form="updateuser" class="btn btn-success">Speichern</button>
									<button type="submit" form="deleteuser" class="btn btn-danger">Benutzer löschen</button>
								</span>
							</form>
							<form id="deleteuser" action="action.jsp?do=deleteuser&user=<% out.print(editingUser.getId()); %>" method="POST"></form>
						</div>
					</div>
				</div>
			</div>
		
		<%
			} else if (request.getParameter("users") != null && user.hasGlobalPermission(Permission.ADMIN)) {
		%>
			<style type="text/css">
				td, th {
					font-size: 20px;
				}
			</style>
			<div id="page-wrapper">
				<div class="container-fluid">
					<div class="row">
						<div class="col-lg-12">
							<h1 class="page-header">Benutzer</h1>
						</div>
						<div class="col-lg-6">
							<h3>Alle</h3>
							<table>
							<%
								for (PanelUser o : main.getChief().getPanelUserDatabase().getAllUsers()) {
									out.print("<tr><td><a href=\"/?user=" + o.getId() + "\"><kbd>" + o.getId() + "</kbd> " + o.getName() + "</a><td></tr>");
								}
							%>
							</table>
						</div>
						<div class="col-lg-6">
							<h3>Anlegen</h3>
							<%
								if ("1".equalsIgnoreCase(request.getParameter("show"))) {
							%>
								<div class="alert alert-danger">Passwörter sind nicht gleich!</div>
							<%
								} else if ("2".equalsIgnoreCase(request.getParameter("show"))) {
							%>
								<div class="alert alert-danger">Benutzername bereits vergeben!</div>
							<%
								} else if ("3".equalsIgnoreCase(request.getParameter("show"))) {
							%>
								<div class="alert alert-danger">Passwort ist zu kurz! (min 8 Zeichen)</div>
							<%
								}
							%>
							<form action="action.jsp?do=usercreate" method="POST">
								<div class="form-group">
									<label>Name: </label><input name="name" type="text" class="form-control" autocomplete="off" value=""></input>
								</div>
								<div class="form-group">
									<label>Password: </label><input name="password" type="password" class="form-control" autocomplete="off"></input>
								</div>
								<div class="form-group">
									<label>Password wiederholen: </label><input name="password2" type="password" class="form-control" autocomplete="off"></input>
								</div>
								<button type="submit" class="btn btn-success">Erstellen</button>
							</form>
						</div>
					</div>
				</div>
			</div>
		<%
			} else if (request.getParameter("server") != null) {
				GameServer server = main.getChief().getServerManager().getServer(Integer.valueOf(request.getParameter("server")));
				if (!user.hasPermission(server.getId(), Permission.PANEL_ACCESS)) {
					out.print("Keine Permissions!");
				} else {
					Worker worker = (Worker) server.getWorker();
		%>
				<script type="text/javascript">
				<%
					if (worker != null) {
				%>
						$( document ).ready(function() {
							refresh();
							$( "#sendCmd" ).click(function() {
								if (isRunning == true) {
									$.post( "action.jsp?do=sendCmd", { server: <% out.print("\"" + server.getId() + "\""); %>, command: $( "#command" ).val() } );
									$( "#command" ).val("");
									setTimeout(refresh, 500);
								}
							});
							$( "#command" ).keypress(function(e) {
								if(e.which === 13) {
									$("#sendCmd").click();
								}
							});
							$( "#server-refresh" ).click(function() {
								refresh();
							});
							$( "#start-server" ).click(function() {
								$.post( "action.jsp?do=start", { server: <% out.print("\"" + server.getId() + "\""); %> } );
								setTimeout(refresh, 500);
								$("#log").html("");
							});
							$( "#force-stop-server" ).click(function() {
								$.post( "action.jsp?do=force-stop", { server: <% out.print("\"" + server.getId() + "\""); %> } );
								setTimeout(refresh, 500);
							});
							setInterval(refresh, 30000);
						});
						
						var isRefreshing = false;
						var isRunning = false;
						var last = 0;
						function refresh() {
							if (isRefreshing == true) {
								return;
							}
							isRefreshing = true;
							$.post( "action.jsp?do=isRunning", { server: <% out.print("\"" + server.getId() + "\""); %>}, function( data ) {
								if (data == "true") {
									isRunning = true;
									$ ("#commandfieldset" ).removeAttr("disabled");
									$ ("#settingsfieldset" ).attr("disabled", "disabled");
									$( "#force-stop-server" ).show();
									$( "#start-server" ).hide();
								} else {
									isRunning = false;
									$ ("#commandfieldset" ).attr("disabled", "disabled");
									<%
										if (user.hasPermission(server.getId(), Permission.SERVER_SETTINGS)) {
									%>
										$( "#settingsfieldset" ).removeAttr("disabled");
									<%
										}
									%>
									$( "#force-stop-server" ).hide();
									$( "#start-server" ).show();
								}
								$.post( "action.jsp?do=getLog", { server: <% out.print("\"" + server.getId() + "\""); %>, time: last }, function( data ) {
									isRefreshing = false;
									data = JSON.parse(data);
									if (typeof data.time != 'undefined') {
										last = data.time;
										$( "#log" ).html( $( "#log" ).html() + data.data );
										var element = document.getElementById("log");
										element.scrollTop = element.scrollHeight;
									}
								});
							});
						}
				<%
					}
				%>
				</script>
				<div id="page-wrapper">
					<div class="container-fluid">
						<div class="row">
							<div class="col-lg-12">
								<h1 class="page-header">
									<% out.print(server.getName()); %>
									<span>
										<fieldset id="server-buttons" class="btn-group" style="display: inline;" <% if (worker == null) { out.print("disabled=\"disabled\""); } %>>
											<button type="button" class="btn btn-danger" id="force-stop-server" style="display: none;">Server stoppen</button>
											<button type="button" class="btn btn-success" id="start-server" style="display: none; border-top-left-radius: 4px; border-bottom-left-radius: 4px;">Server starten</button>
											<button type="button" class="btn btn-warning" id="server-refresh">Aktualisieren</button>
										</fieldset>
									</span>
								</h1>
							</div>
							<div class="col-lg-6 ">
								<h3>Konsole</h3>
								<pre class="well" id="log" style="overflow: scroll; height: 500px; color: white; background-color: black; margin-bottom: 10px;"></pre>
								<fieldset id="commandfieldset" disabled="disabled">
									<div class="input-group">
										<input type="text" class="form-control" id="command">
										<span class="input-group-btn">
											<button class="btn btn-primary" type="button" id="sendCmd">Senden</button>
										</span>
									</div>
								</fieldset>
							</div>
							<form class="form col-lg-6" id="updateserver" action="action.jsp?do=update&server=<% out.print(server.getId()); %>" method="POST">
								<h3>Einstellungen</h3>
								<div class="well">
									<span class="label label-primary">Server ID</span> <code><% out.print(server.getId()); %></code><br>
									<%
										if (worker == null) {
									%>
										<span class="label label-danger">Worker Offline</span> <code><% out.print(server.getWorkerId()); %></code>
									<%
										} else {
									%>
										<span class="label label-info">FTP</span> <code><% out.print("ftp://" + server.getId() + "-" + user.getName() + ":&lt;dein-passwort&gt;@" + worker.getAddress().getHostString() + ":1221/"); %></code><br>
										<span class="label label-success">Worker Online</span> <code><% out.print(worker.getId() + " (" + worker.getAddress().toString() + ")"); %></code>
									<%
										}
									%>
								</div>
								<fieldset id="settingsfieldset" disabled="disabled">
									<div class="form-group">
										<label>Name: </label><input name="servername" class="form-control" value="<% out.print(server.getName()); %>"></input>
									</div>
									<div class="form-group">
										<label>IP: </label><input name="ip" class="form-control" value="<% out.print(server.getAddress().getHost()); %>"></input>
									</div>
									<div class="form-group">
										<label>Port: </label><input name="port" class="form-control" value="<% out.print(server.getAddress().getPort()); %>"></input>
									</div>
									<div class="form-group">
										<label>Startcommand: </label><input name="startcommand" class="form-control" value="<% out.print(server.getStartCommand()); %>"></input>
									</div>
									<span class="btn-group">
									<%
										if (user.hasPermission(server.getId(), Permission.SERVER_SETTINGS)) {
									%>
										<button type="submit" form="updateserver" class="btn btn-success">Speichern</button>
									<%
										}
										if (user.hasPermission(server.getId(), Permission.ADMIN)) {
									%>
										<button type="submit" form="deleteserver" class="btn btn-danger">Server löschen</button>
									<%
										}
									%>
									</span>
								</fieldset>
							</form>
							<form class="form col-lg-6" id="deleteserver" action="action.jsp?do=deleteserver&server=<% out.print(server.getId()); %>" method="POST"></form>
						</div>
					</div>
				</div>
		<%
				}
			} else {
		%>
			<div id="page-wrapper">
				<div class="container-fluid">
					<div class="row">
						<div class="col-lg-12">
							<h1 class="page-header">Willkommen!</h1>
							<p>Willkommen im UmbrellaPanel, hier kannst du alle Server verwalten.<br>Unter Profil kannst du dein Passwort ändern.</p>
						</div>
					</div>
				</div>
			</div>
	<%
			}
		}
	%>
	</div>
	<span style="color: white; position: fixed; right: 0px; bottom: 0px; background: none repeat scroll 0% 0% #666; padding-left: 2px; border-top-left-radius: 5px;">&copy; iDreams 2014-2015</span>
</body>
</html>