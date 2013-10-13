package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import crawler.CollectNewsFeeds;

public class SearchNewsPageHandle implements HttpHandler {
	private String response;

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}

	public SearchNewsPageHandle(String fileLoc) {
		response = fileLoc;
	}

	public void handle(HttpExchange t) throws IOException {
		// String response = "This is the main page response";

		String part1 = readFile("htmlpages/searchnews1.html",
				Charset.defaultCharset());
		String part2 = readFile("htmlpages/searchnews2.html",
				Charset.defaultCharset());
		String part3 = readFile("htmlpages/searchnews3.html",
				Charset.defaultCharset());
		String lastHTMLTags = "</body>\n</html>";

		String webAddress = "";
		if (t.getRequestURI().getQuery() == null) {

		} else {
			String httpQuery = URLDecoder.decode(t.getRequestURI()
					.getRawQuery(), "UTF-8");

			webAddress = httpQuery.split("=")[1];
		}
		// System.out.println(webAddress);
		CollectNewsFeeds feeds = null;
		// To-Do get websites
		if (CollectNewsFeeds.getCollector() == null) {
			System.out.println("it is null");
			lastHTMLTags = "<meta http-equiv=\"refresh\" content=\"2\">"
					+ lastHTMLTags;
			feeds = new CollectNewsFeeds(webAddress);
		} else {
			feeds = CollectNewsFeeds.getCollector();
			String addr = CollectNewsFeeds.getWebAddress();
			if (addr.equals(webAddress)) {
				System.out.println("Loop it");
				if (!feeds.crawlFinished()) {
					String fnp = "\"Find News Pages\"";
					int indexF = part2.indexOf(fnp);
					if (indexF != -1) {
						part2 = part2.substring(0, indexF + fnp.length())
								+ "disabled"
								+ part2.substring(indexF + fnp.length(),
										part2.length());
					}
					lastHTMLTags = "<meta http-equiv=\"refresh\" content=\"10\">"
							+ lastHTMLTags;
				}
				System.out.println("It is still finished "
						+ feeds.crawlFinished());
			} else {
				lastHTMLTags = "<meta http-equiv=\"refresh\" content=\"2\">"
						+ lastHTMLTags;
				feeds = new CollectNewsFeeds(webAddress);
			}
		}

		String sites = "<option value=\"qut.edu.au\">QUT Research News</option>";
		String finalResp = part1 + webAddress + part2 + sites + part3
				+ lastHTMLTags;
		// System.out.println(finalResp);
		t.sendResponseHeaders(200, finalResp.length());
		OutputStream os = t.getResponseBody();
		os.write(finalResp.getBytes());
		os.close();
	}
}