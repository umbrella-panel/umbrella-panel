package ms.idrea.umbrellapanel.chief.webapi;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ms.idrea.umbrellapanel.api.chief.webapi.EndPointManager;
import ms.idrea.umbrellapanel.api.chief.webapi.endpoint.EndPointHandler;
import ms.idrea.umbrellapanel.api.chief.webapi.endpoint.EndPointHandler.EndPointResponse;

import org.apache.commons.lang3.time.StopWatch;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.mongodb.BasicDBObject;

public class RequestHandler extends AbstractHandler {

	private static final String UNKNOWN_ENDPOINT = new BasicDBObject("error", "unknown endpoint").toString();
	private static final String UNKNOWN_ERROR = new BasicDBObject("error", "unknown error").toString();
	private EndPointManager manager;

	public RequestHandler(EndPointManager manager) {
		this.manager = manager;
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		StopWatch timer = new StopWatch();
		timer.start();
		response.setHeader("Server", "Umbrella-Chief");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("x-powered-by", "Java/Jetty");
		response.setHeader("x-robots-tag", "noarchive");
		if (target.equals("") || target.equals("/")) {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().println("<b>Welcome to Umbrella-Chief api v1</b><br>");
			response.getWriter().println("All known endpoints:<br>");
			response.getWriter().println("<ul>");
			for (EndPointHandler endpoint : manager.getAllEndPoints()) {
				response.getWriter().println("<li>" + endpoint.getClass().getSimpleName() + ": " + endpoint.getEndPoint() + "</li>");
			}
			response.getWriter().println("</ul>");
			response.setHeader("x-unicorn-server", "true");
			writeAndEnd(baseRequest, response, HttpServletResponse.SC_OK, timer);
			return;
		}
		response.setContentType("application/json;charset=utf-8");
		EndPointHandler handler = manager.getEndPointHandler(target);
		if (handler == null) {
			response.getWriter().print(UNKNOWN_ENDPOINT);
			writeAndEnd(baseRequest, response, HttpServletResponse.SC_NOT_FOUND, timer);
			return;
		}
		try {
			EndPointResponse endPointResponse;
			if (!handler.isVaild(request)) {
				endPointResponse = handler.getInValidResponse();
			} else {
				endPointResponse = handler.getResponse(request);
			}
			response.getWriter().print(endPointResponse.getResponse());
			writeAndEnd(baseRequest, response, endPointResponse.getHttpCode(), timer);
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.getWriter().print(UNKNOWN_ERROR);
		writeAndEnd(baseRequest, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, timer);
	}

	private static void writeAndEnd(Request baseRequest, HttpServletResponse response, int code, StopWatch watch) {
		response.setHeader("x-processing-time-ms", String.valueOf(watch.getTime()));
		response.setHeader("x-processing-time-nano", String.valueOf(watch.getNanoTime()));
		response.setStatus(code);
		baseRequest.setHandled(true);
	}
}
