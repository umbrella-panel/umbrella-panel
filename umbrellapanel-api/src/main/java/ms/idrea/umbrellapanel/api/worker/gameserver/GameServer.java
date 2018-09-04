package ms.idrea.umbrellapanel.api.worker.gameserver;

public interface GameServer {

	int getId();

	String getStartCommand();

	void setStartCommand(String startCommand);

	void delete();
}
