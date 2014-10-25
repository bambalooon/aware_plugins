/*
Copyright (c) 2013 AWARE Mobile Context Instrumentation Middleware/Framework
http://www.awareframework.com

AWARE is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the 
Free Software Foundation, either version 3 of the License, or (at your option) any later version (GPLv3+).

AWARE is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
See the GNU General Public License for more details: http://www.gnu.org/licenses/gpl.html
*/
package com.aware.plugin.google.fused_location;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.providers.Locations_Provider;
import com.aware.providers.Locations_Provider.Locations_Data;
import com.aware.utils.Aware_Sensor;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

/**
 * Fused location service for Aware framework
 * Requires Google Services API available on the device.
 * @author denzil
 */
public class Plugin extends Aware_Sensor implements ConnectionCallbacks, OnConnectionFailedListener {
    
    /**
     * Broadcasted event: new location available
     */
    public static final String ACTION_AWARE_LOCATIONS = "ACTION_AWARE_LOCATIONS";
    
    //holds accuracy and frequency parameters
    private LocationRequest mLocationRequest = null;
    private PendingIntent pIntent = null;
    private LocationClient mLocationClient = null;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        TAG = "AWARE::Google Fused Location";
        DEBUG = Aware.getSetting(getContentResolver(), Aware_Preferences.DEBUG_FLAG).equals("true");
        
        DATABASE_TABLES = Locations_Provider.DATABASE_TABLES;
        TABLES_FIELDS = Locations_Provider.TABLES_FIELDS;
        CONTEXT_URIS = new Uri[]{ Locations_Data.CONTENT_URI };
        
        CONTEXT_PRODUCER = new ContextProducer() {
            @Override
            public void onContext() {
                Intent context = new Intent( ACTION_AWARE_LOCATIONS );
                sendBroadcast(context);
            }
        };
        
        Aware.setSetting(getContentResolver(), Settings.STATUS_GOOGLE_FUSED_LOCATION, true);
        if( Aware.getSetting(getContentResolver(), Settings.FREQUENCY_GOOGLE_FUSED_LOCATION).length() == 0 ) {
            Aware.setSetting(getContentResolver(), Settings.FREQUENCY_GOOGLE_FUSED_LOCATION, Settings.update_interval);
        } else {
            Aware.setSetting(getContentResolver(), Settings.FREQUENCY_GOOGLE_FUSED_LOCATION, Aware.getSetting(getContentResolver(), Settings.FREQUENCY_GOOGLE_FUSED_LOCATION));
        }
        if( Aware.getSetting(getContentResolver(), Settings.MAX_FREQUENCY_GOOGLE_FUSED_LOCATION).length() == 0) {
            Aware.setSetting(getContentResolver(), Settings.MAX_FREQUENCY_GOOGLE_FUSED_LOCATION, Settings.max_update_interval);
        } else {
            Aware.setSetting(getContentResolver(), Settings.MAX_FREQUENCY_GOOGLE_FUSED_LOCATION, Aware.getSetting(getContentResolver(), Settings.MAX_FREQUENCY_GOOGLE_FUSED_LOCATION));
        }
        if( Aware.getSetting(getContentResolver(), Settings.ACCURACY_GOOGLE_FUSED_LOCATION).length()==0) {
            Aware.setSetting(getContentResolver(), Settings.ACCURACY_GOOGLE_FUSED_LOCATION, Settings.location_accuracy);
        } else {
            Aware.setSetting(getContentResolver(), Settings.ACCURACY_GOOGLE_FUSED_LOCATION, Aware.getSetting(getContentResolver(), Settings.ACCURACY_GOOGLE_FUSED_LOCATION));
        }
        
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(Integer.parseInt(Aware.getSetting(getContentResolver(), Settings.ACCURACY_GOOGLE_FUSED_LOCATION)));
        mLocationRequest.setInterval(Long.parseLong(Aware.getSetting(getContentResolver(), Settings.FREQUENCY_GOOGLE_FUSED_LOCATION)) * 1000);
        mLocationRequest.setFastestInterval(Long.parseLong(Aware.getSetting(getContentResolver(), Settings.MAX_FREQUENCY_GOOGLE_FUSED_LOCATION)) * 1000);
        
        Intent refresh = new Intent(Aware.ACTION_AWARE_REFRESH);
        sendBroadcast(refresh);
        
        if( ! is_google_services_available() ) {
            Log.e(TAG,"Google Services fused location not available on this device.");
            stopSelf();
        } else {
            mLocationClient = new LocationClient(this, this, this);
            
            Intent locationIntent = new Intent();
            locationIntent.setClassName("com.aware.plugin.google.fused_location", "com.aware.plugin.google.fused_location.Algorithm");
            pIntent = PendingIntent.getService(this, 0, locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mLocationClient.connect();
        }
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if( ! mLocationClient.isConnected() ) mLocationClient.connect();
        
        if( mLocationRequest.getPriority() != Integer.parseInt(Aware.getSetting(getContentResolver(), Settings.ACCURACY_GOOGLE_FUSED_LOCATION)) 
        || mLocationRequest.getInterval() !=  Long.parseLong(Aware.getSetting(getContentResolver(), Settings.FREQUENCY_GOOGLE_FUSED_LOCATION))
        || mLocationRequest.getFastestInterval() != Long.parseLong(Aware.getSetting(getContentResolver(), Settings.MAX_FREQUENCY_GOOGLE_FUSED_LOCATION))
        ) {
            mLocationRequest.setPriority(Integer.parseInt(Aware.getSetting(getContentResolver(), Settings.ACCURACY_GOOGLE_FUSED_LOCATION)));
            mLocationRequest.setInterval(Long.parseLong(Aware.getSetting(getContentResolver(), Settings.FREQUENCY_GOOGLE_FUSED_LOCATION)) * 1000);
            mLocationRequest.setFastestInterval(Long.parseLong(Aware.getSetting(getContentResolver(), Settings.MAX_FREQUENCY_GOOGLE_FUSED_LOCATION)) * 1000);
        }
        
        return super.onStartCommand(intent, flags, startId);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        if( mLocationClient != null ) {
            
            mLocationClient.removeLocationUpdates(pIntent);
            mLocationClient.unregisterConnectionCallbacks(this);
            mLocationClient.unregisterConnectionFailedListener(this);
            
            if( mLocationClient.isConnected() ) mLocationClient.disconnect();
        }
        
        Aware.setSetting(getContentResolver(), Settings.STATUS_GOOGLE_FUSED_LOCATION, false);
        
        Intent refresh = new Intent(Aware.ACTION_AWARE_REFRESH);
        sendBroadcast(refresh);
    }
    
    private boolean is_google_services_available() {
        if ( ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext()) ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connection_result ) {
        if( connection_result.hasResolution() ) {
            if( ! mLocationClient.isConnected() ) mLocationClient.connect();
        } else {
            Log.e(TAG,"Error connecting to Google Fused Location services, will try again in 5 minutes");
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        mLocationClient.requestLocationUpdates(mLocationRequest, pIntent);
    }

    @Override
    public void onDisconnected() {
        if( DEBUG ) {
            Log.d(TAG,"Google Fused Locations disconnected...");
        }
    }
}
