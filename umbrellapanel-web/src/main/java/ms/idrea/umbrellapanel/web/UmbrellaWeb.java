package ms.idrea.umbrellapanel.web;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ms.idrea.umbrellapanel.chief.UmbrellaChief;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.io.FileUtils;

public class UmbrellaWeb {

	private static UmbrellaWeb instance;

	public static void main(String... args) {
		instance = new UmbrellaWeb();
		instance.start();
	}

	private Tomcat tomcat;
	private UmbrellaChief chief;
	
	private void start() {
		try {
			setupFiles();
		} catch (IOException e) {
			throw new RuntimeException("Failed to setup files", e);
		}
		
		chief = (UmbrellaChief) UmbrellaChief.createInstance();
		chief.start();
		
		try {
			tomcat = new Tomcat();
			tomcat.getConnector().setDomain("0.0.0.0");
			tomcat.getConnector().setPort(chief.getChiefProperties().getWebPort());
			Context context = tomcat.addWebapp("", new File("web").getAbsolutePath());
			context.setConfigFile(new File("web/WEB-INF/web.xml").getAbsoluteFile().toURL());
			tomcat.start();
		} catch (Exception e) {
			throw new RuntimeException("Failed to start tomcat", e);
		}
		
		chief.enableConsole();
		Scanner scanner = new Scanner(System.in);
		String line;
		while ((line = scanner.nextLine()) != null) {
			if (line.equalsIgnoreCase("exit")) {
				break;
			} else {
				System.out.println("Type \"exit\" to exit the program!");
			}
		}
		System.out.println("Exiting..");
		scanner.close();
		shutdown();
	}

	private void shutdown() {
		try {
			tomcat.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		chief.shutdown();
	}

	public void setupFiles() throws IOException {
		try {
			FileUtils.deleteDirectory(new File("web"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		CodeSource src = UmbrellaWeb.class.getProtectionDomain().getCodeSource();
		if (src != null) {
			URL jar = src.getLocation();
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			ZipEntry ze = null;
			while ((ze = zip.getNextEntry()) != null) {
				if (ze.getName().startsWith("web/") && !ze.isDirectory()) {
					extractFile(ze.getName(), ze.getName());
				}
			}
		}
	}

	public static void extractFile(String in, String out) {
		try {
			URL inputUrl = UmbrellaWeb.class.getResource("/" + in);
			File dest = new File(out);
			FileUtils.copyURLToFile(inputUrl, dest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static UmbrellaWeb getInstance() {
		return instance;
	}

	
	public UmbrellaChief getChief() {
		return chief;
	}
}
