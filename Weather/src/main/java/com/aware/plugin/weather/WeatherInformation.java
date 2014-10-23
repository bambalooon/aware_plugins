package com.aware.plugin.weather;

import java.util.Vector;

import android.content.ContentValues;

import com.aware.plugin.weather.Weather_Provider.WeatherCurrent;

public class WeatherInformation {
	
	private long forecastTimestamp;
	private long requestTimestamp;
	private String dateTime;
	
	private String cityName;
	private double cityLat;
	private double cityLng;
	
	private long sunrise;
	private long sunset;
	
	private double currentTemperature;
	private double minTemperature;
	private double maxTemperature;
	private double nightTemperature;
	private double dayTemperature;
	private double morningTemperature;
	private double eveningTemperature;
	
	private double pressure;
	private double humidity;
	
	private double windSpeed;
	private double windAngle;
	private double windGust;
	
	private double rain;
	
	private double clouds;

	private Vector<String> weatherDescriptions;
	
	private String deviceID;

	public WeatherInformation(String cityName, double cityLat, double cityLng,String deviceID) {
		setCityName(cityName);
		setCityLat(cityLat);
		setCityLng(cityLng);
		setDeviceID(deviceID);
		
		weatherDescriptions = new Vector<String>(0,1);
	}
	
	
	/**
	 * Creates a ContentValues object out of the saved values.
	 * 
	 * @return ContentValues
	 */
	public ContentValues getContentValues() {
		ContentValues rowData = new ContentValues();
		
		rowData.put(WeatherCurrent.DEVICE_ID, this.deviceID);
		rowData.put(WeatherCurrent.FORECAST_TIMESTAMP, this.forecastTimestamp);
		rowData.put(WeatherCurrent.REQUEST_TIMESTAMP, this.requestTimestamp);
		rowData.put(WeatherCurrent.DATE_TIME, this.dateTime);
		rowData.put(WeatherCurrent.LOCATION_NAME, this.cityName);
		rowData.put(WeatherCurrent.LATITUDE, this.cityLat);
		rowData.put(WeatherCurrent.LONGITUDE, this.cityLng);
		rowData.put(WeatherCurrent.SUNRISE, this.sunrise);
		rowData.put(WeatherCurrent.SUNSET, this.sunset);
		rowData.put(WeatherCurrent.TEMPERATURE_VALUE_CURRENT, this.currentTemperature);
		rowData.put(WeatherCurrent.TEMPERATURE_VALUE_DAY, this.dayTemperature);
		rowData.put(WeatherCurrent.TEMPERATURE_VALUE_EVENING, this.eveningTemperature);
		rowData.put(WeatherCurrent.TEMPERATURE_VALUE_MAX, this.maxTemperature);
		rowData.put(WeatherCurrent.TEMPERATURE_VALUE_MIN, this.minTemperature);
		rowData.put(WeatherCurrent.TEMPERATURE_VALUE_MORNING, this.morningTemperature);
		rowData.put(WeatherCurrent.TEMPERATURE_VALUE_NIGHT, this.nightTemperature);
		rowData.put(WeatherCurrent.PRESSURE, this.pressure);
		rowData.put(WeatherCurrent.HUMIDITY, this.humidity);
		rowData.put(WeatherCurrent.WIND_SPEED, this.windSpeed);
		rowData.put(WeatherCurrent.WIND_GUST, this.windGust);
		rowData.put(WeatherCurrent.WIND_ANGLE, this.windAngle);
		rowData.put(WeatherCurrent.RAIN, this.rain);
		rowData.put(WeatherCurrent.WEATHER_PROVIDER, Weather_Connector_OpenWeatherMap.WEATHER_AUTHORITY);
		
		// The map can potentially give multiple names for the current weather condition.
		String tmp = "";
		
		for(int i=0;i<weatherDescriptions.size()-1;i++)
			tmp = tmp + weatherDescriptions.get(i) + ",";
		
		tmp = tmp + weatherDescriptions.get(weatherDescriptions.size()-1);
		
		rowData.put(WeatherCurrent.WEATHER_NAME, tmp);
				
		return rowData;
	}
	
	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public double getCityLat() {
		return cityLat;
	}

	public void setCityLat(double cityLat) {
		this.cityLat = cityLat;
	}

	public double getCityLng() {
		return cityLng;
	}

	public void setCityLng(double cityLng) {
		this.cityLng = cityLng;
	}

	public long getSunrise() {
		return sunrise;
	}

	public void setSunrise(long sunrise) {
		this.sunrise = sunrise;
	}

	public long getSunset() {
		return sunset;
	}

	public void setSunset(long sunset) {
		this.sunset = sunset;
	}

	public double getCurrentTemperature() {
		return currentTemperature;
	}

	public void setCurrentTemperature(double currentTemperature) {
		this.currentTemperature = currentTemperature;
	}

	public double getMinTemperature() {
		return minTemperature;
	}

	public void setMinTemperature(double minTemperature) {
		this.minTemperature = minTemperature;
	}

	public double getMaxTemperature() {
		return maxTemperature;
	}

	public void setMaxTemperature(double maxTemperature) {
		this.maxTemperature = maxTemperature;
	}

	public double getNightTemperature() {
		return nightTemperature;
	}

	public void setNightTemperature(double nightTemperature) {
		this.nightTemperature = nightTemperature;
	}

	public double getDayTemperature() {
		return dayTemperature;
	}

	public void setDayTemperature(double dayTemperature) {
		this.dayTemperature = dayTemperature;
	}

	public double getMorningTemperature() {
		return morningTemperature;
	}

	public void setMorningTemperature(double morningTemperature) {
		this.morningTemperature = morningTemperature;
	}

	public double getEveningTemperature() {
		return eveningTemperature;
	}

	public void setEveningTemperature(double eveningTemperature) {
		this.eveningTemperature = eveningTemperature;
	}

	public double getPressure() {
		return pressure;
	}

	public void setPressure(double pressure) {
		this.pressure = pressure;
	}

	public double getHumidity() {
		return humidity;
	}

	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}

	public double getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
	}

	public double getWindAngle() {
		return windAngle;
	}

	public void setWindAngle(double windAngle) {
		this.windAngle = windAngle;
	}

	public double getWindGust() {
		return windGust;
	}

	public void setWindGust(double windGust) {
		this.windGust = windGust;
	}

	public double getRain() {
		return rain;
	}

	public void setRain(double rain) {
		this.rain = rain;
	}

	public Vector<String> getWeatherDescriptions() {
		return weatherDescriptions;
	}

	public void setWeatherDescriptions(Vector<String> weatherDescriptions) {
		this.weatherDescriptions = weatherDescriptions;
	}

	public long getForecastTimestamp() {
		return forecastTimestamp;
	}

	public void setForecastTimestamp(long forecastTimestamp) {
		this.forecastTimestamp = forecastTimestamp;
	}

	public long getRequestTimestamp() {
		return requestTimestamp;
	}

	public void setRequestTimestamp(long requestTimestamp) {
		this.requestTimestamp = requestTimestamp;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public double getClouds() {
		return clouds;
	}

	public void setClouds(double clouds) {
		this.clouds = clouds;
	}


	public String getDeviceID() {
		return deviceID;
	}


	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
}