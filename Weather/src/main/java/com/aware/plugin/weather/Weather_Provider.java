package com.aware.plugin.weather;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;

import com.aware.Aware;
import com.aware.utils.DatabaseHelper;

/**
 * ContentProvider for the Weather Plugin
 * @author Christian Koehler
 * @email: ckoehler@andrew.cmu.edu
 * @since: June 5th 2013
 */

public class Weather_Provider extends ContentProvider {

        public static final String AUTHORITY = "com.aware.provider.plugin.weather";
        
        private static final int DATABASE_VERSION = 5;
        
        private static final int WEATHER = 1;
        private static final int WEATHER_ID = 2;
        private static final int WEATHER_5DAY = 3;
        private static final int WEATHER_5DAY_ID = 4;
        private static final int WEATHER_14DAY = 5;
        private static final int WEATHER_14DAY_ID = 6;
        
        private static UriMatcher uriMatcher = null;
        private static HashMap<String, String> weatherMap = null;
        private static HashMap<String, String> weather5DayMap = null;
        private static HashMap<String, String> weather14DayMap = null;
        private static DatabaseHelper databaseHelper = null;
        private static SQLiteDatabase database = null;        
        
        /* 
         * 	All the weather data is returned by the openweathermap.org API. 
         * 
         * 	WeatherCurrent 	-> The current weather conditions at the location.
         * 	Weather5Day 	-> The weather over the next 5 days in 3 hr intervals.
         * 	Weather14Day 	-> The weather over the next 14 days
         */
        
        public static final class WeatherCurrent implements BaseColumns {
            private WeatherCurrent() {};
            
            public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/plugin_weather_current"); //this needs to match the table name
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.plugin.weather";
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.plugin.weather";
            
            public static final String _ID = "_id";
            public static final String REQUEST_TIMESTAMP = "timestamp";
            public static final String FORECAST_TIMESTAMP = "double_forecast_timestamp";
            public static final String DEVICE_ID = "device_id";
            public static final String LATITUDE = "double_latitude";
            public static final String LONGITUDE = "double_longitude";
            public static final String TEMPERATURE_VALUE_CURRENT = "temperature_value_current";
            public static final String TEMPERATURE_VALUE_MIN = "temperature_value_min";
            public static final String TEMPERATURE_VALUE_MAX = "temperature_value_max";
            public static final String TEMPERATURE_VALUE_NIGHT = "temperature_value_night";
            public static final String TEMPERATURE_VALUE_DAY = "temperature_value_day";
            public static final String TEMPERATURE_VALUE_EVENING = "temperature_value_evening";
            public static final String TEMPERATURE_VALUE_MORNING = "temperature_value_morning";
            public static final String HUMIDITY = "humidity";
            public static final String PRESSURE = "pressure";
            public static final String WIND_SPEED = "wind_speed";
            public static final String WIND_GUST = "wind_speed_gust";
            public static final String WIND_ANGLE = "wind_angle";                
            public static final String CLOUDS_VALUE = "clouds_value";                
            public static final String RAIN = "rain";                
            public static final String WEATHER_NAME = "weather_name";
            public static final String WEATHER_PROVIDER = "weather_provider";
            public static final String SUNRISE = "sunrise";
            public static final String SUNSET = "sunset";
            public static final String LOCATION_NAME = "location_name";
            public static final String DATE_TIME = "date_time";
                
        }
        
        public static final class Weather5Day implements BaseColumns {
            private Weather5Day() {};
            
            public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/plugin_weather_5day"); //this needs to match the table name
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.plugin.weather.5day";
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.plugin.weather.5day";
            
