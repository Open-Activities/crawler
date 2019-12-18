package org.openactivities;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Downloader
{

	private FileCache fileCache;
	private String start;
	private Pattern patternLinks;

	public Downloader(FileCache fileCache, String start, String linksPattern)
	{
		this.fileCache = fileCache;
		this.start = start;
		this.patternLinks = Pattern.compile(linksPattern);
	}

	public void crawl() throws MalformedURLException, IOException
	{
		crawl(null);
	}

	public void crawl(Consumer<Path> processor)
			throws MalformedURLException, IOException
	{
		URL url = new URL(start);
		fileCache.download(start);

		Path file = fileCache.file(start);
		BufferedReader reader = Files.newBufferedReader(file);
		String text = IOUtils.toString(reader);

		Set<Path> done = new HashSet<>();

		Document document = Jsoup.parse(text);
		Elements links = document.getElementsByTag("a");
		for (int i = 0; i < links.size(); i++) {
			Element link = links.get(i);
			String href = link.attr("href");
			Matcher matcher = patternLinks.matcher(href);
			if (matcher.matches()) {
				URL linkUrl = new URL(url, href);
				fileCache.download(linkUrl.toString());
				Path f = fileCache.file(linkUrl.toString());

				if (done.contains(f)) {
					continue;
				}

				done.add(f);

				if (processor != null) {
					processor.accept(f);
				}
			}
		}
	}

}
