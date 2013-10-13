package crawler;

import crawl.cr.CollectorClass;

public class CollectNewsFeeds {
	private static CollectorClass collectFromAddr = null;
	private static CollectNewsFeeds collectInstance = null;
	private static String webAddress = null;
	public CollectNewsFeeds(String webAddress) {
		// TODO Auto-generated constructor stub
		CollectNewsFeeds.webAddress = webAddress;
		collectInstance = this;
		collectFromAddr = new CollectorClass(webAddress, "NewsCollection/");
		collectFromAddr.setDepth(4);
		collectFromAddr.setMaxFetch(50);
		collectFromAddr.setRobots(true);
		collectFromAddr.setPoliteness(150);
		collectFromAddr.setCrawlerAmount(3);
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
