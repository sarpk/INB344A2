package crawler;

import server.FileLocations;
import crawl.cr.CollectorClass;

public class CollectNewsArticles {
	private static CollectorClass collectFromAddr = null;
	private static CollectNewsArticles collectInstance = null;
	private static String webAddress = null;
	
	public CollectNewsArticles(String webAddress, String hostAddr) {
		// TODO Auto-generated constructor stub
		CollectNewsArticles.webAddress = webAddress;
		collectInstance = this;
		String locationToBeSaved = FileLocations.newsArticles + "/" + hostAddr;
		collectFromAddr = new CollectorClass(webAddress, locationToBeSaved);
		collectFromAddr.setDepth(10);
		collectFromAddr.setMaxFetch(200);
		collectFromAddr.setRobots(true);
		collectFromAddr.setPoliteness(100);
		collectFromAddr.setCrawlerAmount(15);
		collectFromAddr.startCrawling();
	}

	public boolean crawlFinished() {
		// TODO Auto-generated method stub
		return collectFromAddr.isCrawlingFinished();
	}
	
	public static CollectNewsArticles getCollector() {
		return collectInstance;
	}
	
	public static String getWebAddress() {
		return webAddress;
	}
	
}
