package org.openactivities.model;

import lombok.Getter;

public class Image
{

	@Getter
	private String url;

	public Image(String url)
	{
		this.url = url;
	}

}
