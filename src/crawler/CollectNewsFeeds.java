package crawler;

import server.FileLocations;
import crawl.cr.CollectorClass;

public class CollectNewsFeeds {
	private static CollectorClass collectFromAddr = null;
	private static CollectNewsFeeds collectInstance = null;
	private static String webAddress = null;
	public CollectNewsFeeds(String webAddress, String hostAddr) {
		// TODO Auto-generated constructor stub
		CollectNewsFeeds.webAddress = webAddress;
		collectInstance = this;
		String locationToBeSaved = FileLocations.collectNewsFeeds + "/" + hostAddr;
		collectFromAddr = new CollectorClass(webAddress, locationToBeSaved);
		collectFromAddr.setDepth(3);
		collectFromAddr.setMaxFetch(100);
		collectFromAddr.setRobots(true);
		collectFromAddr.setPoliteness(100);
		collectFromAddr.setCrawlerAmount(10);
		collectFromAddr.startCrawling();
	}

	public boolean crawlFinished() {
		// TODO Auto-generated method stub
		return collectFromAddr.isCrawlingFinished();
	}
	
	public static CollectNewsFeeds getCollector() {
		return collectInstance;
	}
	
	public static String getWebAddress() {
		return webAddress;
	}

}
