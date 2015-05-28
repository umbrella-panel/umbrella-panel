// utils
var lastPushedParms = null;

function getParms() {
	var hashParams = {};
	var e,
		a = /\+/g,  // Regex for replacing addition symbol with a space
		r = /([^&;=]+)=?([^&;]*)/g,
		d = function (s) { return decodeURIComponent(s.replace(a, " ")); },
		q = window.location.hash.substring(1);

	while (e = r.exec(q))
		hashParams[d(e[1])] = d(e[2]);

	return hashParams;
}

function pushParms(parms) {
	if (JSON.stringify(parms) === JSON.stringify(lastPushedParms)) {
		return;
	}
	ha = "#";
	$.each(parms, function(k, v) {
		ha += k + "=" + v + "&";
	});
	ha = ha.substr(0, ha.length - 1);
	lastPushedParms = parms;
	window.history.pushState({}, JSON.stringify(parms), window.location.pathname + ha);
}

// /Utils
var sessId = null;
var host = "";
var user = null;
var server = null;
var editingUser = null;

// URI Changes
function progressParms(parms) {
	if (!isNaN(parms.s)) {
		// only progress if not showing
		if (server == null || server.id != parms.s) {
			showServer(parms.s);
		}
		$("#server-view div[role=tabpanel] ul[role=tablist] li a[data-toggle=tab][aria-controls=" + parms.v + "]").click();
	} else if (parms.s == "c") {
		showCreateServer();
	} else if (parms.s == "s") {
		showUserSettings();
	} else if (parms.s == "u") {
		showManageUsers();
	} else if (parms.s == "m" && !isNaN(parms.u)) {
		showManageUser(parms.u);
	}
}

// "server", "profil"
function setView(view) {
	view = view + "-view";
	activeView = $(".view-active");
	if (view == activeView.attr("id")) {
		return "not changed";
	}
	console.log("Setting view to '" + view + "'");
	if (server != null && view != "server-view") {
		$("#server-view div[role=tabpanel] ul[role=tablist] li a[data-toggle=tab][aria-controls=console]").click();
		server = null;
	}
	$(".view").removeClass("view-active");
	$("#" + view).addClass("view-active");
	return "changed"
}

function setHost(newHost) {
	if (!newHost.endsWith("/")) {
		host = newHost + "/";
	} else {
		host = newHost;
	}
	$.cookie('umbr_lastHost', host);
}

function apiPost(endpoint, parms, callback) {
	if (sessId != null) {
		parms.sessId = sessId;
	}
	$.post(host + "v1/" + endpoint, parms)
	  .done(function(data) {
		if (data == null) {
			alert("No data received (Host down?)");
		} else {
			console.log("API(" + endpoint + "): " + JSON.stringify(data));
			callback(data);
		}
	}).fail(function(data) {
		if (data.responseJSON == null) {
			alert("No data received (Host down?)");
		} else {
			console.log("API(" + endpoint + "): " + JSON.stringify(data.responseJSON));
			callback(data.responseJSON);
		}
	});
}

function logout() {
	$.removeCookie('umbr_lastSessId');
	apiPost("user/logout", {}, function(data) {
		if (data.error != null) {
			alert(data.error);
		} else {
			setView("welcome");
			//
			user = null;
			server = null;
			sessId = null;
			//
			$("#logout-status").html("Logging out, please leave this window open, until the login screen is visible.");
			setInterval(function() {
				$("#logout-status").html($("#logout-status").html() + ".");
			}, 200);
			setTimeout(function() {
				$("body").html("");
				setTimeout(function() {
					window.location = "";
				}, 1000);
			}, 3000);
		}
	});
}

