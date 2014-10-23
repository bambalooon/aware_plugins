package com.aware.plugin.weather;

/*
 *  Every weather api connector needs to implement the following methods to be compatible with the plugin:
 *  
 *  The following fields are currently implemented in the weather provider:
 *  
 *  _ID  " integer primary key autoincrement"
	FORECAST_TIMESTAMP  " real default 0"
	REQUEST_TIMESTAMP  " real default 0"
	DEVICE_ID  " text default ''"
	LATITUDE  " real default 0"
	LONGITUDE  " real default 0"
	TEMPERATURE_VALUE_CURRENT  " real default 0"
	TEMPERATURE_VALUE_MIN  " real default 0"
	TEMPERATURE_VALUE_MAX  " real default 0"
	TEMPERATURE_VALUE_NIGHT  " real default 0"
	TEMPERATURE_VALUE_DAY  " real default 0"
	TEMPERATURE_VALUE_EVENING  " real default 0"
	TEMPERATURE_VALUE_MORNING  " real default 0"
	HUMIDITY  " real default 0"
	PRESSURE  " real default 0"
	WIND_SPEED  " real default 0"	
	WIND_GUST  " real default 0"
	WIND_ANGLE  " real default 0"
	CLOUDS_VALUE  " real default 0"				
	RAIN  " real default 0"
	WEATHER_NAME  " text default ''"
	WEATHER_PROVIDER  " text default ''"
	SUNRISE  " real default 0"
	SUNSET  " real default 0"
	LOCATION_NAME  " text default ''"
	DATE_TIME  " text default ''"
    
    Three tables are implemented that all share the same table schema: 
    
    "plugin_weather_current",
    "plugin_weather_5day",
    "plugin_weather_14day"
    
    The number of rows that belong to the same query differ:
    
    current:	1 row,
    5Day:		41 rows,
    14Day:		14 rows. 
    
 */

public interface Weather_Connector {		
		
	public String getWeatherAuthority();
	
	public boolean isCurrentAvailable();
	public boolean is5DayAvailable();
	public boolean is14DayAvailable();
	
	public void getWeatherCurrent(double[] latitudeList, double[] longitudeList);
	public void getWeather5Day(double[] latitudeList, double[] longitudeList);
	public void getWeather14Day(double[] latitudeList, double[] longitudeList);
}
