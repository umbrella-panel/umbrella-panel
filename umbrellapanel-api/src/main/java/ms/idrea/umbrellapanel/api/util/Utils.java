package ms.idrea.umbrellapanel.api.util;

public class Utils {

	private Utils() {
	}

	public static boolean isLong(String s) {
		if (s == null) {
			return false;
		}
		try {
			Long.parseLong(s);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean isInteger(String s) {
		if (s == null) {
			return false;
		}
		try {
			Integer.parseInt(s);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static String fixEndPointString(String endpoint) {
		if (endpoint.charAt(0) != '/') {
			endpoint = "/" + endpoint;
		}
		if (endpoint.charAt(endpoint.length() - 1) == '/') {
			endpoint = endpoint.substring(0, endpoint.length() - 1); // cut
		}
		return endpoint;
	}
}