function login(name, password) {
	var parms = {};
	var silent = false;
	if (name == null && password == null) {
		parms.sessId = sessId;
		silent = true;
	} else {
		parms.name = name;
		parms.password = password;
	}
	apiPost("user/login", parms, function(data) {
		if (data.error != null) {
			if (!silent) {
				alert(data.error);
			}
		} else {
			sessId = data.sessId;
			$.cookie("umbr_lastSessId", sessId);
			user = data.user;
			user.hasPermission = function(server, req) {
				var local = user.permissions[server] >= req;
				if (local == false && server != -1) {
					return user.hasPermission(-1, req);
				} else {
					return local;
				}
			}
			$("#login-content input").val("");
			$("#login-content").hide();
			$("#panel-content").show();
			$(".username").html(user.name);
			loadServerlist();
			progressParms(getParms());
		}
	});
}

function showUserSettings() {
	setView("user-settings");
	pushParms({s:"s"});
}

function showManageUser(userId) {
	apiPost("users/view", {id: userId}, function(data) {
		if (data.error != null) {
			alert(data.error);
		} else {
			setView("manage-user");
			pushParms({s:"m",u:data.user.id});
			editingUser = data.user;
			$("#manage-user-view .editusername").html(editingUser.name);
			$("#manage-user-view #manage-user-set-name input[name=name]").val(editingUser.name);
			$("#manage-user-view table tbody").html("");
			$.each(editingUser.permissions, function(server, level) {
				addPermissionRow(server, level);
			});
		}
	});
}

function showManageUsers() {
	apiPost("users/list", {}, function(data) {
		if (data.error != null) {
			alert(data.error);
		} else {
			setView("manage-users");
			pushParms({s:"u"});
			tbody = $("#manage-users-view table tbody");
			tbody.html("");
			$.each(data.users, function(key, user) {
				tbody.append("<tr userId=\"" + user.id + "\"><td>" + user.id + "</td><td>" + user.name + "</td><td><span class=\"pull-right\"><a class=\"edit-user\"><i class=\"fa fa-pencil\"></i></a>&nbsp;<a class=\"delete-user\"><i class=\"fa fa-trash-o\"></i></a></span></td></tr>");
			});
			$("#manage-users-view table tbody a.edit-user").click(function(e) {
				e.preventDefault();
				userId = $(this).parent().parent().parent().attr("userId");
				showManageUser(userId);
			});
			$("#manage-users-view table tbody a.delete-user").click(function(e) {
				e.preventDefault();
				userId = $(this).parent().parent().parent().attr("userId");
				if (confirm("Benutzer löschen?")) {
					apiPost("users/manage", {action: "delete", id: userId}, function(data) {
						if (data.error != null) {
							alert(data.error);
						} else {
							// handle self delete
							if (userId == user.id) {
								logout();
							} else {
								showManageUsers();
							}
						}
					});
				}
			});
		}
	});
}

function showCreateServer() {
	apiPost("workers/list", {}, function(data) {
		if (data.error != null) {
			alert(data.error);
		} else {
			setView("create-server");
			pushParms({s:"c"});
			select = $("#create-server-view select[name=worker]");
			select.html("");
			$.each(data.workers, function(key, worker) {
				if (worker.online) {
					select.append("<option value='" + worker.id + "'>" + worker.address + " (" + worker.id + ")</option>")
				}
			});
		}
	});
}

