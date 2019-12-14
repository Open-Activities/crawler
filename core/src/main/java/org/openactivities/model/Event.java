package org.openactivities.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import lombok.Getter;

public class Event
{

	@Getter
	private Set<String> types;
	@Getter
	private String name;
	@Getter
	private LocalDateTime startDate;
	@Getter
	private Location location;
	@Getter
	private List<Image> images;
	@Getter
	private String description;

	public Event(Set<String> types, String name, LocalDateTime startDate,
			Location location, List<Image> images, String description)
	{
		this.types = types;
		this.name = name;
		this.startDate = startDate;
		this.location = location;
		this.images = images;
		this.description = description;
	}

}
