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
	public String checkIn(@RequestParam("userid") String userId, @RequestParam("timestamp") Long timestamp,
			@RequestParam("mood") String mood) {
		User u = userRepo.findByuserid(userId);
		if (u == null) return null;
		Long anHourInMilliseconds = Long.valueOf(3600000);
		Date dateCheckInHourAfter =  new Date(u.getNewUserNextCheckInTime() + anHourInMilliseconds);
		if (timestamp >= dateCheckInHourAfter.getTime()) {
			u.setNewUserNextCheckInTime(UserRestController.getNextDayCheckIn(u.getScheduledTimeOfPrediction()));
			u = userRepo.saveAndFlush(u);
		}
		Date dateCheckIn =  new Date(u.getNewUserNextCheckInTime());
		Date dateUserCurrent = new Date(timestamp);
		
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
			Double latitude = user.getLatitude();
			Double longitude = user.getLongitude();
			
			WeatherData weatherData = new WeatherData(longitude, latitude);
			Weather weather = weatherData.convertJSONObjectToWeather(weatherData.getTodaysWeatherDataTimeDependent(dayBefore), userId, dayBefore);
			weather = weatherRepo.saveAndFlush(weather);
			newPastMood.setWeatherId(weather.getWeatherId());
			
			// Get social activity - using MoodProveCronJob code to prevent repeating code
			String fbToken = user.getFacebookAccessToken();
			if (System.currentTimeMillis() > OAuthFacebook.getTokenExpirationTime(fbToken)) {
				Social social = moodProveCronJob.changeRecentSocialData(userId, null, dayBefore, fbToken);
				newPastMood.setSocialId(social.getSocialid());
			}
			else {
				newPastMood.setSocialId("");
			}
			
			// Get sleep activity - using MoodProveCronJob code to prevent repeating code
			SleepData sleepData = new SleepData(userId);
			if (sleepData.isTokenValid()) {
				Sleep sleepRecord = moodProveCronJob.changeRecentSleepData(userId, null, dayBefore, sleepData);
				newPastMood.setSleepid(sleepRecord.getSleepId());
			}
			else {
				newPastMood.setSleepid("");
			}
			
			// Get events
			
			// timestamp == current time
			String events = moodProveCronJob.findEventsForDay(userId, dayBefore, timestamp);
			newPastMood.setEvents(events);
			
			// Set mood/prediction
			newPastMood.setPrediction(mood);
			
			pastMoodRepo.saveAndFlush(newPastMood);
			
			u.setNewUserNextCheckInTime(timestamp + aDayInMilliseconds);
			userRepo.saveAndFlush(u);
		}
		
		return "";
	}

}