            public static final String _ID = "_id";
            public static final String REQUEST_TIMESTAMP = "timestamp";
            public static final String FORECAST_TIMESTAMP = "double_forecast_timestamp";
            public static final String DEVICE_ID = "device_id";
            public static final String LATITUDE = "double_latitude";
            public static final String LONGITUDE = "double_longitude";
            public static final String TEMPERATURE_VALUE_CURRENT = "temperature_value_current";
            public static final String TEMPERATURE_VALUE_MIN = "temperature_value_min";
            public static final String TEMPERATURE_VALUE_MAX = "temperature_value_max";
            public static final String TEMPERATURE_VALUE_NIGHT = "temperature_value_night";
            public static final String TEMPERATURE_VALUE_DAY = "temperature_value_day";
            public static final String TEMPERATURE_VALUE_EVENING = "temperature_value_evening";
            public static final String TEMPERATURE_VALUE_MORNING = "temperature_value_morning";
            public static final String HUMIDITY = "humidity";
            public static final String PRESSURE = "pressure";
            public static final String WIND_SPEED = "wind_speed";
            public static final String WIND_GUST = "wind_speed_gust";
            public static final String WIND_ANGLE = "wind_angle";                
            public static final String CLOUDS_VALUE = "clouds_value";                
            public static final String RAIN = "rain";                
            public static final String WEATHER_NAME = "weather_name";
            public static final String WEATHER_PROVIDER = "weather_provider";
            public static final String SUNRISE = "sunrise";
            public static final String SUNSET = "sunset";
            public static final String LOCATION_NAME = "location_name";
            public static final String DATE_TIME = "date_time";
        }
        
        public static final class Weather14Day implements BaseColumns {
            private Weather14Day() {};
            
            public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/plugin_weather_14day"); //this needs to match the table name
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.plugin.weather.14day";
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.plugin.weather.14day";
            
            public static final String _ID = "_id";     
            public static final String REQUEST_TIMESTAMP = "timestamp";
            public static final String FORECAST_TIMESTAMP = "double_forecast_timestamp";
            public static final String DEVICE_ID = "device_id";
            public static final String LATITUDE = "double_latitude";
            public static final String LONGITUDE = "double_longitude";
            public static final String TEMPERATURE_VALUE_CURRENT = "temperature_value_current";
            public static final String TEMPERATURE_VALUE_MIN = "temperature_value_min";
            public static final String TEMPERATURE_VALUE_MAX = "temperature_value_max";
            public static final String TEMPERATURE_VALUE_NIGHT = "temperature_value_night";
            public static final String TEMPERATURE_VALUE_DAY = "temperature_value_day";
            public static final String TEMPERATURE_VALUE_EVENING = "temperature_value_evening";
            public static final String TEMPERATURE_VALUE_MORNING = "temperature_value_morning";
            public static final String HUMIDITY = "humidity";
            public static final String PRESSURE = "pressure";
            public static final String WIND_SPEED = "wind_speed";
            public static final String WIND_GUST = "wind_speed_gust";
            public static final String WIND_ANGLE = "wind_angle";                
            public static final String CLOUDS_VALUE = "clouds_value";                
            public static final String RAIN = "rain";                
            public static final String WEATHER_NAME = "weather_name";
            public static final String WEATHER_PROVIDER = "weather_provider";
            public static final String SUNRISE = "sunrise";
            public static final String SUNSET = "sunset";
            public static final String LOCATION_NAME = "location_name";
            public static final String DATE_TIME = "date_time";
        }
        
        public static String DATABASE_NAME = Environment.getExternalStorageDirectory() + "/AWARE/plugin_weather.db";
        
        public static final String[] DATABASE_TABLES = {
                "plugin_weather_current",
                "plugin_weather_5day",
                "plugin_weather_14day"
        };
        
