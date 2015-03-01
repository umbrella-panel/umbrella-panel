package ms.idrea.umbrellapanel.api.util;

public class Utils {

	private Utils() {
	}

	public static boolean isInteger(String s) {
		if (s == null) {
			return false;
		}
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