function showServer(serverId) {
	buttonIcon = $("#server-view #server-refresh i.fa-refresh");
	if (buttonIcon.hasClass("fa-spin")) {
		return;
	}
	buttonIcon.addClass("fa-spin");
	apiPost("server/view", {"id": serverId}, function(data) {
		buttonIcon.removeClass("fa-spin");
		if (data.error != null) {
			alert(data.error);
		} else {
			setView("server");
			wasRunning = false;
			hasChanged = true;
			if (server != null) {
				wasRunning = server.isRunning;
				hasChanged = server.id != data.server.id;
				if (hasChanged) {
					$("#server-view #sendCommandFieldset input[type=text]").val("");
					$("#server-view #log").html("");
				} else {
					data.server.lastConsole = server.lastConsole;
				}
			}
			server = data.server;
			if (server.lastConsole == null) {
				server.lastConsole = 0;
			}
			if (wasRunning == false && server.isRunning) {
				$("#server-view #log").html("");
			}
			apiPost("server/console", {"id": server.id, "time": server.lastConsole}, function(data) {
				if (data.error == null) {
					server.lastConsole = data.time;
					$.each(data.data, function(key, line) {
						$("#server-view #log").append(line + "<br>");
					});
				}
			});
			if (server.isRunning) {
				$("#server-view #server-start").hide();
				$("#server-view #server-stop").show();
				$("#server-view #sendCommandFieldset").removeAttr("disabled");
				$("#server-view #settingsFieldset").attr("disabled", "disabled");
			} else {
				$("#server-view #sendCommandFieldset").attr("disabled", "disabled");
				$("#server-view #settingsFieldset").removeAttr("disabled");
				$("#server-view #server-start").show();
				$("#server-view #server-stop").hide();
			}
			$("#server-view .server-name").html(server.name);
			$("#server-view .worker-text").html(server.worker.id + " (" + server.worker.ip + ")");
			$("#server-view .server-id").html(server.id);
			
			setOnlineBullet($("#server-view .server-status"), server.isRunning);
			setOnlineBullet($("#server-view .worker-status"), server.worker.online);
			setOnlineBullet($("#server-view .ftp-status"), server.worker.online);
			if (server.worker.online) {
				$("#server-view .server-controll-btn").removeAttr("disabled");
			} else {
				$("#server-view .server-controll-btn").attr("disabled", "disabled");
			}
			if ($("#server-view #settings").hasClass("active") == false || hasChanged == true) {
				$("#server-view input[name=servername]").val(server.name);
				$("#server-view input[name=host]").val(server.ip);
				$("#server-view input[name=port]").val(server.port);
				$("#server-view input[name=startcommand]").val(server.startcommand);
			}
			if (user.hasPermission(server.id, 20) == true) {
				$("#server-view .ftp-text").html("ftp://" + server.id + "-" + user.name + ":&lt;deinPassword&gt;@" + server.worker.ip + ":1221/");
			} else {
				$("#server-view .ftp-text").html("Kein Zugriff!");
			}
			if (user.hasPermission(server.id, 30) == false) {
				$("#server-view #settingsFieldset").attr("disabled", "disabled");
			}
			if (user.hasPermission(server.id, 999) == false) {
				$("#server-view #server-delete-submit").attr("disabled", "disabled");
			}
		}
	});
}

function deleteServer() {
	if (server == null) {
		return;
	}
	if (confirm("Server löschen?")) {
		manageServer(server, "delete");
	}
}

function addPermissionRow(server, level) {
	if (editingUser != null) {
		if (server == null) {
			server = "";
		}
		if (level == null) {
			level = "";
		}
		addedRow = $("#manage-user-view table tbody").append("<tr><td><input list=\"serverValues\" value=\"" + server + "\" type=\"text\" class=\"form-control\" name=\"server\" autocomplete=\"off\"/></td><td><input list=\"permissionValues\" value=\"" + level + "\" type=\"text\" class=\"form-control\" name=\"value\" autocomplete=\"off\"/></td><td><span class=\"pull-right\"><a id=\"remove-row\"><i class=\"fa fa-trash-o\" style=\"font-size: 2.1em;\"></i></a></span></td></tr>");
		addedRow.find("a#remove-row").click(function() {
			searchId = $(this).parent().parent().parent().find("input[name=server]").val();
			if ($("#manage-user-view table tbody input[name=server]").filter(function(){return $(this).val() == searchId}).length != 1) {
				$(this).parent().parent().parent().remove();
			} else {
				$(this).parent().parent().parent().hide();
			}
		});
	}
}

