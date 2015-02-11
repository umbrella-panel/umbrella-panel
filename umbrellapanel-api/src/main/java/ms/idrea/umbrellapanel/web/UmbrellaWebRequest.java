package ms.idrea.umbrellapanel.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UmbrellaWebRequest {
	String context;
	Request request;
	HttpServletRequest httpServletRequest;
	HttpServletResponse HttpServletResponse;
}
