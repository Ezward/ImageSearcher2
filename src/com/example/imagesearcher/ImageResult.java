package com.example.imagesearcher;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class ImageResult implements Serializable
{
	private static final long	serialVersionUID	= -1872471231726942114L;
	private final String fullUrl;
	private final String thumbUrl;
	
	public ImageResult(final String theFullUrl, final String theThumbUrl)
	{
		fullUrl = theFullUrl;
		thumbUrl = theThumbUrl;
	}
	
	public ImageResult(final JSONObject theJsonObject)
	{
		String theFullUrl = null;
		String theThumbUrl = null;
		try
		{
			theFullUrl = theJsonObject.getString("url");
			theThumbUrl = theJsonObject.getString("tbUrl");
		}
		catch (JSONException e)
		{
			// just skip it
		}
		fullUrl = theFullUrl;
		thumbUrl = theThumbUrl;
	}
	
	public String getFullUrl()
	{
		return fullUrl;
	}
	public String getThumbUrl()
	{
		return thumbUrl;
	}
	
	@Override
	public String toString()
	{
		return thumbUrl;
	}

	public static ArrayList<? extends ImageResult> fromJSONArray(JSONArray theJsonArray)
	{
		final int theCount = theJsonArray.length();
		final ArrayList<ImageResult> theImageResults = new ArrayList<ImageResult>(theCount);
		
		for(int i = 0; i < theCount; i += 1)
		{
			try
			{
				final ImageResult theImageResult = new ImageResult(theJsonArray.getJSONObject(i));
				theImageResults.add(theImageResult);
			}
			catch (JSONException e)
			{
				// just skip it
			}
		}
		
		return theImageResults;
	}
}
