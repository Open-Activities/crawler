package org.openactivities;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.openactivities.model.Event;

import de.topobyte.melon.paths.PathUtil;
import de.topobyte.system.utils.SystemPaths;

public class TestParseAll
{

	public static void main(String[] args)
			throws MalformedURLException, IOException
	{
		Path repoData = SystemPaths.HOME.resolve("github/Open-Activities/data");
		Path type1 = repoData.resolve("type1");

		FileCache fileCache = new FileCache();

		List<Path> files = PathUtil.list(type1);
		for (Path file : files) {
			try (InputStream input = Files.newInputStream(file)) {
				List<String> lines = IOUtils.readLines(input);
				String start = lines.get(0);
				String linksPattern = lines.get(1);
				parse(fileCache, start, linksPattern);
			}
		}
	}

	private static void parse(FileCache fileCache, String start,
			String linksPattern) throws MalformedURLException, IOException
	{
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
