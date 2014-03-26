package com.example.imagesearcher;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class SearchSettings implements Serializable
{
	private static final long	serialVersionUID	= -6829278564857950896L;

	// query
	public final static String QUERY = "q";
	
	// domain to limit search
	public final static String AS_SITESEARCH = "as_sitesearch";
	
	// grayscale or color
	public final static String IMGC = "imgc";
	public final static String IMGC_GRAYSCALE = "gray";
	public final static String IMGC_COLOR = "color";
	
	// predominant color
	public final static String IMGCOLOR = "imgcolor";
	
	// image size
	public final static String IMGSZ = "imgsz";
	public final static String IMGSZ_SMALL = "icon";
	public final static String IMGSZ_MEDIUM = "medium";
	public final static String IMGSZ_LARGE = "xxlarge";
	public final static String IMGSZ_HUGE = "huge";
	
	// image type
	public final static String IMGTYPE = "imgtype";
	public final static String IMGTYPE_FACE = "face";
	public final static String IMGTYPE_PHOTO = "photo";
	public final static String IMGTYPE_CLIPART = "clipart";
	public final static String IMGTYPE_LINEART = "lineart";
	
	
	// keep settings in map, so we can iterate to create query string
	private final Map<String, String> settingsMap = new HashMap<String, String>();
	
	public Iterator<Map.Entry<String, String>> iterator()
	{
		return settingsMap.entrySet().iterator();
	}
	public SearchSettings clear()
	{
		settingsMap.clear();
		return this;
	}
	
	public final String query()
	{
		return get(QUERY);
	}
	public final SearchSettings query(final String theQuery)
	{
		return set(QUERY, theQuery);
	}
	public final String siteUrl()
	{
		return get(AS_SITESEARCH);
	}
	public final SearchSettings siteUrl(final String theSiteUrl)
	{
		return set(AS_SITESEARCH, theSiteUrl);
	}
	
	public final String imageColor()
	{
		return get(IMGCOLOR);
	}
	public final SearchSettings imageColor(final String theImageColor)
	{
		return set(IMGCOLOR, theImageColor);
	}
	
	public String imageSize()
	{
		return get(IMGSZ);
	}
	public SearchSettings imageSize(final String theImageSize)
	{
		return set(IMGSZ, theImageSize);
	}
	
	public final String imageType()
	{
		return get(IMGTYPE);
	}
	public final SearchSettings imageType(final String theImageType)
	{
		return set(IMGTYPE, theImageType);
	}
	
	/**
	 * get a field from the settings.
	 * @param theFieldName not null
	 * @return the field value or null if not found
	 * @throws IllegalArgumentException if theFieldName is null
	 */
	private final String get(final String theFieldName)
	{
		if(null == theFieldName) throw new IllegalArgumentException();
		
		return settingsMap.get(theFieldName);
	}

	/**
	 * Set or clear the field.
	 * 
	 * @param theFieldName not null
	 * @param theFieldValue field value or null to clear the field
	 * @return this SearchSettings for call chaining purposes
	 * @throws IllegalArgumentException if theFieldName is null
	 */
	private final SearchSettings set(final String theFieldName, final String theFieldValue)
	{
		if(null == theFieldName) throw new IllegalArgumentException();
		
		if(null != theFieldValue)
		{
			settingsMap.put(theFieldName, theFieldValue);
		}
		else	// clear the field
		{
			settingsMap.remove(theFieldName);
		}
		return this;	// for call chaining
	}
	
	public void copyFrom(final SearchSettings that)
	{
		this.settingsMap.clear();
		this.settingsMap.putAll(that.settingsMap);
	}

}
