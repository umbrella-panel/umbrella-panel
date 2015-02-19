package ms.idrea.umbrellapanel.api.chief.dreamingstuffnobodyknowwhatthatis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UmbrellaWebRequest {

	String context;
	Request request;
	HttpServletRequest httpServletRequest;
	HttpServletResponse HttpServletResponse;
}
