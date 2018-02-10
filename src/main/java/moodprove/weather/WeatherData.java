package moodprove.weather;

import org.json.JSONArray;
import org.json.JSONObject;

import moodprove.http.MoodProveHttp;
import moodprove.to.Weather;

public class WeatherData {
	
	private static final String WEATHER_API_CALL = "https://api.darksky.net/forecast/%s/%s,%s";
	
	private final Double longitude;
	
	private final Double latitude;
	
	public WeatherData(Double longitude, Double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public JSONArray getDailyWeatherData() {
		String weatherResponse = MoodProveHttp.executeGet(String.format(WEATHER_API_CALL, WeatherAPIClientInfo.WEATHER_API_KEY, latitude, longitude), "");
		JSONObject weatherData = new JSONObject(weatherResponse);
		JSONArray daily = weatherData.getJSONObject("daily").getJSONArray("data");
		return daily;
	}
	
	public JSONObject getTodaysWeatherData() {
		JSONArray weatherDailyArray = getDailyWeatherData();
		return weatherDailyArray.getJSONObject(0);
	}
	
	public Weather convertJSONObjectToWeather(JSONObject weatherData, String userId, Long date) {
		Weather w = new Weather();
		w.setUserId(userId);
		w.setDate(date);
		w.setSunriseTime(weatherData.getLong("sunriseTime"));
		w.setSunsetTime(weatherData.getLong("sunsetTime"));
		w.setPrecipIntensity(weatherData.getDouble("precipIntensity"));
		w.setPrecipProbability(weatherData.getDouble("precipProbability"));
		w.setPrecipType(weatherData.getString("precipType"));
		w.setTemperature((weatherData.getInt("temperatureHigh") + weatherData.getInt("temperatureLow"))/2);
		w.setHumidity(weatherData.getDouble("humidity"));
		w.setCloudCover(weatherData.getDouble("cloudCover"));
		w.setVisibility(weatherData.getInt("visibility"));
		return w;
	}
	
	public static void main(String[] args) {
		WeatherData data = new WeatherData(42.3601, -71.0589);
		System.out.print(data.getTodaysWeatherData().toString());
	}

}