        public static final String[] TABLES_FIELDS = {
        		WeatherCurrent._ID + " integer primary key autoincrement," +
        		WeatherCurrent.FORECAST_TIMESTAMP + " real default 0," +
        		WeatherCurrent.REQUEST_TIMESTAMP + " real default 0," +
        		WeatherCurrent.DEVICE_ID + " text default ''," +
        		WeatherCurrent.LATITUDE + " real default 0," +
        		WeatherCurrent.LONGITUDE + " real default 0," +
        		WeatherCurrent.TEMPERATURE_VALUE_CURRENT + " real default 0," +
        		WeatherCurrent.TEMPERATURE_VALUE_MIN + " real default 0," +
        		WeatherCurrent.TEMPERATURE_VALUE_MAX + " real default 0," +
        		WeatherCurrent.TEMPERATURE_VALUE_NIGHT + " real default 0," +
        		WeatherCurrent.TEMPERATURE_VALUE_DAY + " real default 0," +
        		WeatherCurrent.TEMPERATURE_VALUE_EVENING + " real default 0," +
        		WeatherCurrent.TEMPERATURE_VALUE_MORNING + " real default 0," +
				WeatherCurrent.HUMIDITY + " real default 0," +
				WeatherCurrent.PRESSURE + " real default 0," +
				WeatherCurrent.WIND_SPEED + " real default 0," +	
				WeatherCurrent.WIND_GUST + " real default 0," +
				WeatherCurrent.WIND_ANGLE + " real default 0," +
				WeatherCurrent.CLOUDS_VALUE + " real default 0," +				
				WeatherCurrent.RAIN + " real default 0," +
				WeatherCurrent.WEATHER_NAME + " text default ''," +
				WeatherCurrent.WEATHER_PROVIDER + " text default ''," +
				WeatherCurrent.SUNRISE + " real default 0," +
				WeatherCurrent.SUNSET + " real default 0," +
				WeatherCurrent.LOCATION_NAME + " text default ''," +
				WeatherCurrent.DATE_TIME + " text default ''," +
                "UNIQUE (" + WeatherCurrent._ID+","+ WeatherCurrent.DEVICE_ID + ")",
                
                Weather5Day._ID + " integer primary key autoincrement," +
        		Weather5Day.FORECAST_TIMESTAMP + " real default 0," +
        		Weather5Day.REQUEST_TIMESTAMP + " real default 0," +
        		Weather5Day.DEVICE_ID + " text default ''," +
        		Weather5Day.LATITUDE + " real default 0," +
        		Weather5Day.LONGITUDE + " real default 0," +
        		Weather5Day.TEMPERATURE_VALUE_CURRENT + " real default 0," +
        		Weather5Day.TEMPERATURE_VALUE_MIN + " real default 0," +
        		Weather5Day.TEMPERATURE_VALUE_MAX + " real default 0," +
        		Weather5Day.TEMPERATURE_VALUE_NIGHT + " real default 0," +
        		Weather5Day.TEMPERATURE_VALUE_DAY + " real default 0," +
        		Weather5Day.TEMPERATURE_VALUE_EVENING + " real default 0," +
        		Weather5Day.TEMPERATURE_VALUE_MORNING + " real default 0," +
				Weather5Day.HUMIDITY + " real default 0," +
				Weather5Day.PRESSURE + " real default 0," +
				Weather5Day.WIND_SPEED + " real default 0," +	
				Weather5Day.WIND_GUST + " real default 0," +
				Weather5Day.WIND_ANGLE + " real default 0," +
				Weather5Day.CLOUDS_VALUE + " real default 0," +				
				Weather5Day.RAIN + " real default 0," +
				Weather5Day.WEATHER_NAME + " text default ''," +
				Weather5Day.WEATHER_PROVIDER + " text default ''," +
				Weather5Day.SUNRISE + " real default 0," +
				Weather5Day.SUNSET + " real default 0," +
				Weather5Day.LOCATION_NAME + " text default ''," +
				Weather5Day.DATE_TIME + " text default ''," +
				"UNIQUE (" + Weather5Day._ID+","+ Weather5Day.DEVICE_ID + ")",
                
                Weather14Day._ID + " integer primary key autoincrement," +
        		Weather14Day.FORECAST_TIMESTAMP + " real default 0," +
        		Weather14Day.REQUEST_TIMESTAMP + " real default 0," +
        		Weather14Day.DEVICE_ID + " text default ''," +
        		Weather14Day.LATITUDE + " real default 0," +
        		Weather14Day.LONGITUDE + " real default 0," +
        		Weather14Day.TEMPERATURE_VALUE_CURRENT + " real default 0," +
        		Weather14Day.TEMPERATURE_VALUE_MIN + " real default 0," +
        		Weather14Day.TEMPERATURE_VALUE_MAX + " real default 0," +
        		Weather14Day.TEMPERATURE_VALUE_NIGHT + " real default 0," +
        		Weather14Day.TEMPERATURE_VALUE_DAY + " real default 0," +
        		Weather14Day.TEMPERATURE_VALUE_EVENING + " real default 0," +
        		Weather14Day.TEMPERATURE_VALUE_MORNING + " real default 0," +
				Weather14Day.HUMIDITY + " real default 0," +
				Weather14Day.PRESSURE + " real default 0," +
				Weather14Day.WIND_SPEED + " real default 0," +	
				Weather14Day.WIND_GUST + " real default 0," +
				Weather14Day.WIND_ANGLE + " real default 0," +
				Weather14Day.CLOUDS_VALUE + " real default 0," +				
				Weather14Day.RAIN + " real default 0," +
				Weather14Day.WEATHER_NAME + " text default ''," +
				Weather14Day.WEATHER_PROVIDER + " text default ''," +
				Weather14Day.SUNRISE + " real default 0," +
				Weather14Day.SUNSET + " real default 0," +
				Weather14Day.LOCATION_NAME + " text default ''," +
				Weather14Day.DATE_TIME + " text default ''," +
                "UNIQUE (" + Weather14Day._ID + "," + Weather14Day.DEVICE_ID + ")"
        };
        