function setUserPermissions() {
	if (editingUser != null) {
		var showedError = false;
		var resCount = 0;
		var exCount = $("#manage-user-view table tbody").children().length;
		var permissions = [];
		$("#manage-user-view table tbody").children().each(function () {
			if (showedError == true) {
				return;
			}
			var server = $(this).find("input[name=server]").val();
			var level = $(this).find("input[name=value]").val();
			if ($(this).css("display") == "none") {
				level = -1;
				if (isNaN(server) || server == "") {
					// skip
					exCount--;
					return;
				}
			}
			if (server == "" && level == "") {
				exCount--;
				return;
			}
			if (isNaN(server) || isNaN(level) || server == "" || level == "") {
				alert("Keine gültige Zahl!");
				permissions = [];
				showedError = true;
				return;
			}
			permissions.push({"server":server, "level": level});
		});
		showedError = false;
		$.each(permissions, function(key, permission) {
			var parms = {};
			parms.id = editingUser.id;
			parms.action = "setpermission";
			parms.level = permission.level;
			parms.server = permission.server;
			apiPost("users/manage", parms, function(data) {
				resCount++;
				if (data.error != null && !showedError) {
					alert(data.error);
					showedError = true;
				} else {
					if (resCount >= exCount) {
						if (data.user.id == user.id && data.user.permissions[-1] != 999) {
							alert("Permissions gesetzt, du hast jetzt aber keine Rechte mehr auf diese Seite!");
							setView("welcome");
						} else {
							alert("Permissions gesetzt!");
							showManageUser(data.user.id);
						}
						
					}
				}
			});
		});
	}
}

function setUserName() {
	if (editingUser != null) {
		parms = {};
		parms.id = editingUser.id;
		parms.newname = $("#manage-user-view #manage-user-set-name input[name=name]").val();
		parms.action = "updatename";
		if (parms.newname.length <= 0) {
			alert("Name ist zu kurz!");
			return;
		}
		apiPost("users/manage", parms, function(data) {
			if (data.error != null) {
				alert(data.error);
			} else {
				showManageUser(data.user.id);
			}
		});
	}
}

function setUserPassword() {
	if (editingUser != null) {
		parms = {};
		parms.id = editingUser.id;
		parms.newpassword = $("#manage-user-view #manage-user-set-password input[name=password]").val();
		parms.newpassword2 = $("#manage-user-view #manage-user-set-password input[name=password2]").val();
		parms.action = "setpassword";
		if (parms.newpassword != parms.newpassword2) {
			alert("Passwörter stimmen nicht überein!");
			return;
		} else if (parms.newpassword.length < 8) {
			alert("Passwort ist zu kurz!");
			return;
		}
		apiPost("users/manage", parms, function(data) {
			if (data.error != null) {
				alert(data.error);
			} else {
				$("#manage-user-view #manage-user-set-password input").val("");
				alert("Passwort geändert!");
				showManageUser(editingUser.id);
			}
		});
	}
}

function createUser() {
	parms = {};
	parms.name = $("#manage-users-view #user-create input[name=name]").val();
	parms.password = $("#manage-users-view #user-create input[name=password]").val();
	parms.password2 = $("#manage-users-view #user-create input[name=password2]").val();
	if (parms.password != parms.password2) {
		alert("Passwörter stimmen nicht überein!");
		return;
	} else if (parms.password.length < 8) {
		alert("Passwort ist zu kurz!");
		return;
	} else if (parms.name.length <= 0) {
		alert("Name ist zu kurz!")
		return;
	}
	apiPost("users/create", parms, function(data) {
		if (data.error != null) {
			alert(data.error);
		} else {
			$("#manage-users-view #user-create input").val("");
			showManageUsers();
			alert("Benutzer angelegt!");
		}
	});
}

