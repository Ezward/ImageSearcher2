package com.example.imagesearcher;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.image.SmartImageView;

/**
 * @author Ed
 * 
 * ArrayAdapter to adapt array of ImageResult into image views.
 *
 */
public class ImageResultArrayAdapter extends android.widget.ArrayAdapter<ImageResult>
{
	public ImageResultArrayAdapter(Context context, List<ImageResult> objects)
	{
		// use our grid image layout for grid items
		super(context, R.layout.grid_item_image, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final ImageResult theImageResult = this.getItem(position);
		SmartImageView theSmartImageView;
		
		if(null == convertView)
		{
			// create a new view
			LayoutInflater theLayoutInflater = LayoutInflater.from(getContext());
			theSmartImageView = (SmartImageView)theLayoutInflater.inflate(R.layout.grid_item_image, parent, false);

			// set url and return it.  The image is loaded asynchronously
			theSmartImageView.setImageUrl(theImageResult.getThumbUrl());
			theSmartImageView.setTag(theImageResult.getThumbUrl());	// remember the url
		}
		else	// use the pre-existing view
		{
			theSmartImageView = (SmartImageView)convertView;
			
			// if this url is different, reload the image
			if(!theImageResult.getThumbUrl().equals(theSmartImageView.getTag()))
			{
				theSmartImageView.setImageResource(android.R.color.darker_gray);	// make the image transparent

				// set url and return it.  The image is loaded asynchronously
				theSmartImageView.setImageUrl(theImageResult.getThumbUrl());
				theSmartImageView.setTag(theImageResult.getThumbUrl());	// remember the url
			}
		}
		return theSmartImageView;
	}
	
	

}
