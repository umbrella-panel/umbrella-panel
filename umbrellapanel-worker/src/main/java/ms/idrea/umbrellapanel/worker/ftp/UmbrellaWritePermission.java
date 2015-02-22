package ms.idrea.umbrellapanel.worker.ftp;

import java.util.regex.Pattern;

import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.apache.ftpserver.usermanager.impl.WriteRequest;

public class UmbrellaWritePermission extends WritePermission {

	public static final Pattern ALLOWED_FILES = Pattern.compile("\\/\\d+.*", Pattern.DOTALL);

	@Override
	public AuthorizationRequest authorize(final AuthorizationRequest request) {
		if (request instanceof WriteRequest) {
			WriteRequest writeRequest = (WriteRequest) request;
			String requestFile = writeRequest.getFile();
			if (ALLOWED_FILES.matcher(requestFile).matches()) {
				return writeRequest;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
