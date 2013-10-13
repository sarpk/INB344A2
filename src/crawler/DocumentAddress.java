package crawler;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import crawl.cr.ThreadBlock;

public class DocumentAddress {
	private static String SPLITTER = " ; ";
	private static String INDEX_FILE = "index.dat";
	private static List<String> URLs = null;
	private static ThreadBlock block = null;

	public DocumentAddress(List<String> pages) {
		block = ThreadBlock.getInstance();
		// System.out.println("In Document Addr");
		if (pages == null) {
			System.out.println("pages are null");
		} else {
			URLs = new ArrayList<String>();
			for (String s : pages) {
				String urlSubs[] = s.split("\\\\");
				String fileNameEncoded = urlSubs[urlSubs.length - 1];
				String urlFolder = "";
				for (int i = 0; i < urlSubs.length - 1; i++) {
					urlFolder += urlSubs[i] + "\\";
				}
				if (!urlFolder.endsWith("\\")) {
					urlFolder = urlFolder + "\\";
				}
				setURL(urlFolder, fileNameEncoded);

			}
		}
	}

	private void setURL(String urlFolder, String fileName) {
		synchronized (block) {
			//System.out.println(urlFolder + " and encoded " + fileName);
			String indexPath = urlFolder + INDEX_FILE;
			String content = "";
			File indexExist = new File(indexPath);
			if (indexExist.exists()) {
				content = readFile(indexPath, Charset.defaultCharset());
			}
			// System.out.println(content);
			int indexFound = content.indexOf(fileName);

			if (indexFound != -1) {
				int afterIndex = indexFound + fileName.length()
						+ SPLITTER.length();
				String nextSub = content
						.substring(afterIndex, content.length());
				int newLineDel = nextSub.indexOf(System.lineSeparator());
				String exactURL = nextSub.substring(0, newLineDel);
				URLs.add(exactURL);
				// System.out.println(nextSub.substring(0, newLineDel));
				// System.out.println(indexFound + " and " + nextLineI);
				// System.out.println(content.substring(indexFound, nextLineI));
			}
		}
	}

	public List<String> getURLS() {
		return URLs;
	}

	static String readFile(String path, Charset encoding) {
		byte[] encoded = null;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
}
