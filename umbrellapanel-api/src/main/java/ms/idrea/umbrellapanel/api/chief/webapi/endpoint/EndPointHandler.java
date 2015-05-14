package ms.idrea.umbrellapanel.api.chief.webapi.endpoint;

import javax.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

public interface EndPointHandler {

	public String getEndPoint();

	public boolean isVaild(HttpServletRequest request);

	public EndPointResponse getResponse(HttpServletRequest request);

	public EndPointResponse getInValidResponse();

	@Getter
	@ToString
	@AllArgsConstructor
	public static class EndPointResponse {

		private int httpCode;
		private String response;
	}
}