        static {
                uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
                uriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0], WEATHER);
                uriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0]+"/#", WEATHER_ID);
                uriMatcher.addURI(AUTHORITY, DATABASE_TABLES[1], WEATHER_5DAY);
                uriMatcher.addURI(AUTHORITY, DATABASE_TABLES[1]+"/#", WEATHER_5DAY_ID);
                uriMatcher.addURI(AUTHORITY, DATABASE_TABLES[2], WEATHER_14DAY);
                uriMatcher.addURI(AUTHORITY, DATABASE_TABLES[2]+"/#", WEATHER_14DAY_ID);
                
                weatherMap = new HashMap<String, String>();
                weatherMap.put(WeatherCurrent._ID,WeatherCurrent._ID );
                weatherMap.put(WeatherCurrent.REQUEST_TIMESTAMP,WeatherCurrent.REQUEST_TIMESTAMP );
                weatherMap.put(WeatherCurrent.FORECAST_TIMESTAMP,WeatherCurrent.FORECAST_TIMESTAMP );
                weatherMap.put(WeatherCurrent.DEVICE_ID,WeatherCurrent.DEVICE_ID );
                weatherMap.put(WeatherCurrent.LATITUDE,WeatherCurrent.LATITUDE );
                weatherMap.put(WeatherCurrent.LONGITUDE,WeatherCurrent.LONGITUDE );
                weatherMap.put(WeatherCurrent.TEMPERATURE_VALUE_CURRENT,WeatherCurrent.TEMPERATURE_VALUE_CURRENT );
                weatherMap.put(WeatherCurrent.TEMPERATURE_VALUE_MIN,WeatherCurrent.TEMPERATURE_VALUE_MIN );
                weatherMap.put(WeatherCurrent.TEMPERATURE_VALUE_MAX,WeatherCurrent.TEMPERATURE_VALUE_MAX );
                weatherMap.put(WeatherCurrent.TEMPERATURE_VALUE_NIGHT,WeatherCurrent.TEMPERATURE_VALUE_NIGHT );
                weatherMap.put(WeatherCurrent.TEMPERATURE_VALUE_DAY,WeatherCurrent.TEMPERATURE_VALUE_DAY );
                weatherMap.put(WeatherCurrent.TEMPERATURE_VALUE_EVENING,WeatherCurrent.TEMPERATURE_VALUE_EVENING );
                weatherMap.put(WeatherCurrent.TEMPERATURE_VALUE_MORNING,WeatherCurrent.TEMPERATURE_VALUE_MORNING );
                weatherMap.put(WeatherCurrent.HUMIDITY,WeatherCurrent.HUMIDITY );
                weatherMap.put(WeatherCurrent.PRESSURE,WeatherCurrent.PRESSURE );
                weatherMap.put(WeatherCurrent.WIND_SPEED,WeatherCurrent.WIND_SPEED );
                weatherMap.put(WeatherCurrent.WIND_GUST,WeatherCurrent.WIND_GUST );
                weatherMap.put(WeatherCurrent.WIND_ANGLE,WeatherCurrent.WIND_ANGLE );
                weatherMap.put(WeatherCurrent.CLOUDS_VALUE,WeatherCurrent.CLOUDS_VALUE );
                weatherMap.put(WeatherCurrent.RAIN,WeatherCurrent.RAIN );
                weatherMap.put(WeatherCurrent.WEATHER_NAME,WeatherCurrent.WEATHER_NAME );
                weatherMap.put(WeatherCurrent.WEATHER_PROVIDER,WeatherCurrent.WEATHER_PROVIDER );
                weatherMap.put(WeatherCurrent.SUNRISE,WeatherCurrent.SUNRISE );
                weatherMap.put(WeatherCurrent.SUNSET,WeatherCurrent.SUNSET );
                weatherMap.put(WeatherCurrent.LOCATION_NAME,WeatherCurrent.LOCATION_NAME );
                weatherMap.put(WeatherCurrent.DATE_TIME,WeatherCurrent.DATE_TIME );
                
                weather5DayMap = new HashMap<String, String>();
                weather5DayMap.put(Weather5Day._ID,Weather5Day._ID );                
                weather5DayMap.put(Weather5Day.REQUEST_TIMESTAMP,Weather5Day.REQUEST_TIMESTAMP );
                weather5DayMap.put(Weather5Day.FORECAST_TIMESTAMP,Weather5Day.FORECAST_TIMESTAMP );
                weather5DayMap.put(Weather5Day.DEVICE_ID,Weather5Day.DEVICE_ID );
                weather5DayMap.put(Weather5Day.LATITUDE,Weather5Day.LATITUDE );
                weather5DayMap.put(Weather5Day.LONGITUDE,Weather5Day.LONGITUDE );
                weather5DayMap.put(Weather5Day.TEMPERATURE_VALUE_CURRENT,Weather5Day.TEMPERATURE_VALUE_CURRENT );
                weather5DayMap.put(Weather5Day.TEMPERATURE_VALUE_MIN,Weather5Day.TEMPERATURE_VALUE_MIN );
                weather5DayMap.put(Weather5Day.TEMPERATURE_VALUE_MAX,Weather5Day.TEMPERATURE_VALUE_MAX );
                weather5DayMap.put(Weather5Day.TEMPERATURE_VALUE_NIGHT,Weather5Day.TEMPERATURE_VALUE_NIGHT );
                weather5DayMap.put(Weather5Day.TEMPERATURE_VALUE_DAY,Weather5Day.TEMPERATURE_VALUE_DAY );
                weather5DayMap.put(Weather5Day.TEMPERATURE_VALUE_EVENING,Weather5Day.TEMPERATURE_VALUE_EVENING );
                weather5DayMap.put(Weather5Day.TEMPERATURE_VALUE_MORNING,Weather5Day.TEMPERATURE_VALUE_MORNING );
                weather5DayMap.put(Weather5Day.HUMIDITY,Weather5Day.HUMIDITY );
                weather5DayMap.put(Weather5Day.PRESSURE,Weather5Day.PRESSURE );
                weather5DayMap.put(Weather5Day.WIND_SPEED,Weather5Day.WIND_SPEED );
                weather5DayMap.put(Weather5Day.WIND_GUST,Weather5Day.WIND_GUST );
                weather5DayMap.put(Weather5Day.WIND_ANGLE,Weather5Day.WIND_ANGLE );
                weather5DayMap.put(Weather5Day.CLOUDS_VALUE,Weather5Day.CLOUDS_VALUE );
                weather5DayMap.put(Weather5Day.RAIN,Weather5Day.RAIN );
                weather5DayMap.put(Weather5Day.WEATHER_NAME,Weather5Day.WEATHER_NAME );
                weather5DayMap.put(Weather5Day.WEATHER_PROVIDER,Weather5Day.WEATHER_PROVIDER );
                weather5DayMap.put(Weather5Day.SUNRISE,Weather5Day.SUNRISE );
                weather5DayMap.put(Weather5Day.SUNSET,Weather5Day.SUNSET );
                weather5DayMap.put(Weather5Day.LOCATION_NAME,Weather5Day.LOCATION_NAME );
                weather5DayMap.put(Weather5Day.DATE_TIME,Weather5Day.DATE_TIME );
                
                weather14DayMap = new HashMap<String, String>();
                weather14DayMap.put(Weather14Day._ID,Weather14Day._ID );                
                weather14DayMap.put(Weather14Day.REQUEST_TIMESTAMP,Weather14Day.REQUEST_TIMESTAMP );
                weather14DayMap.put(Weather14Day.FORECAST_TIMESTAMP,Weather14Day.FORECAST_TIMESTAMP );
                weather14DayMap.put(Weather14Day.DEVICE_ID,Weather14Day.DEVICE_ID );
                weather14DayMap.put(Weather14Day.LATITUDE,Weather14Day.LATITUDE );
                weather14DayMap.put(Weather14Day.LONGITUDE,Weather14Day.LONGITUDE );
                weather14DayMap.put(Weather14Day.TEMPERATURE_VALUE_CURRENT,Weather14Day.TEMPERATURE_VALUE_CURRENT );
                weather14DayMap.put(Weather14Day.TEMPERATURE_VALUE_MIN,Weather14Day.TEMPERATURE_VALUE_MIN );
                weather14DayMap.put(Weather14Day.TEMPERATURE_VALUE_MAX,Weather14Day.TEMPERATURE_VALUE_MAX );
                weather14DayMap.put(Weather14Day.TEMPERATURE_VALUE_NIGHT,Weather14Day.TEMPERATURE_VALUE_NIGHT );
                weather14DayMap.put(Weather14Day.TEMPERATURE_VALUE_DAY,Weather14Day.TEMPERATURE_VALUE_DAY );
                weather14DayMap.put(Weather14Day.TEMPERATURE_VALUE_EVENING,Weather14Day.TEMPERATURE_VALUE_EVENING );
                weather14DayMap.put(Weather14Day.TEMPERATURE_VALUE_MORNING,Weather14Day.TEMPERATURE_VALUE_MORNING );
                weather14DayMap.put(Weather14Day.HUMIDITY,Weather14Day.HUMIDITY );
                weather14DayMap.put(Weather14Day.PRESSURE,Weather14Day.PRESSURE );
                weather14DayMap.put(Weather14Day.WIND_SPEED,Weather14Day.WIND_SPEED );
                weather14DayMap.put(Weather14Day.WIND_GUST,Weather14Day.WIND_GUST );
                weather14DayMap.put(Weather14Day.WIND_ANGLE,Weather14Day.WIND_ANGLE );
                weather14DayMap.put(Weather14Day.CLOUDS_VALUE,Weather14Day.CLOUDS_VALUE );
                weather14DayMap.put(Weather14Day.RAIN,Weather14Day.RAIN );
                weather14DayMap.put(Weather14Day.WEATHER_NAME,Weather14Day.WEATHER_NAME );
                weather14DayMap.put(Weather14Day.WEATHER_PROVIDER,Weather14Day.WEATHER_PROVIDER );
                weather14DayMap.put(Weather14Day.SUNRISE,Weather14Day.SUNRISE );
                weather14DayMap.put(Weather14Day.SUNSET,Weather14Day.SUNSET );
                weather14DayMap.put(Weather14Day.LOCATION_NAME,Weather14Day.LOCATION_NAME );
                weather14DayMap.put(Weather14Day.DATE_TIME,Weather14Day.DATE_TIME );
        }
        
        @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {
            if( database == null || ! database.isOpen()) database = databaseHelper.getWritableDatabase();
        
	        int count = 0;
	        switch (uriMatcher.match(uri)) {
	            case WEATHER:
	                count = database.delete(DATABASE_TABLES[0], selection, selectionArgs);
	                break;
	            case WEATHER_5DAY:
	                count = database.delete(DATABASE_TABLES[1], selection, selectionArgs);
	                break;
	            case WEATHER_14DAY:
	                count = database.delete(DATABASE_TABLES[2], selection, selectionArgs);
	                break;
	            default:
	                throw new IllegalArgumentException("Unknown URI " + uri);
	        }
	        getContext().getContentResolver().notifyChange(uri, null);
	        return count;
        }

        @Override
        public String getType(Uri uri) {
                switch (uriMatcher.match(uri)) {
                case WEATHER:
                    return WeatherCurrent.CONTENT_TYPE;
                case WEATHER_ID:
                    return WeatherCurrent.CONTENT_ITEM_TYPE;
                case WEATHER_5DAY:
                    return Weather5Day.CONTENT_TYPE;
                case WEATHER_5DAY_ID:
                    return Weather5Day.CONTENT_ITEM_TYPE;
                case WEATHER_14DAY:
                    return Weather14Day.CONTENT_TYPE;
                case WEATHER_14DAY_ID:
                    return Weather14Day.CONTENT_ITEM_TYPE;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }

        @Override
        public Uri insert(Uri uri, ContentValues initialValues) {
            if( database == null || ! database.isOpen()) database = databaseHelper.getWritableDatabase();
        
	        ContentValues values = (initialValues != null) ? new ContentValues(initialValues) : new ContentValues();
	        
	        switch(uriMatcher.match(uri)) {
	            case WEATHER:
	                long _id = database.insert(DATABASE_TABLES[0], WeatherCurrent.FORECAST_TIMESTAMP, values);
	                if (_id > 0) {
	                    Uri dataUri = ContentUris.withAppendedId(WeatherCurrent.CONTENT_URI, _id);
	                    getContext().getContentResolver().notifyChange(dataUri, null);
	                    return dataUri;
	                }
	                throw new SQLException("Failed to insert row into " + uri);
	            case WEATHER_5DAY:
	                long Weather5Day_id = database.insert(DATABASE_TABLES[1], Weather5Day.FORECAST_TIMESTAMP, values);
	                if (Weather5Day_id > 0) {
	                    Uri dataUri = ContentUris.withAppendedId(Weather5Day.CONTENT_URI, Weather5Day_id);
	                    getContext().getContentResolver().notifyChange(dataUri, null);
	                    return dataUri;
	                }
	                throw new SQLException("Failed to insert row into " + uri);
	            case WEATHER_14DAY:
	                long Weather14Day_id = database.insert(DATABASE_TABLES[2], Weather14Day.FORECAST_TIMESTAMP, values);
	                if (Weather14Day_id > 0) {
	                    Uri dataUri = ContentUris.withAppendedId(Weather14Day.CONTENT_URI, Weather14Day_id);
	                    getContext().getContentResolver().notifyChange(dataUri, null);
	                    return dataUri;
	                }
	                throw new SQLException("Failed to insert row into " + uri);
	            default:
	                throw new IllegalArgumentException("Unknown URI " + uri);
	        }
        }

        @Override
        public boolean onCreate() {
            if( databaseHelper == null ) databaseHelper = new DatabaseHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS );
	        database = databaseHelper.getWritableDatabase();
	        return (databaseHelper != null);
        }

        @Override
        public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
                
	        if( database == null || ! database.isOpen()) database = databaseHelper.getWritableDatabase();
	        
	        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	        switch (uriMatcher.match(uri)) {
	            case WEATHER:
	                qb.setTables(DATABASE_TABLES[0]);
	                qb.setProjectionMap(weatherMap);
	                break;
	            case WEATHER_5DAY:
	                qb.setTables(DATABASE_TABLES[1]);
	                qb.setProjectionMap(weather5DayMap);
	                break;
	            case WEATHER_14DAY:
	                qb.setTables(DATABASE_TABLES[2]);
	                qb.setProjectionMap(weather14DayMap);
	                break;
	            default:
	                throw new IllegalArgumentException("Unknown URI " + uri);
	        }
	        try {
	            Cursor c = qb.query(database, projection, selection, selectionArgs, null, null, sortOrder);
	            c.setNotificationUri(getContext().getContentResolver(), uri);
	            return c;
	        }catch ( IllegalStateException e ) {
	            if ( Aware.DEBUG ) Log.e(Aware.TAG,e.getMessage());
	            return null;
	        }
        }

        @Override
        public int update(Uri uri, ContentValues values, String selection,
                        String[] selectionArgs) {
                
	                if( database == null || ! database.isOpen()) database = databaseHelper.getWritableDatabase();
	        
	                int count = 0;
	        switch (uriMatcher.match(uri)) {
	            case WEATHER:
	                count = database.update(DATABASE_TABLES[0], values, selection, selectionArgs);
	                break;
	            case WEATHER_5DAY:
	                count = database.update(DATABASE_TABLES[1], values, selection, selectionArgs);
	                break;
	            case WEATHER_14DAY:
	                count = database.update(DATABASE_TABLES[2], values, selection, selectionArgs);
	                break;
	            default:
	                database.close();
	                throw new IllegalArgumentException("Unknown URI " + uri);
	        }
	        getContext().getContentResolver().notifyChange(uri, null);
	        return count;
        }
}