package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import crawler.CollectNewsFeeds;
import crawler.DocumentAddress;
import engine.IndexDocuments;
import engine.SearchDocuments;

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
		if (!webAddress.startsWith("http://")) {
			webAddress = "http://" + webAddress;
		}
		if (!webAddress.endsWith("/")) {
			webAddress = webAddress + "/";
		}
		// System.out.println(webAddress);
		CollectNewsFeeds feeds = null;
		IndexDocuments indexer = null;
		SearchDocuments searcher = null;
		URL checkURL = new URL(webAddress);
		String checkWebAddress = checkURL.getHost();
		if (CollectNewsFeeds.getCollector() == null) {
			System.out.println("it is null");
			lastHTMLTags = "<meta http-equiv=\"refresh\" content=\"2\">"
					+ lastHTMLTags;
			feeds = new CollectNewsFeeds(webAddress, checkWebAddress);
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
					indexer = new IndexDocuments(
							FileLocations.collectNewsFeeds, checkWebAddress);
					searcher = new SearchDocuments(
							FileLocations.collectNewsFeeds, checkWebAddress,
							"news", 5);
					// searcher.getPages(0, 5);
				}
				System.out.println("It is still finished "
						+ feeds.crawlFinished());
			} else {
				lastHTMLTags = "<meta http-equiv=\"refresh\" content=\"2\">"
						+ lastHTMLTags;
				feeds = new CollectNewsFeeds(webAddress, checkWebAddress);
			}
		}
		//System.out.println("After the cond");
		// Index downloaded pages
		if (feeds.crawlFinished() && indexer == null) {
			indexer = new IndexDocuments(FileLocations.collectNewsFeeds,
					checkWebAddress);
			searcher = new SearchDocuments(FileLocations.collectNewsFeeds,
					checkWebAddress, "news", 5);
			// searcher.getPages(0, 5);
		}
		String sites = "";
		if (searcher != null) {
			//System.out.println("In doc adder");
			List<String> pages = searcher.getPages(0, 5);
			if (pages == null) {
				System.out.println("Null Pages");
			} else {
				DocumentAddress docAddr = new DocumentAddress(pages);
				List<String> urls = docAddr.getURLS();
				//System.out.println("Got URLS");
				if (urls == null) {
					System.out.println("URL null here");
				}
				// String html = urls.get(0);
				for (String html : urls) {
					URL newURL = new URL(html);
					Document doc = Jsoup.parse(newURL, 10000);
					String pageTitle = doc.title();
					String options = String
							.format("<option value=\"%s\">%s</option>", html,
									pageTitle);
					sites += options;
					//System.out.println(options);
				}
			}
		}

		String finalResp = part1 + webAddress + part2 + sites + part3
				+ lastHTMLTags;
		// System.out.println(finalResp);
		t.sendResponseHeaders(200, finalResp.length());
		OutputStream os = t.getResponseBody();
		os.write(finalResp.getBytes());
		os.close();
	}
}
