package com.example.imagesearcher;

import com.loopj.android.image.SmartImageView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class ImageViewActivity extends Activity
{
	private SmartImageView imgFullImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_view);
		
		imgFullImage = (SmartImageView)findViewById(R.id.imgFullImage);
		
		final Intent i = getIntent();
		final ImageResult theImageInfo = (ImageResult)i.getSerializableExtra(ImageGridActivity.IMAGE_RESULTS_KEY);
		final int theIndex = i.getIntExtra(ImageGridActivity.IMAGE_POSITION_KEY, 0);
		
		imgFullImage.setImageUrl(theImageInfo.getFullUrl());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_view, menu);
		return true;
	}

}
