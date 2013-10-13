package server;

import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import com.sun.net.httpserver.HttpServer;

public class Controller {

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String mainSearchRes = readFile("htmlpages/mainsearch.html",
				Charset.defaultCharset());

		HttpServer server = HttpServer.create(new InetSocketAddress(8002), 0);
		server.createContext("/", new MainPageHandle(mainSearchRes));
		server.createContext("/search", new SearchPageHandle());
		server.setExecutor(null); // creates a default executor
		server.start();

		Scanner sc = new Scanner(System.in);
		String asd = "stay on";
		while (!asd.equals("turn off")) {
			asd = sc.nextLine();
		}

		server.stop(0);
		sc.close();

	}

}
