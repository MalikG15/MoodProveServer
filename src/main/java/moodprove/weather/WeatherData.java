package moodprove.weather;

import org.json.JSONArray;
import org.json.JSONObject;

import moodprove.http.MoodProveHttp;
import moodprove.to.Weather;

public class WeatherData {
	
	private static final String STANDARD_WEATHER_API_CALL = "https://api.darksky.net/forecast/%s/%s,%s";
	
	private static final String TIME_DEPENDENT_WEATHER_API_CALL = "https://api.darksky.net/forecast/%s/%s,%f";
	
	private final Double longitude;
	
	private final Double latitude;
	
	public WeatherData(Double longitude, Double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public JSONArray getDailyWeatherData(boolean timeDependent, Long time) {
		String weatherResponse = "";
		if (!timeDependent) {
			weatherResponse = MoodProveHttp.executeGet(String.format(STANDARD_WEATHER_API_CALL, WeatherAPIClientInfo.WEATHER_API_KEY, latitude, longitude), "");
		}
		else {
			weatherResponse = MoodProveHttp.executeGet(String.format(TIME_DEPENDENT_WEATHER_API_CALL, WeatherAPIClientInfo.WEATHER_API_KEY, latitude, longitude, time), "");;
		}
		JSONObject weatherData = new JSONObject(weatherResponse);
		JSONArray daily = weatherData.getJSONObject("daily").getJSONArray("data");
		return daily;
	}
	
	public JSONObject getTodaysWeatherData() {
		JSONArray weatherDailyArray = getDailyWeatherData(false, null);
		System.out.println(weatherDailyArray.toString());
		return weatherDailyArray.getJSONObject(0);
	}
	
	public JSONObject getTodaysWeatherDataTimeDependent(Long time) {
		JSONArray weatherDailyArray = getDailyWeatherData(false, time);
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
		WeatherData data = new WeatherData(44.2600610, -88.3964900);
		//System.out.print(data.getTodaysWeatherData().toString());
		Long l = (long) 1525752799;
		System.out.println(data.getDailyWeatherData(true, l));
	}

}
