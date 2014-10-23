/**
@author: denzil
 */
package com.aware.plugin.google.activityrecognition;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.plugin.google.activityrecognition.Google_AR_Provider.Google_Activity_Recognition_Data;
import com.aware.utils.Aware_Sensor;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;

public class Plugin extends Aware_Sensor implements ConnectionCallbacks, OnConnectionFailedListener {

	public static String ACTION_AWARE_GOOGLE_ACTIVITY_RECOGNITION = "ACTION_AWARE_GOOGLE_ACTIVITY_RECOGNITION";
	public static String EXTRA_ACTIVITY = "activity";
	public static String EXTRA_CONFIDENCE = "confidence";

	private PendingIntent gARPending = null;
	private ActivityRecognitionClient gARClient = null;

	public static int current_activity = -1;
	public static int current_confidence = -1;

	@Override
	public void onCreate() {
		super.onCreate();

		TAG = "AWARE::Google Activity Recognition";
		DEBUG = Aware.getSetting(getContentResolver(),
				Aware_Preferences.DEBUG_FLAG).equals("true");

		DATABASE_TABLES = Google_AR_Provider.DATABASE_TABLES;
		TABLES_FIELDS = Google_AR_Provider.TABLES_FIELDS;
		CONTEXT_URIS = new Uri[]{ Google_Activity_Recognition_Data.CONTENT_URI };
		
		CONTEXT_PRODUCER = new ContextProducer() {
			@Override
			public void onContext() {
				Intent context = new Intent(ACTION_AWARE_GOOGLE_ACTIVITY_RECOGNITION);
				context.putExtra(EXTRA_ACTIVITY, current_activity);
				context.putExtra(EXTRA_CONFIDENCE, current_confidence);
				sendBroadcast(context);
			}
		};

		Aware.setSetting(getContentResolver(), Settings.STATUS_GOOGLE_ACTIVITY_RECOGNITION, true);
		if( Aware.getSetting(getContentResolver(), Settings.FREQUENCY_GOOGLE_ACTIVITY_RECOGNITION).length()==0 ) {
			Aware.setSetting(getContentResolver(), Settings.FREQUENCY_GOOGLE_ACTIVITY_RECOGNITION, Settings.frequency);
		}else{
			Aware.setSetting(getContentResolver(), Settings.FREQUENCY_GOOGLE_ACTIVITY_RECOGNITION, Aware.getSetting(getContentResolver(), Settings.FREQUENCY_GOOGLE_ACTIVITY_RECOGNITION));
		}
	
		Intent refresh = new Intent(Aware.ACTION_AWARE_REFRESH);
		sendBroadcast(refresh);
	
		if ( ! is_google_services_available() ) {
			Log.e(TAG,"Google Services activity recognition not available on this device.");
			stopSelf();
		} else {
			gARClient = new ActivityRecognitionClient(this, this, this);
			Intent gARIntent = new Intent();
			gARIntent.setClassName("com.aware.plugin.google.activityrecognition", "com.aware.plugin.google.activityrecognition.Algorithm");
			gARPending = PendingIntent.getService(this, 0, gARIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			if( ! gARClient.isConnected() && ! gARClient.isConnecting() ) gARClient.connect();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if( ! gARClient.isConnected() && ! gARClient.isConnecting() ) gARClient.connect();
		if( gARClient.isConnected() ) {
			gARClient.requestActivityUpdates(Long.parseLong(Aware.getSetting(getContentResolver(),Settings.FREQUENCY_GOOGLE_ACTIVITY_RECOGNITION)) * 1000, gARPending);
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		//we might get here if phone doesn't support Google Services
		if ( gARClient != null ) {
			gARClient.removeActivityUpdates(gARPending);
			if( gARClient.isConnected() ) gARClient.disconnect();
		}

		Aware.setSetting(getContentResolver(), Settings.STATUS_GOOGLE_ACTIVITY_RECOGNITION, false);
	}

	private boolean is_google_services_available() {
		if ( ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext()) ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connection_result) {
		if( connection_result.hasResolution() ) {
			if( ! gARClient.isConnected() || ! gARClient.isConnecting() ) gARClient.connect();
		} else {
			if( DEBUG ) Log.w(TAG,"Error connecting to Google's activity recognition services, will try again in 5 minutes");
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		gARClient.requestActivityUpdates(Long.parseLong(Aware.getSetting(getContentResolver(),Settings.FREQUENCY_GOOGLE_ACTIVITY_RECOGNITION)) * 1000, gARPending);
	}

	@Override
	public void onDisconnected() {
		//Get the connection back
		if( ! gARClient.isConnected() && ! gARClient.isConnecting() ) gARClient.connect();
	}
}
