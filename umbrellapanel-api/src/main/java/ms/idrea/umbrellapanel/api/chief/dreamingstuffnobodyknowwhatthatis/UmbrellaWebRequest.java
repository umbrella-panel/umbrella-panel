package ms.idrea.umbrellapanel.api.chief.dreamingstuffnobodyknowwhatthatis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.eclipse.jetty.server.Request;

@Getter
@AllArgsConstructor
public class UmbrellaWebRequest {

	String context;
	Request request;
	HttpServletRequest httpServletRequest;
	HttpServletResponse HttpServletResponse;
}
