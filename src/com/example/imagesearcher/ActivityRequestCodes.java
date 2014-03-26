package com.example.imagesearcher;

public enum ActivityRequestCodes
{
	SETTINGS_ACTIVITY;
	
	public int activityCode()
	{
		return this.ordinal() + 1;
	}
}
