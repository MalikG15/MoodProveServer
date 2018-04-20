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
		System.out.println(weatherDailyArray.toString());
		return weatherDailyArray.getJSONObject(0);
	}
	
	public Weather convertJSONObjectToWeather(JSONObject weatherData, String userId, Long date) {
		Weather w = new Weather();
		w.setUserId(userId);
		w.setDate(date);
		if (weatherData.has("sunriseTime")) w.setSunriseTime(weatherData.getLong("sunriseTime"));
		if (weatherData.has("sunsetTime")) w.setSunsetTime(weatherData.getLong("sunsetTime"));
		if (weatherData.has("precipIntensity")) w.setPrecipIntensity(weatherData.getDouble("precipIntensity"));
		if (weatherData.has("precipProbability")) w.setPrecipProbability(weatherData.getDouble("precipProbability"));
		if (weatherData.has("temperatureHigh") && weatherData.has("temperatureLow")) {
			w.setTemperature((weatherData.getInt("temperatureHigh") + weatherData.getInt("temperatureLow"))/2);
		}
		if (weatherData.has("humidity")) w.setHumidity(weatherData.getDouble("humidity"));
		if (weatherData.has("cloudCover")) w.setCloudCover(weatherData.getDouble("cloudCover"));
		if (weatherData.has("visibility")) w.setVisibility(weatherData.getInt("visibility"));
		return w;
	}
	
	public static void main(String[] args) {
		WeatherData data = new WeatherData(44.259839252451, -88.3965381816552);
		//System.out.print(data.getTodaysWeatherData().toString());
		Long l = (long) 12122;
		data.convertJSONObjectToWeather(data.getTodaysWeatherData(), "1", l);
	}

}