function createServer() {
	port = $("#create-server-view input[name=port]").val();
	if (isNaN(port)) {
		alert(port + " is not a Number!");
		return;
	}
	parms = {};
	parms.port = port;
	parms.ip = $("#create-server-view input[name=host]").val();
	parms.startcommand = $("#create-server-view input[name=startcommand]").val();
	parms.workerId = $("#create-server-view select[name=worker]").val();
	$("#create-server-view #createServerFieldset").attr("disabled", "disabled");
	apiPost("servers/create", parms, function(data) {
		$("#create-server-view #createServerFieldset").removeAttr("disabled");
		if (data.error != null) {
			alert(data.error);
		} else {
			$("#create-server-view input").val("");
			loadServerlist();
			alert("Server created!");
		}
	});
}

function updateServerSettings() {
	if (server != null) {
		if (server.isRunning) {
			alert("Cant edit the Server while running!");
			return;
		}
		port = $("#server-view input[name=port]").val();
		if (isNaN(port)) {
			alert(port + " is not a Number!");
			return;
		}
		parms = {};
		parms.id = server.id;
		parms.port = port;
		parms.servername = $("#server-view input[name=servername]").val();
		parms.ip = $("#server-view input[name=host]").val();
		parms.startcommand = $("#server-view input[name=startcommand]").val();
		parms.action = "update";
		$("#server-view #settingsFieldset").attr("disabled", "disabled");
		apiPost("server/manage", parms, function(data) {
			$("#server-view #settingsFieldset").removeAttr("disabled");
			if (data.error != null) {
				alert(data.error);
			} else {
				loadServerlist();
				showServer(data.server.id);
			}
		});
	}
}

function manageServer(server, action) {
	apiPost("server/manage", {"id": server.id, "action": action}, function(data) {
		if (data.error != null) {
			alert(data.error);
		} else {
			if (action != "delete") {
				showServer(server.id);
			} else {
				loadServerlist();
				setView("welcome");
			}
		}
	});
}

function loadServerlist() {
	apiPost("servers/list", {}, function(data) {
		if (data.error != null) {
			alert(data.error);
		} else {
			$("#nav-server-list").html("");
			$("#serverValues").html("");
			$.each(data.servers, function(key, server) {
				$("#nav-server-list").append("<li><a serverId=" + server.id + "><span class=\"bullet bullet-" + (server.isRunning == true ? "success" : "danger") + "\"></span> " + server.name + "</a></li>");
				$("#serverValues").append("<option value=\"" + server.id + "\">" + server.name + "</option>");
			});
			$("#serverValues").append("<option value=\"-1\">GLOBAL</option>");
			$("#nav-server-list li a").click(function(e) {
				e.preventDefault();
				serverId = $(this).attr("serverId");
				pushParms({"s": serverId, "v": "console"});
				showServer(serverId);
			})
		}
	});
}

function changeUserPassword() {
	parms = {};
	parms.newpassword = $("#user-settings-view #user-change-password input[name=newpassword]").val();
	parms.newpassword2 = $("#user-settings-view #user-change-password input[name=newpassword2]").val();
	parms.oldpassword = $("#user-settings-view #user-change-password input[name=oldpassword]").val();
	if (parms.newpassword != parms.newpassword2) {
		alert("Neue Passwörter stimmen nicht überein!");
		return;
	} else if (parms.newpassword.length < 8) {
		alert("Passwort ist zu kurz!");
		return;
	}
	apiPost("user/changepassword", parms, function(data) {
		if (data.error != null) {
			alert(data.error);
		} else {
			$("#user-settings-view #user-change-password input").val("");
			alert("Passwort geändert!");
		}
	});
}

function setOnlineBullet(object, online) {
	if (online) {
		object.removeClass("bullet-danger");
		object.addClass("bullet-success");
	} else {
		object.addClass("bullet-danger");
		object.removeClass("bullet-success");
	}
}

