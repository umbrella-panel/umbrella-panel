package ms.idrea.umbrellapanel.web.webservice;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ms.idrea.umbrellapanel.web.UmbrellaWebRequest;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class WebHandler extends AbstractHandler{
	
	private UmbrellaWebServer server;
	
	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest arg2,HttpServletResponse arg3) throws IOException, ServletException {
		server.request(new UmbrellaWebRequest(arg0, arg1, arg2, arg3));
	}

}
