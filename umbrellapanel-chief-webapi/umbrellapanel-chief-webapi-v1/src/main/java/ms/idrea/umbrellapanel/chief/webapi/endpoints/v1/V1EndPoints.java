package ms.idrea.umbrellapanel.chief.webapi.endpoints.v1;

import com.mongodb.BasicDBObject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ms.idrea.umbrellapanel.api.chief.gameserver.GameServer;
import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.core.permissions.PanelUser;
import ms.idrea.umbrellapanel.api.util.Address;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.server.ConsoleEndPoint;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.server.ManageEndPoint;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.server.SendCommandEndPoint;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.server.ViewEndPoint;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.user.ChangePasswordEndPoint;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.user.LoginEndPoint;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.user.LogoutEndPoint;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.users.CreateEndPoint;
import ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.users.ListEndPoint;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class V1EndPoints {

	public static void init(EndPointManager manager) {
		// server
		new ConsoleEndPoint(manager);
		new ManageEndPoint(manager);
		new SendCommandEndPoint(manager);
		new ViewEndPoint(manager);
		// servers
		new CreateEndPoint(manager);
		new ListEndPoint(manager);
		// user
		new ChangePasswordEndPoint(manager);
		new LoginEndPoint(manager);
		new LogoutEndPoint(manager);
		// users
		new ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.servers.CreateEndPoint(manager);
		new ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.servers.ListEndPoint(manager);
		new ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.users.ManageEndPoint(manager);
		new ms.idrea.umbrellapanel.chief.webapi.endpoints.v1.users.ViewEndPoint(manager);
	}

	public static BasicDBObject convertGameServer(GameServer server) {
		BasicDBObject s = new BasicDBObject();
		s.append("id", server.getId());
		s.append("name", server.getName());
		BasicDBObject w = new BasicDBObject();
		w.append("workerId", server.getWorkerId());
		w.append("online", server.getWorker() != null);
		Address workerAdress = server.getWorkerAddress();
		if (workerAdress != null) {
			w.append("address", workerAdress.toString());
			w.append("ip", workerAdress.getHost());
			w.append("port", workerAdress.getPort());
		}
		s.append("worker", w);
		s.append("isRunning", server.isRunning());
		s.append("address", server.getAddress().toString());
		s.append("ip", server.getAddress().getHost());
		s.append("port", server.getAddress().getPort());
		s.append("startcommand", server.getStartCommand());
		return s;
	}

	public static BasicDBObject convertPanelUser(PanelUser user) {
		BasicDBObject u = new BasicDBObject();
		u.append("id", user.getId());
		u.append("name", user.getName());
		BasicDBObject permissions = new BasicDBObject();
		for (int server : user.getPermissions().keySet()) {
			permissions.append(String.valueOf(server), String.valueOf(user.getPermission(server)));
		}
		u.append("permissions", permissions);
		return u;
	}
}
