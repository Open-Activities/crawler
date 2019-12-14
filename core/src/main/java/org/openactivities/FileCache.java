package org.openactivities;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;

import de.topobyte.system.utils.SystemPaths;

public class FileCache
{

	private Path cache = SystemPaths.HOME.resolve("oa/webcache");

	public FileCache() throws IOException
	{
		Files.createDirectories(cache);
	}

	public Path file(String url)
	{
		String stripped = strip(url, "https://", "http://");
		int firstSlash = stripped.indexOf("/");
		String dirname = stripped;
		if (firstSlash > 0) {
			dirname = stripped.substring(0, firstSlash + 1);
		}
		String filename = stripped.replaceAll("/", "_");

		Path dir = cache.resolve(dirname);
		Path file = dir.resolve(filename);
		return file;
	}

	public void download(String url) throws MalformedURLException, IOException
	{
		Path file = file(url);
		if (Files.exists(file)) {
			return;
		}

		Path dir = file.getParent();
		Files.createDirectories(dir);

		System.out.println("Downloading: " + file);

		InputStream input = new URL(url).openStream();
		BufferedWriter writer = Files.newBufferedWriter(file);
		IOUtils.copy(input, writer, StandardCharsets.UTF_8);
	}

	private String strip(String url, String... prefixes)
	{
		for (String prefix : prefixes) {
			if (url.startsWith(prefix)) {
				return url.substring(prefix.length());
			}
		}
		return url;
	}

}