$(document).ready(function() {
	// common
	setInterval(function() {
		if (user != null) {
			loadServerlist();
			if (server != null) {
				showServer(server.id);
			}
		}
	}, 10000);
	$(window).on("popstate", function() {
		progressParms(getParms());
	});
	// server-view
	$("#server-view #server-start").click(function() {
		if (server != null) {
			manageServer(server, "start");
		}
	});
	$("#server-view #server-stop").click(function() {
		if (server != null) {
			manageServer(server, "stop");
		}
	});
	$("#server-view #server-refresh").click(function() {
		if (server != null) {
			showServer(server.id);
		}
	});
	$("#server-view #server-delete-submit").click(function() {
		deleteServer();
	});
	$("#server-view #server-settings-update").click(function() {
		updateServerSettings();
	});
	$("#server-view #sendCommandFieldset input").keypress(function(e) {
		if(e.which === 13) {
			$("#server-view #sendCommandFieldset button").click();
		}
	});
	$("#server-view #sendCommandFieldset button").click(function() {
		if (server != null) {
			if (server.isRunning == false) {
				alert("Server is not running!");
				return;
			}
			command = $("#server-view #sendCommandFieldset input").val();
			if (command == "") {
				alert("Empty command");
			}
			$("#server-view #sendCommandFieldset input").val("");
			apiPost("server/sendCommand", {"id": server.id, "command": command}, function(data) {
				if (data.error != null) {
					alert(data.error);
				} else {
					setTimeout(showServer(server.id), 1000);
				}
			});
		}
	});
	$("#server-view div[role=tabpanel] ul[role=tablist] li a[data-toggle=tab]").click(function(e) {
		if (server != null) {
			e.preventDefault();
			pushParms({"s": server.id, "v": $(this).attr("aria-controls")});
		}	
	});
	// create-server-view
	$("#create-server-view #create-server-submit").click(function() {
		createServer();
	});
	// manage-users-view
	$("#manage-users-view #user-create-submit").click(function() {
		createUser();
	});
	// manage-user-view
	$("#manage-user-view #manage-user-set-name-submit").click(function() {
		setUserName();
	});
	$("#manage-user-view #manage-user-set-password-submit").click(function() {
		setUserPassword();
	});
	$("#manage-user-view #manage-user-permissions-submit").click(function() {
		setUserPermissions();
	});
	$("#manage-user-view #manage-user-permissions-addrow").click(function() {
		addPermissionRow();
	});
	// user-settings-view
	$("#user-settings-view #user-change-password-submit").click(function() {
		changeUserPassword();
	});
	// login
	$("#login-content #form-signin-submit").click(function() {
		lHost = $("#login-content input#inputHost").val();
		lName = $("#login-content input#inputName").val();
		lPassword = $("#login-content input#inputPassword").val();
		if (lHost == "") {
			alert("Bitte Host angeben!");
			return;
		}
		if (lName == "") {
			alert("Bitte Name angeben!");
			return;
		}
		if (lPassword == "") {
			alert("Bitte Passwort angeben!");
			return;
		}
		setHost(lHost);
		login(lName, lPassword);
		lName = "";
		lPassword = "";
	});
	// nav
	$("#nav-create-server").click(function(e) {
		e.preventDefault();
		showCreateServer();
	});
	$("#nav-user-logout").click(function(e) {
		e.preventDefault();
		logout();
	});
	$("#nav-user-settings").click(function(e) {
		e.preventDefault();
		showUserSettings();
	});
	$("#nav-manage-users").click(function(e) {
		e.preventDefault();
		showManageUsers();
	});
	$("#nav-welcome").click(function(e) {
		e.preventDefault();
		setView("welcome");
		pushParms({});
	});
	// autologin
	var lastHost = $.cookie("umbr_lastHost");
	var lastSessId = $.cookie("umbr_lastSessId");
	if (lastHost != null) {
		$("#login-content input#inputHost").val(lastHost);
	}
	if (lastHost != null && lastSessId != null) {
		setHost(lastHost);
		sessId = lastSessId;
		login();
	}
});
