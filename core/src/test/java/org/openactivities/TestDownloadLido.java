package org.openactivities;

import java.io.IOException;
import java.net.MalformedURLException;

public class TestDownloadLido
{

	public static void main(String[] args)
			throws MalformedURLException, IOException
	{
		String start = "https://www.lido-berlin.de";
		String linksPattern = "/events/.*";
		FileCache fileCache = new FileCache();
		Downloader downloader = new Downloader(fileCache, start, linksPattern);
		downloader.crawl();
	}

}
