package com.aware.plugin.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.plugin.weather.Weather_Provider.Weather14Day;
import com.aware.plugin.weather.Weather_Provider.Weather5Day;
import com.aware.plugin.weather.Weather_Provider.WeatherCurrent;
import com.aware.utils.Aware_Plugin;

import java.util.Timer;
import java.util.TimerTask;

/*
 *  TO-DO: Settings dialog to change retrieval frequency.
 */

/**
 * Plugin for the Weather Plugin
 *
 * @author Christian Koehler
 * @email: ckoehler@andrew.cmu.edu
 * @since: June 5th 2013
 */

public class Plugin extends Aware_Plugin {

    public static final String ACTION_AWARE_WEATHER = "ACTION_AWARE_WEATHER";

    public static final int MODE_CURRENT = 0;
    public static final int MODE_5DAY = 1;
    public static final int MODE_14DAY = 2;

    public static Weather_Connector WeatherCollector;
    private Weather_Places weatherPlaces;

    private Timer _currentTimer;
    private Timer _5DayTimer;
    private Timer _14DayTimer;

    // Retrieve the weather information at the current location. Retrieval every 30 minutes.

    private class Weather_Current_Query extends TimerTask {

        @Override
        public void run() {
            weatherPlaces.checkLocations();
            WeatherCollector.getWeatherCurrent(weatherPlaces.getLatitudeList(), weatherPlaces.getLongitudeList());
        }
    }

    private Weather_Current_Query weatherCurrentQuery = new Weather_Current_Query();

    private class Weather_5Day_Query extends TimerTask {

        @Override
        public void run() {
            weatherPlaces.checkLocations();
            WeatherCollector.getWeather5Day(weatherPlaces.getLatitudeList(), weatherPlaces.getLongitudeList());
        }
    }

    private Weather_5Day_Query weather5DayQuery = new Weather_5Day_Query();

    private class Weather_14Day_Query extends TimerTask {

        @Override
        public void run() {
            weatherPlaces.checkLocations();
            WeatherCollector.getWeather14Day(weatherPlaces.getLatitudeList(), weatherPlaces.getLongitudeList());
        }
    }

    private Weather_14Day_Query weather14DayQuery = new Weather_14Day_Query();

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            final String info;
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                info = networkInfo.isConnected()
                        ? networkInfo.getTypeName() + " connected!"
                        : "Connecting to " + networkInfo.getTypeName();
            }
            else {
                info = "Network disconnected!";
            }
            Toast.makeText(context, info, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        //Set plugin values on the framework
        TAG = "Weather";

        //Share the context back to the framework and other applications
        CONTEXT_PRODUCER = new Aware_Plugin.ContextProducer() {
            @Override
            public void onContext() {
                Intent notification = new Intent(ACTION_AWARE_WEATHER);
                sendBroadcast(notification);
            }
        };
        DATABASE_TABLES = Weather_Provider.DATABASE_TABLES;
        TABLES_FIELDS = Weather_Provider.TABLES_FIELDS;
        CONTEXT_URIS = new Uri[]{WeatherCurrent.CONTENT_URI, Weather5Day.CONTENT_URI, Weather14Day.CONTENT_URI};

        if (Aware.DEBUG) Log.e("Weather Plugin", "Plugin Started");

        String deviceID = Aware.getSetting(getContentResolver(), Aware_Preferences.DEVICE_ID);

        WeatherCollector = new Weather_Connector_OpenWeatherMap(true, true, true, this, deviceID);
        weatherPlaces = new Weather_Places(this);

        Aware.setSetting(getContentResolver(), Aware_Preferences.STATUS_LOCATION_NETWORK, true);
        Aware.setSetting(getContentResolver(), Aware_Preferences.FREQUENCY_NETWORK, 300);
        Aware.setSetting(getContentResolver(), Aware_Preferences.MIN_NETWORK_ACCURACY, 1500);
        Aware.setSetting(getContentResolver(), Aware_Preferences.EXPIRATION_TIME, 300);

        Intent applySettings = new Intent(Aware.ACTION_AWARE_REFRESH);
        sendBroadcast(applySettings);

        _currentTimer = new Timer();
        _currentTimer.schedule(weatherCurrentQuery, 0, 1800000);

        _5DayTimer = new Timer();
        _5DayTimer.schedule(weather5DayQuery, 60000, 3600000);

        _14DayTimer = new Timer();
        _14DayTimer.schedule(weather14DayQuery, 120000, 43200000);

        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        getApplicationContext().registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Aware.setSetting(getContentResolver(), Aware_Preferences.STATUS_LOCATION_NETWORK, false);

        Intent applySettings = new Intent(Aware.ACTION_AWARE_REFRESH);
        sendBroadcast(applySettings);

        this._currentTimer.cancel();
        this._5DayTimer.cancel();
        this._14DayTimer.cancel();
        getApplicationContext().unregisterReceiver(networkChangeReceiver);
    }

}
