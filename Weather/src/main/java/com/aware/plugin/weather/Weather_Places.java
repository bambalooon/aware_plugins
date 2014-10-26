package com.aware.plugin.weather;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.providers.Locations_Provider.Locations_Data;

/**
 * An object of this class encapsulates the locations that are being used by a Weather_Connector object.
 * Currently only a single location is supported by this class (the last known), but in the future multiple additional places
 * of interest can be considered, which would need implementation of additional methods.
 * 
 * @author Christian Koehler
 * Last Update: 06/14/2013
 */

public class Weather_Places {

	private double[] latitudeList;
	private double[] longitudeList;
	
	private Context appContext;
	
	public Weather_Places(Context appContext) {
		this.appContext = appContext;		
	}
	
	public void checkLocations() {
		Cursor locationResult = appContext.getContentResolver().query(Locations_Data.CONTENT_URI, null, null, null, Locations_Data.TIMESTAMP + " DESC LIMIT 1");				
		
		double currentLatitude = 0;
		double currentLongitude = 0;
		long lastTimestamp = 0;

		if( locationResult != null && locationResult.moveToFirst()) {
			do {				
				currentLatitude = locationResult.getDouble(locationResult.getColumnIndex(Locations_Data.LATITUDE));
				currentLongitude = locationResult.getDouble(locationResult.getColumnIndex(Locations_Data.LONGITUDE));
				lastTimestamp = locationResult.getLong(locationResult.getColumnIndex(Locations_Data.TIMESTAMP));				
			}		
			while(locationResult.moveToNext());
		}
		if( locationResult != null && ! locationResult.isClosed() ) locationResult.close();
		
		Calendar rightNow = Calendar.getInstance();		
		
		if((rightNow.getTimeInMillis() - lastTimestamp)/60000 <= 30 ) {
			latitudeList = new double[1];
			longitudeList = new double[1];
			
			latitudeList[0] = currentLatitude;
			longitudeList[0] = currentLongitude;			
		}
		else {
			latitudeList = new double[1];
			longitudeList = new double[1];
			
			latitudeList[0] = currentLatitude;
			longitudeList[0] = currentLongitude;
		}
	}	
	
	public double[] getLatitudeList() {
		return latitudeList;
	}
	public void setLatitudeList(double[] latitudeList) {
		this.latitudeList = latitudeList;
	}
	public double[] getLongitudeList() {
		return longitudeList;
	}
	public void setLongitudeList(double[] longitudeList) {
		this.longitudeList = longitudeList;
	}
}
