package com.aware.plugin.weather;

import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.plugin.weather.Weather_Provider.Weather14Day;
import com.aware.plugin.weather.Weather_Provider.Weather5Day;
import com.aware.plugin.weather.Weather_Provider.WeatherCurrent;
import com.aware.utils.Aware_Plugin;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.aware.plugin.weather.Settings.*;

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
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                final ContentResolver contentResolver = context.getContentResolver();
                final long now = new Date().getTime();
                if(isWeatherUpdateEnabled(contentResolver, ENABLED_WEATHER_CURRENT, DEFAULT_ENABLED_CURRENT)) {
                    int currFq = getWeatherUpdateFrequency
                            (contentResolver, FREQUENCY_WEATHER_CURRENT, DEFAULT_FREQUENCY_CURRENT);
                    if((now - WeatherCollector.getLastCurrentWeatherCall())/SECOND >= currFq) {
                        if(_currentTimer != null) {
                            _currentTimer.cancel();
                        }
                        _currentTimer = new Timer();
                        weatherCurrentQuery = new Weather_Current_Query();
                        _currentTimer.schedule(weatherCurrentQuery, 0, SECOND * currFq);
                    }
                }
                if(isWeatherUpdateEnabled(contentResolver, ENABLED_WEATHER_5DAYS, DEFAULT_ENABLED_5DAYS)) {
                    int _5dFq = getWeatherUpdateFrequency
                            (contentResolver, FREQUENCY_WEATHER_5DAYS, DEFAULT_FREQUENCY_5DAYS);
                    if((now - WeatherCollector.getLast5DaysWeatherCall())/SECOND >= _5dFq) {
                        if(_5DayTimer != null) {
                            _5DayTimer.cancel();
                        }
                        _5DayTimer = new Timer();
                        weather5DayQuery = new Weather_5Day_Query();
                        _5DayTimer.schedule(weather5DayQuery, 0, SECOND * _5dFq);
                    }
                }
                if(isWeatherUpdateEnabled(contentResolver, ENABLED_WEATHER_14DAYS, DEFAULT_ENABLED_14DAYS)) {
                    int _14dFq = getWeatherUpdateFrequency
                            (contentResolver, FREQUENCY_WEATHER_14DAYS, DEFAULT_FREQUENCY_14DAYS);
                    if((now - WeatherCollector.getLast14DaysWeatherCall())/SECOND >= _14dFq) {
                        if(_14DayTimer != null) {
                            _14DayTimer.cancel();
                        }
                        _14DayTimer = new Timer();
                        weather14DayQuery = new Weather_14Day_Query();
                        _14DayTimer.schedule(weather14DayQuery, 0, SECOND * _14dFq);
                    }
                }
            }
        }
    };

    private BroadcastReceiver weatherSettingsChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startWeatherUpdateTimers();
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

        final ContentResolver contentResolver = getContentResolver();
        String deviceID = Aware.getSetting(contentResolver, Aware_Preferences.DEVICE_ID);

        WeatherCollector = new Weather_Connector_OpenWeatherMap(true, true, true, this, deviceID);
        weatherPlaces = new Weather_Places(this);

        Aware.setSetting(contentResolver, Aware_Preferences.STATUS_LOCATION_NETWORK, true);
        Aware.setSetting(contentResolver, Aware_Preferences.FREQUENCY_NETWORK, 300);
        Aware.setSetting(contentResolver, Aware_Preferences.MIN_NETWORK_ACCURACY, 1500);
        Aware.setSetting(contentResolver, Aware_Preferences.EXPIRATION_TIME, 300);

        Intent applySettings = new Intent(Aware.ACTION_AWARE_REFRESH);
        sendBroadcast(applySettings);

        startWeatherUpdateTimers();

        IntentFilter networkChangeIntent = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        getApplicationContext().registerReceiver(networkChangeReceiver, networkChangeIntent);
        IntentFilter settingsChangeIntent = new IntentFilter(ACTION_WEATHER_REFRESH);
        getApplicationContext().registerReceiver(weatherSettingsChangeReceiver, settingsChangeIntent);
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
        getApplicationContext().unregisterReceiver(weatherSettingsChangeReceiver);
    }

    private void startWeatherUpdateTimers() {
        ContentResolver contentResolver = getContentResolver();
        if(isWeatherUpdateEnabled(contentResolver, ENABLED_WEATHER_CURRENT, DEFAULT_ENABLED_CURRENT)) {
            final int currWthFq =
                    getWeatherUpdateFrequency(contentResolver, FREQUENCY_WEATHER_CURRENT, DEFAULT_FREQUENCY_CURRENT);
            if(_currentTimer != null) {
                _currentTimer.cancel();
            }
            _currentTimer = new Timer();
            weatherCurrentQuery = new Weather_Current_Query();
            _currentTimer.schedule(weatherCurrentQuery, 0, SECOND * currWthFq);
        }

        if(isWeatherUpdateEnabled(contentResolver, ENABLED_WEATHER_5DAYS, DEFAULT_ENABLED_5DAYS)) {
            final int _5daysWthFq =
                    getWeatherUpdateFrequency(contentResolver, FREQUENCY_WEATHER_5DAYS, DEFAULT_FREQUENCY_5DAYS);
            if(_5DayTimer != null) {
                _5DayTimer.cancel();
            }
            _5DayTimer = new Timer();
            weather5DayQuery = new Weather_5Day_Query();
            _5DayTimer.schedule(weather5DayQuery, 0, SECOND * _5daysWthFq);
        }

        if(isWeatherUpdateEnabled(contentResolver, ENABLED_WEATHER_14DAYS, DEFAULT_ENABLED_14DAYS)) {
            final int _14daysWthFq =
                    getWeatherUpdateFrequency(contentResolver, FREQUENCY_WEATHER_14DAYS, DEFAULT_FREQUENCY_14DAYS);
            if(_14DayTimer != null) {
                _14DayTimer.cancel();
            }
            _14DayTimer = new Timer();
            weather14DayQuery = new Weather_14Day_Query();
            _14DayTimer.schedule(weather14DayQuery, 0, SECOND * _14daysWthFq);
        }
    }

    private boolean isWeatherUpdateEnabled(ContentResolver contentResolver,
                                           String enabledWeatherSetting,
                                           boolean defaultWeatherSetting) {

        final String wthEnabledText = Aware.getSetting(contentResolver, enabledWeatherSetting);
        return wthEnabledText.length() == 0
                ? defaultWeatherSetting
                : TRUE_STATEMENT.equalsIgnoreCase(wthEnabledText);
    }

    private int getWeatherUpdateFrequency(ContentResolver contentResolver,
                                          String frequencyWeatherSetting,
                                          int defaultFrequencySetting) {

        final String currWthFqText = Aware.getSetting(contentResolver, frequencyWeatherSetting);
        return currWthFqText.length() == 0
                ? defaultFrequencySetting
                : Integer.parseInt(currWthFqText);
    }

}
