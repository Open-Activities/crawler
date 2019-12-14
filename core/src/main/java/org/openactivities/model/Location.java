package org.openactivities.model;

import lombok.Getter;

public class Location
{

	@Getter
	private String locationName;
	@Getter
	private String adress;

	public Location(String locationName, String adress)
	{
		this.locationName = locationName;
		this.adress = adress;
	}

}
