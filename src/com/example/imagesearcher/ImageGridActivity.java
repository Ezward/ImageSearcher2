package com.example.imagesearcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * @author Ed
 * 
 *         Adapted from original by Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * 
 */
public class ImageGridActivity extends Activity
{
	public static final String				GOOGLE_SEARCH_TEMPLATE	= "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz={COUNT}&q={QUERY}";
	public static final String				START_PARAMETER			= "&start=";
	public static final String				QUERY_FIELD				= "{QUERY}";
	public static final String				START_FIELD				= "{START}";
	public static final String				COUNT_FIELD				= "{COUNT}";

	public static final String				IMAGE_RESULTS_KEY		= "com.lumpofcode.imagesearcher.ImageGridActivity.IMAGE_RESULTS_KEY";
	public static final String				IMAGE_POSITION_KEY		= "com.lumpofcode.imagesearcher.ImageGridActivity.IMAGE_POSITION_KEY";
	public static final String				SEARCH_SETTINGS_KEY		= "com.lumpofcode.imagesearcher.ImageGridActivity.SEARCH_SETTINGS_KEY";

	public static final int 				IMAGE_PAGE_SIZE			= 8;
	private String searchQuery;
	private final SearchSettings			searchSettings			= new SearchSettings();
	private final ArrayList<ImageResult>	searchResults			= new ArrayList<ImageResult>();
	private ImageResultArrayAdapter			searchResultsArray;
	private GridView						grdImageResults;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (null != savedInstanceState)
		{
			final SearchSettings theSettings = (SearchSettings) savedInstanceState.getSerializable(SEARCH_SETTINGS_KEY);
			searchSettings.copyFrom(theSettings);
		}
		else
		{
			readSettings();
		}

		setContentView(R.layout.activity_image_grid);	// our image grid
		searchResultsArray = new ImageResultArrayAdapter(this, searchResults);

