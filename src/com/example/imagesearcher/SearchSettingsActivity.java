package com.example.imagesearcher;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SearchSettingsActivity extends Activity implements OnItemSelectedListener 
{
	private SearchSettings searchSettings;
	private EditText editSiteUrl;
	private Spinner spinColorFilter;
	private RadioGroup toggleSizeGroup;
	private RadioGroup toggleTypeGroup;
		
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_settings);
		
		final Intent i = getIntent();
		searchSettings = (SearchSettings)i.getSerializableExtra(ImageGridActivity.SEARCH_SETTINGS_KEY);

		//
		// set the settings into the view
		//
		bindView();
	}
	
	/**
	 * bind the data and listeners to the view
	 */
	private void bindView()
	{
		// select the toggle button that matches the size setting
		toggleSizeGroup = (RadioGroup)findViewById(R.id.toggleSizeGroup);
		checkToggleSetting(toggleSizeGroup, searchSettings.imageSize());

		// select the toggle button that matches the size setting
		toggleTypeGroup = (RadioGroup)findViewById(R.id.toggleTypeGroup);
		checkToggleSetting(toggleTypeGroup, searchSettings.imageType());

		//
		// setup the colors spinner.
		// 1. get the spinner view
		// 2. Create an ArrayAdapter using the string array and a default spinner layout
		// 3. Specify the layout to use when the list of choices appears
		// 4. Apply the adapter to the spinner
		//
		spinColorFilter = (Spinner) findViewById(R.id.spinColorFilter); 				// 1
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.image_colors, R.layout.text_view_right_aligned); 			// 2
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 3
		spinColorFilter.setAdapter(adapter);
		spinColorFilter.setOnItemSelectedListener(this);// 4
		
		// set color selection
		@SuppressWarnings("rawtypes")
		final ArrayAdapter theArrayAdapter = (ArrayAdapter) spinColorFilter.getAdapter();
		@SuppressWarnings("unchecked")
		final int thePosition = theArrayAdapter.getPosition(searchSettings.imageColor());
		spinColorFilter.setSelection((thePosition > 0) ? thePosition : 0);
		

		//
		// setup site url text edit
		//
		editSiteUrl = (EditText)findViewById(R.id.editSiteFilter);
		if(null != searchSettings.siteUrl())
		{
			editSiteUrl.setText(searchSettings.siteUrl());
		}
		editSiteUrl.addTextChangedListener(new TextWatcher()
		{
			@Override
			public final void afterTextChanged(final Editable s)
			{
				searchSettings.siteUrl(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				// do nothing
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				// do nothing
			}
			
		});
	}
	
	private void checkToggleSetting(final RadioGroup theRadioGroup, final String theSetting)
	{
		for(int i = 0; i < theRadioGroup.getChildCount(); i += 1)
		{
			final ToggleButton theView = (ToggleButton)theRadioGroup.getChildAt(i);
			theView.setChecked((null != theSetting) && theSetting.equals(theView.getTag()));
		}
	}


//	@Override
//	public boolean onCreateOptionsMenu(final Menu menu)
//	{
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.search_settings, menu);
//		return true;
//	}

	/**
	 * Called when a size button is toggled.
	 * 
	 * @param theView
	 */
	public void onToggleSize(final View theView)
	{
		searchSettings.imageSize(toggleSetting(theView));
	}
	
	public void onToggleType(final View theView)
	{
		searchSettings.imageType(toggleSetting(theView));
	}
	
	private String toggleSetting(final View theView)
	{
		// update the settings
		final ToggleButton theToggledButton = (ToggleButton)theView;
		final RadioGroup theRadioGroup = (RadioGroup)theToggledButton.getParent();
		final String theSettingValue = 
				theToggledButton.isChecked() 
					? theToggledButton.getTag().toString() 
					: null;
		
		// DEBUG: show a toast to show the change
		Toast.makeText(
				this, 
				"Toggled {button} is {onOff}"
					.replace("{button}", theToggledButton.getTag().toString())
					.replace("{onOff}", theToggledButton.isChecked() ? "on" : "Off"), 
				Toast.LENGTH_SHORT).show();
		
		//
		// make sure others get pushed up
		//
		final int theButtonCount = theRadioGroup.getChildCount();
		for (int j = 0; j < theButtonCount; j += 1)
		{
			final ToggleButton theOtherButton = (ToggleButton) theRadioGroup.getChildAt(j);
			if(theOtherButton != theToggledButton)
			{
				theOtherButton.setChecked(false);
			}
		}
		
		return theSettingValue;
	}

    public void onItemSelected(final AdapterView<?> parent, final View view, final int pos, final long id) 
    {
    	if(parent.getId() == R.id.spinColorFilter)
    	{
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
    		if(pos > 0)	
    		{
	    		final String theSettingValue = parent.getItemAtPosition(pos).toString();
	    		
	    		searchSettings.imageColor(theSettingValue.toLowerCase(Locale.US));
    		}
    		else	// position 0 is no filtering
    		{
        		searchSettings.imageColor(null);
    		}
    	}
    }

    public void onNothingSelected(AdapterView<?> parent) 
    {
    	if(parent.getId() == R.id.spinColorFilter)
    	{
    		searchSettings.imageColor(null);
    	}
    }
    
    public void onSaveButton(View theView)
    {
    	// send updated settings back to caller.
		Intent data = new Intent();
		data.putExtra(ImageGridActivity.SEARCH_SETTINGS_KEY, searchSettings);
		setResult(RESULT_OK, data); // set result code and bundle data for response
		finish(); // closes the activity, pass data to parent
    }

}
