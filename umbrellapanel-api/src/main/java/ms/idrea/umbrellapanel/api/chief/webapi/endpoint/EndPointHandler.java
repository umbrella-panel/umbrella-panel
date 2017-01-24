package ms.idrea.umbrellapanel.api.chief.webapi.endpoint;

import javax.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

public interface EndPointHandler {

	String getEndPoint();

	boolean isVaild(HttpServletRequest request);

	EndPointResponse getResponse(HttpServletRequest request);

	EndPointResponse getInValidResponse();

	@Getter
	@ToString
	@AllArgsConstructor
	public static class EndPointResponse {

		private int httpCode;
		private String response;
	}
}
