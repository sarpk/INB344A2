package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import wordnet.WordNetQueryProcessor;

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
		String mainChoiceRes = readFile("htmlpages/mainchoice.html",
				Charset.defaultCharset());
		String searchNewsRes = readFile("htmlpages/searchnews.html",
				Charset.defaultCharset());
		int portNumber = 8010;
		for (int i = 0; i < args.length; i++) {
			if ("-port".equals(args[i])) {
				portNumber = Integer.valueOf(args[i + 1]);
				i++;
			}
			if ("-wordnet".equals(args[i])) {
				WordNetQueryProcessor.wnhome = args[i + 1];
				i++;
			}
		}
		
		
		System.out.println("Using port " + portNumber);
		System.out.println("Using wordnet location as  " + WordNetQueryProcessor.wnhome);
		HttpServer server = HttpServer.create(
				new InetSocketAddress(portNumber), 0);
		server.createContext("/", new MainPageHandle(mainChoiceRes));
		server.createContext("/mainsearch", new MainPageHandle(mainSearchRes));
		server.createContext("/findNews", new SearchNewsPageHandle(
				searchNewsRes));

		server.createContext("/searchNews", new SearchPageHandle());
		server.createContext("/offlineSearch", new SearchOfflineHandle());
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
