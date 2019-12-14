package org.openactivities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.openactivities.model.Event;

public class TestParseLido
{

	public static void main(String[] args)
			throws MalformedURLException, IOException
	{
		String start = "https://www.lido-berlin.de";
		String linksPattern = "/events/.*";
		FileCache fileCache = new FileCache();
		Downloader downloader = new Downloader(fileCache, start, linksPattern);
		downloader.crawl(file -> {
			try {
				List<Event> events = EventParsing.parse(file);
				for (Event event : events) {
					System.out.println(event.getName());
					System.out.println(event.getStartDate());
				}
			} catch (Throwable e) {
				System.out.println("Error while parsing file " + file);
				e.printStackTrace();
			}
		});
	}

}
