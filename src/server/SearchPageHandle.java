package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class SearchPageHandle implements HttpHandler {
	public void handle(HttpExchange exchange) throws IOException {
		String requestMethod = exchange.getRequestMethod();
		if (requestMethod.equalsIgnoreCase("GET")) {
			Headers responseHeaders = exchange.getResponseHeaders();
			responseHeaders.set("Content-Type", "text/plain");
			exchange.sendResponseHeaders(200, 0);

			OutputStream responseBody = exchange.getResponseBody();
			Headers requestHeaders = exchange.getRequestHeaders();

			String webAddress = "Web Address Not Found";
			String searchQuery = "Search Query Not Found";
			if (exchange.getRequestURI().getQuery() == null) {

			}
			else {
				String httpQuery = URLDecoder.decode(exchange.getRequestURI()
						.getRawQuery(), "UTF-8");

				String userInput[] = httpQuery.split("&");
				webAddress = userInput[0].split("=")[1];
				searchQuery = userInput[1].split("=")[1];

			}
			System.out.println(exchange.getRequestURI());
			System.out.println(exchange.getRequestURI().getRawQuery());
			System.out.println(exchange.getRequestURI().getQuery());
			Set<String> keySet = requestHeaders.keySet();
			Iterator<String> iter = keySet.iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				List values = requestHeaders.get(key);
				String s = key + " = " + values.toString() + "\n";
				responseBody.write(s.getBytes());
			}
			responseBody.write("\n".getBytes());
			responseBody.write(webAddress.getBytes());
			responseBody.write("\n".getBytes());
			responseBody.write(searchQuery.getBytes());

			responseBody.close();
			
		}
	}
}
