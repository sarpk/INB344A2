package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import crawler.CollectNewsArticles;
import crawler.CollectNewsFeeds;
import crawler.DocumentAddress;
import engine.IndexDocuments;
import engine.SearchDocuments;

public class SearchPageHandle implements HttpHandler {
	public void handle(HttpExchange exchange) throws IOException {
		String requestMethod = exchange.getRequestMethod();
		if (requestMethod.equalsIgnoreCase("GET")) {

			String webAddress = "Web Address Not Found";
			String searchQuery = "Search Query Not Found";
			if (exchange.getRequestURI().getQuery() == null) {

			} else {
				String httpQuery = URLDecoder.decode(exchange.getRequestURI()
						.getRawQuery(), "UTF-8");

				String userInput[] = httpQuery.split("&");
				webAddress = userInput[1].split("=")[1];
				searchQuery = userInput[0].split("=")[1];

			}
			String top = "<!DOCTYPE html>\n<html>\n<body>\n";
			String bottom = "\n<p><b>Note:</b> This page is done for INB344 Assignment 2.</p>\n"
					+ "\n</body>\n</html>\n";
			String resultsP = "<script>function goBack(){window.history.back()}</script>"
					+ "<input type=\"button\" value=\"Back to Search\" onclick=\"goBack()\">"
					+ "<p><b>Results for query "
					+ searchQuery
					+ ":</b></p><br>";

			URL checkURL = new URL(webAddress);
			String checkWebAddress = checkURL.getHost();

			CollectNewsArticles articles = CollectNewsArticles.getCollector();

			if (articles == null
					|| !CollectNewsArticles.getWebAddress().equals(webAddress)) {
				System.out.println("Creating a new crawler");
				articles = new CollectNewsArticles(webAddress, checkWebAddress);
				bottom = "<meta http-equiv=\"refresh\" content=\"2\">" + bottom;
			} else if (!articles.crawlFinished()) {
				bottom = "<meta http-equiv=\"refresh\" content=\"10\">"
						+ bottom;
			}

			IndexDocuments indexer = new IndexDocuments(
					FileLocations.newsArticles, checkWebAddress);

			SearchDocuments searcher = new SearchDocuments(
					FileLocations.newsArticles, checkWebAddress, searchQuery,
					15);

			if (searcher != null) {
				// System.out.println("In doc adder");
				List<String> pages = searcher.getPages(0, 10);
				if (pages == null) {
					System.out.println("Null Pages");
				} else {
					// System.out.println("in Else");
					DocumentAddress docAddr = new DocumentAddress(pages);
					// System.out.println("doc adder added");
					List<String> urls = docAddr.getURLS();
					// System.out.println("Got URLS");
					if (urls == null) {
						System.out.println("URL null here");
					}
					// String html = urls.get(0);
					int docN = 0;
					for (String html : urls) {
						String options = String
								.format("<p>%d. <a href=\"%s\" target=\"_blank\">%s</a></p>",
										++docN, html, html);
						// System.out.println(options);
						resultsP = resultsP + options;
					}
				}
			}

			String writeResponse = top + resultsP + bottom;

			exchange.sendResponseHeaders(200, writeResponse.length());
			OutputStream os = exchange.getResponseBody();
			os.write(writeResponse.getBytes());
			os.close();

		}
	}
}
