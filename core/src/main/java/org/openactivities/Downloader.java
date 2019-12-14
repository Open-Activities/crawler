package org.openactivities;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Downloader
{

	private String start = "https://www.lido-berlin.de";
	private Pattern patternLinks = Pattern.compile("/events/.*");

	public Downloader(String start, String linksPattern)
	{
		this.start = start;
		this.patternLinks = Pattern.compile(linksPattern);
	}

	public void crawl() throws MalformedURLException, IOException
	{
		FileCache fileCache = new FileCache();

		URL url = new URL(start);
		fileCache.download(start);

		Path file = fileCache.file(start);
		BufferedReader reader = Files.newBufferedReader(file);
		String text = IOUtils.toString(reader);

		Document document = Jsoup.parse(text);
		Elements links = document.getElementsByTag("a");
		for (int i = 0; i < links.size(); i++) {
			Element link = links.get(i);
			String href = link.attr("href");
			Matcher matcher = patternLinks.matcher(href);
			if (matcher.matches()) {
				URL linkUrl = new URL(url, href);
				fileCache.download(linkUrl.toString());
			}
		}
	}

}
