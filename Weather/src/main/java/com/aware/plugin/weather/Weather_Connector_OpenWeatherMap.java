package com.aware.plugin.weather;

import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import com.aware.plugin.weather.Weather_Provider.Weather14Day;
import com.aware.plugin.weather.Weather_Provider.Weather5Day;
import com.aware.plugin.weather.Weather_Provider.WeatherCurrent;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class Weather_Connector_OpenWeatherMap implements Weather_Connector {

    public static final int QUERY_CURRENT = 0;
    public static final int QUERY_5DAY = 1;
    public static final int QUERY_14DAY = 2;
	public static final String WEATHER_AUTHORITY = "openweathermap.org";

    private boolean WEATHER_CURRENT_AVAILABLE = false;
    private boolean WEATHER_5DAY_AVAILABLE = false;
	private boolean WEATHER_14DAY_AVAILABLE = false;

	private Context appContext;
	private String deviceID;

    private long lastCurrentWeatherCall;
    private long last5DaysWeatherCall;
    private long last14DaysWeatherCall;

	public Weather_Connector_OpenWeatherMap(boolean weatherCurrent,boolean weather5Day,boolean weather14Day,Context appContext,String deviceId) {
		WEATHER_CURRENT_AVAILABLE = weatherCurrent;
		WEATHER_5DAY_AVAILABLE = weather5Day;
		WEATHER_14DAY_AVAILABLE = weather14Day;
		
		this.setAppContext(appContext);
		this.setDeviceID(deviceId);
	}
	

	public void readJsonStream(String jsonString,double lat, double lng, int lastQuery) throws IOException {
			 
		/*
		 *  The OpenWeatherMap JSON string is differently formated depending on the mode
		 */

		switch(lastQuery) {
		
		case Weather_Connector_OpenWeatherMap.QUERY_CURRENT:
			
			try {
				JSONObject jo = new JSONObject(jsonString);
				parseCurrentWeather(jo,lat,lng);
			} catch (JSONException e) {				
//				e.printStackTrace();
			}
			
			break;
		case Weather_Connector_OpenWeatherMap.QUERY_5DAY:
			try {
				JSONObject jo = new JSONObject(jsonString);
				parse5DayWeather(jo,lat,lng);
			} catch (JSONException e) {				
//				e.printStackTrace();
			}
			
			break;
		case Weather_Connector_OpenWeatherMap.QUERY_14DAY:
			try {
				JSONObject jo = new JSONObject(jsonString);
				parse14DayWeather(jo,lat,lng);
			} catch (JSONException e) {				
//				e.printStackTrace();
			}
			
			break;		
		}
	}

	private void parse14DayWeather(JSONObject response,double lat, double lng) {				
		
		JSONArray list = null;
		JSONObject city = null;
				
		JSONArray weather = null;
		JSONObject temp = null;
		
		WeatherInformation currentWeather = null;
		
		JSONObject current = null;
		
		Calendar currentTime = Calendar.getInstance();
		
		try {
			city = response.getJSONObject("city");
		} catch (JSONException e2) {			
//			e2.printStackTrace();
		}
		try {
			list = response.getJSONArray("list");
		} catch (JSONException e2) {			
//			e2.printStackTrace();
		}
		
		for(int j=0;j<list.length();j++) {		
			
			weather = null;
			temp = null;
			current = null;
		
			try {
				current = list.getJSONObject(j);
			} catch (JSONException e1) {					
//				e1.printStackTrace();
			}
			
			if(current != null) {
				currentWeather = new WeatherInformation("",0,0,this.deviceID);
				currentWeather.setRequestTimestamp(currentTime.getTimeInMillis());
					
				try {
					temp = current.getJSONObject("temp");
				} catch (JSONException e1) {
//					e1.printStackTrace();
				}
				
				try {
					weather = current.getJSONArray("weather");
				} catch (JSONException e) {			
//					e.printStackTrace();
				}
						
				try {
					currentWeather.setForecastTimestamp(current.getLong("dt")*1000L);
				} catch (JSONException e) {			
//					e.printStackTrace();
				}
				
				Calendar dateTime = Calendar.getInstance();
				try {
					dateTime.setTimeInMillis(current.getLong("dt")*1000L);						
				} catch (JSONException e) {		
//					e.printStackTrace();
				}
						
				currentWeather.setDateTime(dateTime.getTime().toString());
				
				try {
					currentWeather.setCityName(city.getString("name"));
				} catch (JSONException e) {			
//					e.printStackTrace();
				}
				
				currentWeather.setCityLat(lat);
				currentWeather.setCityLng(lng);
				
				if(temp != null) {
					
					try {
						currentWeather.setDayTemperature(temp.getDouble("day"));
					} catch (JSONException e) {						
//						e.printStackTrace();
					}
					
					try {
						currentWeather.setNightTemperature(temp.getDouble("night"));
					} catch (JSONException e) {
//						e.printStackTrace();
					}
					
					try {
						currentWeather.setMinTemperature(temp.getDouble("min"));
					} catch (JSONException e) {						
//						e.printStackTrace();
					}
					
					try {
						currentWeather.setMaxTemperature(temp.getDouble("max"));
					} catch (JSONException e) {
//						e.printStackTrace();
					}
					
					try {
						currentWeather.setMorningTemperature(temp.getDouble("morn"));
					} catch (JSONException e) {						
//						e.printStackTrace();
					}
					
					try {
						currentWeather.setEveningTemperature(temp.getDouble("eve"));
					} catch (JSONException e) {
//						e.printStackTrace();
					}
				}
				
				try {
					currentWeather.setPressure(current.getDouble("pressure"));
				} catch (JSONException e1) {
//					e1.printStackTrace();
				}
				
				try {
					currentWeather.setHumidity(current.getDouble("humidity"));
				} catch (JSONException e1) {
//					e1.printStackTrace();
				}
				
				try {
					currentWeather.setWindSpeed(current.getDouble("speed"));
				} catch (JSONException e1) {					
//					e1.printStackTrace();
				}
				
				try {
					currentWeather.setWindAngle(current.getDouble("deg"));
				} catch (JSONException e1) {
//					e1.printStackTrace();
				}
				
				try {
					currentWeather.setClouds(current.getDouble("clouds"));
				} catch (JSONException e1) {					
//					e1.printStackTrace();
				}
				
				try {
					currentWeather.setRain(current.getDouble("rain"));
				} catch (JSONException e1) {
//					e1.printStackTrace();
				}
				
				Vector<String> weatherDescriptions = new Vector<String>(0,1);
				
				if(weather != null) {
					JSONObject tmp = null;
					
					for(int i=0;i<weather.length();i++) {
						try {
							tmp = weather.getJSONObject(i);
						} catch (JSONException e) {
//							e.printStackTrace();
						}					
						try {
							weatherDescriptions.add(tmp.getString("description"));
						} catch (JSONException e) {
//							e.printStackTrace();
						} 
					}
				}						
				currentWeather.setWeatherDescriptions(weatherDescriptions);
						

				appContext.getContentResolver().insert(Weather14Day.CONTENT_URI,currentWeather.getContentValues());

//				Log.e("Weather Plugin","Weather Information Saved");
			}					
		}
        last14DaysWeatherCall = new Date().getTime();
        Log.d(Plugin.TAG, Plugin.ACTION_AWARE_WEATHER+": 14 Day Weather Added");
    }
	
	private void parse5DayWeather(JSONObject response,double lat, double lng) {				
		
		JSONArray list = null;
		JSONObject city = null;
		
		JSONObject sys = null;
		JSONArray weather = null;
		JSONObject main = null;
		JSONObject wind = null;
		JSONObject rain = null;
		JSONObject clouds = null;				
		
		WeatherInformation currentWeather = null;
		
		JSONObject current = null;
		
		Calendar currentTime = Calendar.getInstance();
		
		try {
			city = response.getJSONObject("city");
		} catch (JSONException e2) {			
//			e2.printStackTrace();
		}
		try {
			list = response.getJSONArray("list");
		} catch (JSONException e2) {			
//			e2.printStackTrace();
		}
		
		for(int j=0;j<list.length();j++) {
			
			sys = null;
			weather = null;
			main = null;
			wind = null;
			rain = null;
			clouds = null;
			current = null;
			
			try {
				current = list.getJSONObject(j);
			} catch (JSONException e1) {				
//				e1.printStackTrace();
			}
			
			if(current != null) {
				currentWeather = new WeatherInformation("",0,0,this.deviceID);
				currentWeather.setRequestTimestamp(currentTime.getTimeInMillis());
				
				try {
					sys = current.getJSONObject("sys");
				} catch (JSONException e) {			
//					e.printStackTrace();
				}
				
				try {
					weather = current.getJSONArray("weather");
				} catch (JSONException e) {			
//					e.printStackTrace();
				}
				try {
					main = current.getJSONObject("main");
				} catch (JSONException e) {			
//					e.printStackTrace();
				}
				try {
					wind = current.getJSONObject("wind");
				} catch (JSONException e) {			
//					e.printStackTrace();
				}
				try {
					rain = current.getJSONObject("rain");
				} catch (JSONException e) {			
//					e.printStackTrace();
				}
				try {
					clouds = current.getJSONObject("clouds");
				} catch (JSONException e) {			
//					e.printStackTrace();
				}
							
				try {
					currentWeather.setForecastTimestamp(current.getLong("dt")*1000L);
				} catch (JSONException e) {			
//					e.printStackTrace();
				}
				
				Calendar dateTime = Calendar.getInstance();
				try {
					dateTime.setTimeInMillis(current.getLong("dt")*1000L);						
				} catch (JSONException e) {		
//					e.printStackTrace();
				}
						
				currentWeather.setDateTime(dateTime.getTime().toString());
				
				try {
					currentWeather.setCityName(city.getString("name"));
				} catch (JSONException e) {			
//					e.printStackTrace();
				}
				
				currentWeather.setCityLat(lat);
				currentWeather.setCityLng(lng);
				
				if(sys != null) {
					try {
						currentWeather.setSunrise(sys.getLong("sunrise"));
					} catch (JSONException e) {				
//						e.printStackTrace();
					}
					try {
						currentWeather.setSunset(sys.getLong("sunset"));
					} catch (JSONException e) {
//						e.printStackTrace();
					}
				}
				
				if(main != null) {
					try {
						currentWeather.setCurrentTemperature(main.getDouble("temp"));
					} catch (JSONException e) {
//						e.printStackTrace();
					}
					try {
						currentWeather.setMinTemperature(main.getDouble("temp_min"));
					} catch (JSONException e) {
//						e.printStackTrace();
					}
					try {
						currentWeather.setMaxTemperature(main.getDouble("temp_max"));
					} catch (JSONException e) {
//						e.printStackTrace();
					}
					try {
						currentWeather.setHumidity(main.getDouble("humidity"));
					} catch (JSONException e) {
//						e.printStackTrace();
					}
					try {
						currentWeather.setPressure(main.getDouble("pressure"));
					} catch (JSONException e) {
//						e.printStackTrace();
					}
				}
				
				if(wind != null) {
					try {
						currentWeather.setWindSpeed(wind.getDouble("speed"));
					} catch (JSONException e) {
//						e.printStackTrace();
					}
					try {
						currentWeather.setWindGust(wind.getDouble("gust"));
					} catch (JSONException e) {
//						e.printStackTrace();
					}
					try {
						currentWeather.setWindAngle(wind.getDouble("deg"));
					} catch (JSONException e) {
//						e.printStackTrace();
					}
				}
				
				if(rain != null) {
					try {
						currentWeather.setRain(rain.getDouble("3h"));
					} catch (JSONException e) {
//						e.printStackTrace();
					}
				}
				
				if(clouds != null) {
					try {
						currentWeather.setClouds(clouds.getDouble("all"));
					} catch (JSONException e) {
//						e.printStackTrace();
					}
				}
				
				Vector<String> weatherDescriptions = new Vector<String>(0,1);
				
				if(weather != null) {
					JSONObject tmp = null;
					
					for(int i=0;i<weather.length();i++) {
						try {
							tmp = weather.getJSONObject(i);
						} catch (JSONException e) {
//							e.printStackTrace();
						}					
						try {
							weatherDescriptions.add(tmp.getString("description"));
						} catch (JSONException e) {
//							e.printStackTrace();
						} 
					}
				}						
				currentWeather.setWeatherDescriptions(weatherDescriptions);						
				
				appContext.getContentResolver().insert(Weather5Day.CONTENT_URI,currentWeather.getContentValues());

//				Log.e("Weather Plugin","Weather Information Saved");
			}
        }
        last5DaysWeatherCall = new Date().getTime();
        Log.d(Plugin.TAG, Plugin.ACTION_AWARE_WEATHER+": 5 Day Weather Added");
	}
	
	private void parseCurrentWeather(JSONObject current,double lat, double lng) {
				
		JSONObject sys = null;
		JSONArray weather = null;
		JSONObject main = null;
		JSONObject wind = null;
		JSONObject rain = null;
		JSONObject clouds = null;
		
		WeatherInformation currentWeather = new WeatherInformation("",0,0,this.deviceID);
		
		Calendar currentTime = Calendar.getInstance();
		currentWeather.setRequestTimestamp(currentTime.getTimeInMillis());
		
		try {
			sys = current.getJSONObject("sys");
		} catch (JSONException e) {			
//			e.printStackTrace();
		}
		
		try {
			weather = current.getJSONArray("weather");
		} catch (JSONException e) {			
//			e.printStackTrace();
		}
		try {
			main = current.getJSONObject("main");
		} catch (JSONException e) {			
//			e.printStackTrace();
		}
		try {
			wind = current.getJSONObject("wind");
		} catch (JSONException e) {			
//			e.printStackTrace();
		}
		try {
			rain = current.getJSONObject("rain");
		} catch (JSONException e) {			
//			e.printStackTrace();
		}
		try {
			clouds = current.getJSONObject("clouds");
		} catch (JSONException e) {			
//			e.printStackTrace();
		}
					
		try {
			currentWeather.setForecastTimestamp(current.getLong("dt")*1000L);
		} catch (JSONException e) {			
//			e.printStackTrace();
		}
		
		Calendar dateTime = Calendar.getInstance();
		try {
			dateTime.setTimeInMillis(current.getLong("dt")*1000L);						
		} catch (JSONException e) {		
//			e.printStackTrace();
		}
				
		currentWeather.setDateTime(dateTime.getTime().toString());
		
		try {
			currentWeather.setCityName(current.getString("name"));
		} catch (JSONException e) {			
//			e.printStackTrace();
		}
		
		currentWeather.setCityLat(lat);
		currentWeather.setCityLng(lng);
		
		if(sys != null) {
			try {
				currentWeather.setSunrise(sys.getLong("sunrise"));
			} catch (JSONException e) {				
//				e.printStackTrace();
			}
			try {
				currentWeather.setSunset(sys.getLong("sunset"));
			} catch (JSONException e) {
//				e.printStackTrace();
			}
		}
		
		if(main != null) {
			try {
				currentWeather.setCurrentTemperature(main.getDouble("temp"));
			} catch (JSONException e) {
//				e.printStackTrace();
			}
			try {
				currentWeather.setMinTemperature(main.getDouble("temp_min"));
			} catch (JSONException e) {
//				e.printStackTrace();
			}
			try {
				currentWeather.setMaxTemperature(main.getDouble("temp_max"));
			} catch (JSONException e) {
//				e.printStackTrace();
			}
			try {
				currentWeather.setHumidity(main.getDouble("humidity"));
			} catch (JSONException e) {
//				e.printStackTrace();
			}
			try {
				currentWeather.setPressure(main.getDouble("pressure"));
			} catch (JSONException e) {
//				e.printStackTrace();
			}
		}
		
		if(wind != null) {
			try {
				currentWeather.setWindSpeed(wind.getDouble("speed"));
			} catch (JSONException e) {
//				e.printStackTrace();
			}
			try {
				currentWeather.setWindGust(wind.getDouble("gust"));
			} catch (JSONException e) {
//				e.printStackTrace();
			}
			try {
				currentWeather.setWindAngle(wind.getDouble("deg"));
			} catch (JSONException e) {
//				e.printStackTrace();
			}
		}
		
		if(rain != null) {
			try {
				currentWeather.setRain(rain.getDouble("3h"));
			} catch (JSONException e) {
//				e.printStackTrace();
			}
		}
		
		if(clouds != null) {
			try {
				currentWeather.setClouds(clouds.getDouble("all"));
			} catch (JSONException e) {
//				e.printStackTrace();
			}
		}
		
		Vector<String> weatherDescriptions = new Vector<String>(0,1);
		
		if(weather != null) {
			JSONObject tmp = null;
			
			for(int i=0;i<weather.length();i++) {
				try {
					tmp = weather.getJSONObject(i);
				} catch (JSONException e) {
//					e.printStackTrace();
				}					
				try {
					weatherDescriptions.add(tmp.getString("description"));
				} catch (JSONException e) {
//					e.printStackTrace();
				} 
			}
		}						
		currentWeather.setWeatherDescriptions(weatherDescriptions);
				
		
		appContext.getContentResolver().insert(WeatherCurrent.CONTENT_URI, currentWeather.getContentValues());
        appContext.sendBroadcast(generateWeatherNotification(currentWeather));
        lastCurrentWeatherCall = new Date().getTime();
        Log.d(Plugin.TAG, Plugin.ACTION_AWARE_WEATHER+": CurrentWeather Notification");

//		Log.e("Weather Plugin","Weather Information Saved");
			
	}

    private Intent generateWeatherNotification(WeatherInformation weather) {
        Intent notification = new Intent(Plugin.ACTION_AWARE_WEATHER);
        notification.putExtra(WeatherCurrent.FORECAST_TIMESTAMP, weather.getForecastTimestamp());
        notification.putExtra(WeatherCurrent.REQUEST_TIMESTAMP, weather.getRequestTimestamp());
        notification.putExtra(WeatherCurrent.DATE_TIME, weather.getDateTime());
        notification.putExtra(WeatherCurrent.LOCATION_NAME, weather.getCityName());
        notification.putExtra(WeatherCurrent.LATITUDE, weather.getCityLat());
        notification.putExtra(WeatherCurrent.LONGITUDE, weather.getCityLng());
        notification.putExtra(WeatherCurrent.SUNRISE, weather.getSunrise());
        notification.putExtra(WeatherCurrent.SUNSET, weather.getSunset());
        notification.putExtra(WeatherCurrent.TEMPERATURE_VALUE_CURRENT, weather.getCurrentTemperature());
        notification.putExtra(WeatherCurrent.TEMPERATURE_VALUE_DAY, weather.getDayTemperature());
        notification.putExtra(WeatherCurrent.TEMPERATURE_VALUE_EVENING, weather.getEveningTemperature());
        notification.putExtra(WeatherCurrent.TEMPERATURE_VALUE_MAX, weather.getMaxTemperature());
        notification.putExtra(WeatherCurrent.TEMPERATURE_VALUE_MIN, weather.getMinTemperature());
        notification.putExtra(WeatherCurrent.TEMPERATURE_VALUE_MORNING, weather.getMorningTemperature());
        notification.putExtra(WeatherCurrent.TEMPERATURE_VALUE_NIGHT, weather.getNightTemperature());
        notification.putExtra(WeatherCurrent.PRESSURE, weather.getPressure());
        notification.putExtra(WeatherCurrent.HUMIDITY, weather.getHumidity());
        notification.putExtra(WeatherCurrent.WIND_SPEED, weather.getWindSpeed());
        notification.putExtra(WeatherCurrent.WIND_GUST, weather.getWindGust());
        notification.putExtra(WeatherCurrent.WIND_ANGLE, weather.getWindAngle());
        notification.putExtra(WeatherCurrent.RAIN, weather.getRain());
        notification.putExtra(WeatherCurrent.WEATHER_PROVIDER, Weather_Connector_OpenWeatherMap.WEATHER_AUTHORITY);
        return notification;
    }


    private class NetworkTask extends AsyncTask<String, Void, Integer> {
	    @Override
	    protected Integer doInBackground(String... params) {
	        String link = params[0];
	        Double lat = Double.valueOf(params[1]);
	        Double lng = Double.valueOf(params[2]);
            int lastQuery = Integer.valueOf(params[3]);
	        HttpGet request = new HttpGet(link);
	        AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
	        try {
	        		        	
	            HttpResponse response = client.execute(request);
	            if( response == null || response.getStatusLine().getStatusCode() != 200 ) {
	                client.close(); 
	                return null;
	            }
	            
	    		String jsonString = EntityUtils.toString(response.getEntity());
	            readJsonStream(jsonString,lat,lng,lastQuery);
	            
	        } catch (IOException e) {
//	            e.printStackTrace();
	        } finally {
	        client.close();
	    }
			return null;
	    }

	    @Override
	    protected void onPostExecute(Integer result) {}
	}
	
	@Override
	public void getWeatherCurrent(double[] latitudeList,
			double[] longitudeList) {

        int lastQuery = Weather_Connector_OpenWeatherMap.QUERY_CURRENT;
		String[] params = new String[4];
		
		for(int i=0;i<latitudeList.length;i++) {
			params[0] = "http://api.openweathermap.org/data/2.5/weather?lat=" + latitudeList[i] + "&lon=" + longitudeList[i] + "&units=imperial&mode=json";
			params[1] = "" + latitudeList[i];
			params[2] = "" + longitudeList[i];
            params[3] = "" + lastQuery;
			
			new NetworkTask().execute(params);
		}
		
	}

	@Override
	public void getWeather5Day(double[] latitudeList,
			double[] longitudeList) {

        int lastQuery = Weather_Connector_OpenWeatherMap.QUERY_5DAY;
		String[] params = new String[4];
		
		for(int i=0;i<latitudeList.length;i++) {
			params[0] = "http://api.openweathermap.org/data/2.5/forecast?lat=" + latitudeList[i] + "&lon=" + longitudeList[i] + "&units=imperial&mode=json";
			params[1] = "" + latitudeList[i];
			params[2] = "" + longitudeList[i];
			params[3] = "" + lastQuery;

			new NetworkTask().execute(params);
		}
	}

	@Override
	public void getWeather14Day(double[] latitudeList,
			double[] longitudeList) {

        int lastQuery = Weather_Connector_OpenWeatherMap.QUERY_14DAY;
		String[] params = new String[4];
		
		for(int i=0;i<latitudeList.length;i++) {
			params[0] = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=" + latitudeList[i] + "&lon=" + longitudeList[i] + "&units=imperial&mode=json&cnt=14";
			params[1] = "" + latitudeList[i];
			params[2] = "" + longitudeList[i];
			params[3] = "" + lastQuery;

			new NetworkTask().execute(params);
		}		
	}

	@Override
	public boolean isCurrentAvailable() {		
		return WEATHER_CURRENT_AVAILABLE;
	}

	@Override
	public boolean is5DayAvailable() {		
		return WEATHER_5DAY_AVAILABLE;
	}

	@Override
	public boolean is14DayAvailable() {		
		return WEATHER_14DAY_AVAILABLE;
	}

	@Override
	public String getWeatherAuthority() {		
		return WEATHER_AUTHORITY;
	}


	public Context getAppContext() {
		return appContext;
	}


	public void setAppContext(Context appContext) {
		this.appContext = appContext;
	}


	public String getDeviceID() {
		return deviceID;
	}


	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

    @Override
    public long getLastCurrentWeatherCall() {
        return lastCurrentWeatherCall;
    }

    @Override
    public long getLast5DaysWeatherCall() {
        return last5DaysWeatherCall;
    }

    @Override
    public long getLast14DaysWeatherCall() {
        return last14DaysWeatherCall;
    }

}
