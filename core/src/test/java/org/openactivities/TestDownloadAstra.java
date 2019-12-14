package org.openactivities;

import java.io.IOException;
import java.net.MalformedURLException;

public class TestDownloadAstra
{

	public static void main(String[] args)
			throws MalformedURLException, IOException
	{
		String start = "https://www.astra-berlin.de";
		String linksPattern = "/events/.*";
		FileCache fileCache = new FileCache();
		Downloader downloader = new Downloader(fileCache, start, linksPattern);
		downloader.crawl();
	}

}
