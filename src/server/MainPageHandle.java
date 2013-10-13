package server;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class MainPageHandle implements HttpHandler {
	private String response;
    public MainPageHandle(String mainSearchRes) {
    	response = mainSearchRes;
	}

	public void handle(HttpExchange t) throws IOException {
        //String response = "This is the main page response";
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
