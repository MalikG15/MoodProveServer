package moodprove.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import moodprove.application.MoodProveCronJob;
import moodprove.data.PastMoodRepository;
import moodprove.data.UserRepository;
import moodprove.data.WeatherRepository;
import moodprove.facebook.OAuthFacebook;
import moodprove.sleep.SleepData;
import moodprove.to.PastMood;
import moodprove.to.Sleep;
import moodprove.to.Social;
import moodprove.to.User;
import moodprove.to.Weather;
import moodprove.weather.WeatherData;

@RequestMapping("/predicted")
@RestController
public class PredictedMoodRestController {
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired 
	WeatherRepository weatherRepo;
	
	@Autowired
	MoodProveCronJob moodProveCronJob;
	
	@Autowired
	PastMoodRepository pastMoodRepo;
	
	SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd HH");  
	
	@RequestMapping("/getCheckInInterval")
	public String getCheckInInterval(@RequestParam("userid") String userId, 
			@RequestParam("timestamp") Long timestamp) {
		User u = userRepo.findByuserid(userId);
		if (u == null) return null;
		//System.out.println(u.getScheduledTimeOfPrediction());
		Long anHourInMilliseconds = Long.valueOf(3600000);
		Date dateCheckInHourAfter =  new Date(u.getNewUserNextCheckInTime() + anHourInMilliseconds);
		JSONObject response = new JSONObject();
		Date dateCheckIn =  new Date(u.getNewUserNextCheckInTime());
		// Adding a day for development purposes only
		// normally, users would not be able to check-in the day of
		Date dateUserCurrent = new Date((timestamp*1000) + 86400000);
		if (timestamp >= dateCheckInHourAfter.getTime()) {
			u.setNewUserNextCheckInTime(UserRestController.getNextDayCheckIn(u.getScheduledTimeOfPrediction()));
			u = userRepo.saveAndFlush(u);
		}
		
		// Do a check if they can check in now, if so send that it is time
		if (dateUserCurrent.after(dateCheckIn) && dateUserCurrent.before(dateCheckInHourAfter)) {
			response.put("checkInInterval", "Now");
			return response.toString();
		}
		
		
		response.put("checkInInterval", String.format("%s:00 - %s:00", dateFormatter.format(u.getNewUserNextCheckInTime()), 
				dateFormatter.format(dateCheckInHourAfter)));
		return response.toString();
	}
	
	@RequestMapping("/checkIn")
	public String checkIn(@RequestParam("userid") String userId, @RequestParam("timestamp") String timestampVal,
			@RequestParam("mood") String mood) {
		User u = userRepo.findByuserid(userId);
		Long timestamp = Long.valueOf(timestampVal);
		
		if (u == null) {
			return null;
		}
		
		Long anHourInMilliseconds = Long.valueOf(3600000);
		Date dateCheckInHourAfter =  new Date(u.getNewUserNextCheckInTime() + anHourInMilliseconds);
		Date dateCheckIn =  new Date(u.getNewUserNextCheckInTime());
		Date dateUserCurrent = new Date((timestamp*1000) + 86400000);
		
		if (dateUserCurrent.after(dateCheckIn) && dateUserCurrent.before(dateCheckInHourAfter)) {
			// Used to a day for another checkin or to subtract a day for weather info
			Long aDayInMilliseconds = Long.valueOf(86400000);
			Long dayBefore = timestamp - aDayInMilliseconds;
			
			// Retrieve data and set new past mood
			PastMood newPastMood = new PastMood();
			newPastMood.setUserid(userId);
			newPastMood.setDate(dayBefore);
			
			// Get user for retrieval of data
			User user = userRepo.findByuserid(userId);
			
			// Get weather
			if (user.getLatitude() != null && user.getLongitude() != null) {
				Double latitude = user.getLatitude();
				Double longitude = user.getLongitude();
				
				WeatherData weatherData = new WeatherData(longitude, latitude);
				Weather weather = weatherData.convertJSONObjectToWeather(weatherData.getTodaysWeatherDataTimeDependent(dayBefore), userId, dayBefore);
				weather = weatherRepo.saveAndFlush(weather);
				newPastMood.setWeatherId(weather.getWeatherId());
			}
			
			// Get social activity - using MoodProveCronJob code to prevent repeating code
			String fbToken = user.getFacebookAccessToken();
			if (fbToken != null && System.currentTimeMillis() > OAuthFacebook.getTokenExpirationTime(fbToken)) {
				Social social = moodProveCronJob.changeRecentSocialData(userId, null, dayBefore, fbToken);
				newPastMood.setSocialId(social.getSocialid());
			}
			else {
				newPastMood.setSocialId("");
			}
			
			// Get sleep activity - using MoodProveCronJob code to prevent repeating code
			
			// Adding getting events to sleepData block of code since token is shared for both
			// types of data
			SleepData sleepData = new SleepData(userId);
			if (sleepData.isTokenValid()) {
				Sleep sleepRecord = moodProveCronJob.changeRecentSleepData(userId, null, dayBefore, sleepData);
				newPastMood.setSleepid(sleepRecord.getSleepId());
				
				// Get events
				// timestamp == current time
				String events = moodProveCronJob.findEventsForDay(userId, dayBefore, timestamp);
				newPastMood.setEvents(events);
				
			}
			else {
				newPastMood.setSleepid("");
			}
			
			// Set mood/prediction
			// adding quotation marks since the ML algorithm reads
			// two words as two seperate words without the quotation marks
			// removing %20, which marks a space in the given mood
			mood = mood.replace("%20", " ");
			mood = "\"" + mood + "\"";
			newPastMood.setPrediction(mood);
			
			pastMoodRepo.saveAndFlush(newPastMood);
			
			u.setNewUserNextCheckInTime((u.getNewUserNextCheckInTime()) + aDayInMilliseconds);
			userRepo.saveAndFlush(u);
		
		}
		
		return "";
	}

}
