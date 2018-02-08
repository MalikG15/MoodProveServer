package moodprove.weather;

import org.json.JSONArray;
import org.json.JSONObject;

import moodprove.http.MoodProveHttp;

public class WeatherData {
	
	private final static String WEATHER_API_DOMAIN = "https://api.darksky.net/forecast/%s/%s,%s";
	
	private final Double longitude;
	
	private final Double latitude;
	
	public WeatherData(Double longitude, Double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public JSONArray getDailyWeatherData() {
		String weatherResponse = MoodProveHttp.executeGet(String.format(WEATHER_API_DOMAIN, WeatherAPIClientInfo.WEATHER_API_KEY, longitude, latitude));
		JSONObject weatherData = new JSONObject(weatherResponse);
		JSONArray daily = weatherData.getJSONArray("daily");
		return daily;
	}
	
	public JSONObject getTodaysWeatherData() {
		JSONArray weatherDailyArray = getDailyWeatherData();
		return weatherDailyArray.getJSONObject(0);
	}

}
