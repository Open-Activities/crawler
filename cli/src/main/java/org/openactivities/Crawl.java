package org.openactivities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.IOUtils;

import de.topobyte.melon.paths.PathUtil;
import de.topobyte.system.utils.SystemPaths;

public class Crawl
{

	public static void main(String[] args) throws IOException
	{
		Path repoData = SystemPaths.HOME.resolve("github/Open-Activities/data");
		Path type1 = repoData.resolve("type1");

		List<Path> files = PathUtil.list(type1);
		for (Path file : files) {
			try (InputStream input = Files.newInputStream(file)) {
				List<String> lines = IOUtils.readLines(input);
				String start = lines.get(0);
				String linksPattern = lines.get(1);
				crawl(start, linksPattern);
			}
		}
	}

	private static void crawl(String start, String linksPattern)
			throws IOException
	{
		FileCache fileCache = new FileCache();
		Downloader downloader = new Downloader(fileCache, start, linksPattern);
		downloader.crawl();
	}

}
