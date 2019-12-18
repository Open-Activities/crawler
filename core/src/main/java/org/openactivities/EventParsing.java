package org.openactivities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.openactivities.model.Event;
import org.openactivities.model.Image;
import org.openactivities.model.Location;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.topobyte.gson.GsonUtil;

public class EventParsing
{

	public static List<Event> parse(Path file) throws IOException
	{
		List<Event> events = new ArrayList<>();

		String html = FileUtils.readFileToString(file.toFile(),
				StandardCharsets.UTF_8);
		Document document = Jsoup.parse(html);

		List<JsonElement> jsons = EventParsing.load(document);
		for (JsonElement json : jsons) {
			List<Event> extracted = EventParsing.extractEvent(json);
			events.addAll(extracted);
		}

		return events;
	}

	public static List<JsonElement> load(Document document) throws IOException
	{
		Elements scripts = document.select("script[type$=application/ld+json]");

		List<JsonElement> jsons = new ArrayList<>();

		for (Element script : scripts) {
			List<Node> nodes = script.childNodes();
			for (Node node : nodes) {
				if ("#data".equals(node.nodeName())) {
					JsonElement json = parse(node.toString());
					if (json != null) {
						jsons.add(json);
					}
				}
			}
		}

		return jsons;
	}

	private static JsonElement parse(String text) throws IOException
	{
		String trimmed = text.trim();
		return new JsonParser().parse(trimmed);
	}

	public static void printSomeInfo(JsonElement element) throws IOException
	{
		System.out.println(GsonUtil.prettyPrint(element));
		JsonElement context = element.getAsJsonObject().get("@context");
		System.out.println(context);
		JsonElement type = element.getAsJsonObject().get("@type");
		System.out.println("type: " + type);

		JsonElement name = element.getAsJsonObject().get("name");
		JsonElement startDate = element.getAsJsonObject().get("startDate");
		JsonElement location = element.getAsJsonObject().get("location");
		System.out.println(name);
		System.out.println(startDate);
		System.out.println(location);
	}

	public static List<Event> extractEvent(JsonElement element)
			throws IOException
	{
		List<Event> results = new ArrayList<>();
		if (element.isJsonObject()) {
			JsonObject jo = (JsonObject) element;
			results.add(extractSingleEvent(jo));
		} else if (element.isJsonArray()) {
			JsonArray array = element.getAsJsonArray();
			for (int i = 0; i < array.size(); i++) {
				JsonObject jo = (JsonObject) array.get(i);
				results.add(extractSingleEvent(jo));
			}
		}
		return results;
	}

	private static Event extractSingleEvent(JsonObject jo)
	{
		JsonElement jType = jo.get("@type");
		JsonElement jName = jo.get("name");
		JsonElement jStartDate = jo.get("startDate");
		JsonElement jLocation = jo.get("location");
		JsonElement jImage = jo.get("image");
		JsonElement jDescription = jo.get("description");

		// types
		Set<String> types = new HashSet<>();
		if (jType.isJsonPrimitive()) {
			types.add(jType.getAsString());
		} else if (jType.isJsonArray()) {
			JsonArray jTypes = jType.getAsJsonArray();
			for (int i = 0; i < jTypes.size(); i++) {
				types.add(jTypes.get(i).getAsString());
			}
		}

		// name
		String name = jName.getAsString();

		// date and time
		String sStartDate = jStartDate.getAsString();
		LocalDateTime startDate = LocalDateTime.parse(sStartDate,
				DateTimeFormatter.ISO_DATE_TIME);

		// location
		Location location = location(jLocation);

		// images
		List<Image> images = images(jImage);

		// description
		String description = null;
		if (jDescription != null) {
			description = jDescription.getAsString();
		}

		return new Event(types, name, startDate, location, images, description);
	}

	private static List<Image> images(JsonElement jImage)
	{
		List<Image> images = new ArrayList<>();
		if (jImage != null) {
			JsonArray jImages = jImage.getAsJsonArray();
			for (int i = 0; i < jImages.size(); i++) {
				JsonElement jeImage = jImages.get(i);
				if (jeImage.isJsonPrimitive()) {
					String url = jeImage.getAsString();
					images.add(new Image(url));
				} else if (jeImage.isJsonObject()) {
					JsonObject joImage = jImages.get(i).getAsJsonObject();
					String url = joImage.get("url").getAsString();
					images.add(new Image(url));
				}
			}
		}
		return images;
	}

	private static Location location(JsonElement jLocation)
	{
		JsonObject joLocation = jLocation.getAsJsonObject();
		String type = joLocation.get("@type").getAsString();
		if (!type.equals("Place")) {
			return null;
		}

		String locationName = joLocation.get("name").getAsString();
		JsonElement jeAddress = joLocation.get("address");
		String address = null;
		if (jeAddress.isJsonPrimitive()) {
			address = jeAddress.getAsString();
		} else if (jeAddress.isJsonObject()) {
			JsonObject joAddress = (JsonObject) jeAddress;
			JsonElement streetAddress = joAddress.get("streetAddress");
			if (streetAddress.isJsonPrimitive()) {
				address = streetAddress.getAsString();
			}
		}
		return new Location(locationName, address);
	}

}