		// get the grid view and set item click listener
		grdImageResults = (GridView) findViewById(R.id.grdImageResults);
		grdImageResults.setAdapter(searchResultsArray);	// adapter for grid item's view
		grdImageResults.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				startImageViewActivity(position);
			}
		});
		grdImageResults.setOnScrollListener(new EndlessScrollListener(IMAGE_PAGE_SIZE)
		{
			@Override
			public void onLoadMore(int page, int totalItemsCount)
			{
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your AdapterView
				loadImagesStartingAt(totalItemsCount);
				// or customLoadMoreDataFromApi(totalItemsCount);
			}
		});

		handleIntent(getIntent());	// fire off a search
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

		// Associate searchable configuration with the SearchView
		final SearchManager theSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		final SearchView theSearchView = (SearchView) menu.findItem(R.id.search_action).getActionView();
		theSearchView.setSearchableInfo(theSearchManager.getSearchableInfo(getComponentName()));

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putSerializable(SEARCH_SETTINGS_KEY, searchSettings);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		handleIntent(intent);
	}

	public void onSettingsAction(MenuItem item)
	{
		Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
		startSearchSettingsActivity();
	}

	private void handleIntent(Intent intent)
	{
		if (Intent.ACTION_SEARCH.equals(intent.getAction()))
		{
			final String theQuery = intent.getStringExtra(SearchManager.QUERY);
			searchSettings.query(theQuery);
			onImageSearch();
			writeSettings();	// write the settings while the query runs
		}
	}

	private void startSearchSettingsActivity()
	{
		Intent intent = new Intent(this, SearchSettingsActivity.class);
		intent.putExtra(SEARCH_SETTINGS_KEY, searchSettings);
		startActivityForResult(intent, ActivityRequestCodes.SETTINGS_ACTIVITY.activityCode());
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{
		if (ActivityRequestCodes.SETTINGS_ACTIVITY.activityCode() == requestCode)
		{
			if (RESULT_OK == resultCode)
			{
				this.searchSettings.copyFrom((SearchSettings) data.getSerializableExtra(SEARCH_SETTINGS_KEY));
				onImageSearch();	// run the search
				writeSettings();	// write the settings while the query runs
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Start the activity to view the image at the given position.
	 * 
	 * @param position
	 *            index into searchResults array of image info.
	 */
	private void startImageViewActivity(int position)
	{
		// TODO: save searchResults to storage and pass position rather than ImageResult
		Intent intent = new Intent(this, ImageViewActivity.class);
		intent.putExtra(IMAGE_RESULTS_KEY, searchResults.get(position));
		intent.putExtra(IMAGE_POSITION_KEY, position);	// not used yet
		startActivity(intent);
	}

	/**
	 * Fire an asynchronouse call to Google Search API using the given string as the query parameter's value.
	 * 
	 * @param theQueryField
	 *            the query parameter value
	 */
	public void onImageSearch()
	{
		// first page, so we are creating a new array, clear the old one
		searchResultsArray.clear();

		//
		// create minimal query
		//
		String theSearchUrl = GOOGLE_SEARCH_TEMPLATE
				.replace(QUERY_FIELD, Uri.encode(searchSettings.query()))
				.replace(COUNT_FIELD, Uri.encode(Integer.toString(IMAGE_PAGE_SIZE)));

		//
		// now apply search settings
		//
		Iterator<Map.Entry<String, String>> theSettingsIterator = searchSettings.iterator();
		while (theSettingsIterator.hasNext())
		{
			final Map.Entry<String, String> theEntry = theSettingsIterator.next();

			theSearchUrl += "&" + theEntry.getKey() + "=" + theEntry.getValue();
		}
		
		searchQuery = theSearchUrl;	// save the formatted query

		loadImagesStartingAt(0);
		return;
		
	}

	private static final String	SEARCH_SETTINGS_FILE_NAME	= "search_settings.dat";

	private void readSettings()
	{
		ObjectInputStream theStream = null;

		try
		{
			theStream = new ObjectInputStream(openFileInput(SEARCH_SETTINGS_FILE_NAME));
			SearchSettings theSettings = (SearchSettings) theStream.readObject();
			searchSettings.copyFrom(theSettings);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (null != theStream)
			{
				try
				{
					theStream.close();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private void writeSettings()
	{
		ObjectOutputStream theObjectOutputStream = null;

		try
		{
			theObjectOutputStream = new ObjectOutputStream(openFileOutput(SEARCH_SETTINGS_FILE_NAME,
					Context.MODE_PRIVATE));
			theObjectOutputStream.writeObject(searchSettings);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (null != theObjectOutputStream)
			{
				try
				{
					theObjectOutputStream.close();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private void deleteSettings()
	{
		File theFile = new File(this.getFilesDir(), SEARCH_SETTINGS_FILE_NAME);
		if (theFile.exists()) theFile.delete();
	}
	
	private void loadImagesStartingAt(final int theStartingOffset)
	{
		//
		// create minimal query
		//
		String theSearchUrl = searchQuery + START_PARAMETER + Integer.toString(theStartingOffset);

		//
		// fill the searchResults with the query results
		//
		final AsyncHttpClient theHttpClient = new AsyncHttpClient();
		theHttpClient.get(theSearchUrl, new JsonHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				JSONArray theJsonArray = null;

				//
				// parse the json response from google search api
				//
				if (null != response)
				{
					try
					{
						JSONObject theResponseData = response.getJSONObject("responseData");
						if (null != theResponseData)
						{
							theJsonArray = theResponseData.getJSONArray("results");
						}
					}
					catch (JSONException e)
					{
						// do nothing
					}
				}

				// clear current results and add new results.
				// this will cause the grid to redraw each item in the grid.
				if ((null != theJsonArray) && (theJsonArray.length() > 0))
				{
					searchResultsArray.addAll(ImageResult.fromJSONArray(theJsonArray));
				}
			}
		});

	}

}
