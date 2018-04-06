package moodprove.application;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;

import moodprove.data.*;
import moodprove.facebook.FacebookUserData;
import moodprove.facebook.OAuthFacebook;
import moodprove.google.GoogleCalendarEvents;
import moodprove.predict.MoodPredictor;
import moodprove.sleep.SleepData;
import moodprove.to.*;
import moodprove.weather.WeatherData;

@Service
public class MoodProveCronJob extends TimerTask {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PastMoodRepository pastMoodRepository;
	@Autowired
	private PredictedMoodRepository predictedMoodRepository;
	@Autowired
	private SleepRepository sleepRepository;
	@Autowired
	private SocialRepository socialRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private WeatherRepository weatherRepository;
	
	private static final String[] DAYS_OF_WEEK = new String[] {"Sun", "Mon", "Tu", "Wed", "Thu", "Fri", "Sat"};

	@Override
	public void run() {
	   Long currentTime = System.currentTimeMillis();
	   String hour = convertTimeMillisToHour(currentTime);
	   List<User> users = userRepository.findAll()
			   .stream()
			   .filter(u -> hour.equals(u.getScheduledTimeOfPrediction()))
			   .collect(Collectors.toList());
	   for (User u : users) {
		   List<PastMood> moodHistory = pastMoodRepository.findAllByuserid(u.getUserid());
		   if (moodHistory.size() < 7) continue;
		   PredictedMood oldestPredicted = predictedMoodRepository.findBydateLessThan(System.currentTimeMillis());
		   if (oldestPredicted != null) {
			   // going back 24 hours to retrieve data 
			   // for most recent sleep and facebook activity
			   // and replace it from averages to improve data
			   setPredictedToPastMood(u.getUserid(), oldestPredicted, currentTime - (86400*1000));
		   }
		   predictedMoodRepository.deleteAllByuserid(u.getUserid());
		   setPredictionDataForNext7Days(u, currentTime);
		   makePredictionForNext7Days(u);
	   }
	   
	}
	
	private static String convertTimeMillisToHour(Long timeMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		int hour = calendar.get(Calendar.HOUR);
		//String pmOrAm = (calendar.get(Calendar.AM_PM) == 1) ? "PM" : "AM";
		return String.format("%d:00", hour);
	}
	
	private static String convertTimeMillisToDay(Long timeMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		int mDay = calendar.get(Calendar.DAY_OF_WEEK);
		return DAYS_OF_WEEK[mDay - 1];
	}
	
	public void setPredictedToPastMood(String userId, PredictedMood oldestPredicted, Long twentyFourHoursBefore) {
		PastMood newPastMood = new PastMood();
		newPastMood.setUserid(oldestPredicted.getUserid());
		newPastMood.setDate(oldestPredicted.getDate());
		newPastMood.setEvents(oldestPredicted.getEvents());
		newPastMood.setPrediction(oldestPredicted.getPrediction());
		
		
		// Check token validity before retrieving data
		SleepData sleepData = new SleepData(userId);
		Sleep sleepRecord = null;
		if (sleepData.isTokenValid()) {
			sleepRecord = changeRecentSleepData(userId, oldestPredicted, twentyFourHoursBefore, sleepData);
		}
		
		// Check token validity before retrieving data 
		String fbToken = userRepository.findByuserid(userId).getFacebookAccessToken();
		Social socialRecord = null;
		if (System.currentTimeMillis() < OAuthFacebook.getTokenExpirationTime(fbToken)) {
			socialRecord = changeRecentSocialData(userId, oldestPredicted, twentyFourHoursBefore, fbToken);
		}

		// If data is null, then we do not change from averages
		if (sleepRecord != null) newPastMood.setSleepid(sleepRecord.getSleepId());
		else newPastMood.setSocialId(oldestPredicted.getSleepid());
		
		// If data is null, then we do not change from averages
		if (socialRecord != null) newPastMood.setSocialId(socialRecord.getSocialid());
		else newPastMood.setSocialId(oldestPredicted.getSocialId());
		
		newPastMood.setWeatherId(oldestPredicted.getWeatherId());
		pastMoodRepository.saveAndFlush(newPastMood);
	}
	
	public Sleep changeRecentSleepData(String userId, PredictedMood oldestPredicted, Long twentyFourHoursBefore, SleepData data) {
		// Retrieving most recent sleepRecord
		Sleep sleepRecord = data.getSleepData(twentyFourHoursBefore);
		sleepRecord.setUserid(userId);
		sleepRecord.setDay(convertTimeMillisToDay(twentyFourHoursBefore));
		sleepRecord.setDate(twentyFourHoursBefore);
		sleepRecord = sleepRepository.saveAndFlush(sleepRecord);
				
		// Delete the predicted sleep because it is just averages
		sleepRepository.deleteBysleepid(oldestPredicted.getSleepid());
		
		return sleepRecord;
	}	
	
	public Social changeRecentSocialData(String userId, PredictedMood oldestPredicted, Long twentyFourHoursBefore, String fbToken) {
		// Retrieving most recent social activity from Facebook
		FacebookClient fbClient = new DefaultFacebookClient(fbToken, Version.LATEST);
		FacebookUserData fbData = new FacebookUserData(fbClient, new Date(twentyFourHoursBefore));
		Social socialRecord = new Social();
		socialRecord.setUserid(userId);
		socialRecord.setFacebookEvents(fbData.getFbDataByType("/events"));
		socialRecord.setFacebookLikes(fbData.getFbDataByType("/likes"));
		socialRecord.setFacebookTimeLineUpdates(fbData.getFbDataByType("/feed"));
		socialRecord.setDay(convertTimeMillisToDay(twentyFourHoursBefore));
		// Saving it gener
		socialRecord = socialRepository.saveAndFlush(socialRecord);
		
		// Delete the predicted social because it is just averages
		socialRepository.deleteBysocialid(oldestPredicted.getSocialId());
		
		return socialRecord;
	}
	
	public void setPredictionDataForNext7Days(User user, Long start) {
		String userId = user.getUserid();
		Long twentyFourHoursInMilliseconds = Long.valueOf(86400*1000);
		WeatherData weatherData = new WeatherData(user.getLongitude(), user.getLatitude());
		JSONArray array = weatherData.getDailyWeatherData();
		for (int day = 0; day < 7; day++) {
			PredictedMood newMood = new PredictedMood();
			newMood.setUserid(userId);
			newMood.setDate(start);
			newMood.setEvents(findEventsForDay(userId, start, start + twentyFourHoursInMilliseconds));
			// Getting weather information for that day
			newMood.setWeatherId(setWeatherDataForDay(weatherData, array.getJSONObject(day), userId, start));
			// Getting averages for facebook activity for this day
			newMood.setSocialId(setSocialActivityForDay(userId, start));
			newMood.setSleepid(setSleepDataForDay(userId, start));
			
			predictedMoodRepository.saveAndFlush(newMood);
			start += twentyFourHoursInMilliseconds;
		}
	} 
	
	public String findEventsForDay(String userId, Long start, Long end) {
		GoogleCalendarEvents calendarEvents = new GoogleCalendarEvents(userId);
		List<com.google.api.services.calendar.model.Event> events =
				calendarEvents.getEventsWithinTimeFrame(start, end);
		StringBuilder eventIds = new StringBuilder();
		for (com.google.api.services.calendar.model.Event e : events) {
			Event event = eventRepository.findByeventid(e.getId());
			if (event != null) {
				eventIds.append(e.getId() + ",");
			}
		}
		return eventIds.toString();
	}
	
	public String setWeatherDataForDay(WeatherData weatherData, JSONObject data, String userId, Long date) {
		Weather w = weatherData.convertJSONObjectToWeather(data, userId, date);
		w = weatherRepository.saveAndFlush(w);
		return w.getWeatherId();
	}
	
	public String setSocialActivityForDay(String userId, Long date) {
		Social newSocial = new Social();
		newSocial.setUserid(userId);
		newSocial.setDate(date);
		String day = convertTimeMillisToDay(date);
		newSocial.setDay(day);
		List<Social> socialActivityForDay = socialRepository.findByday(day);
		
		int totalFacebookEvents = 0, totalFacebookLikes = 0, totalFacebookTimelineUpdates = 0;
		for (Social s : socialActivityForDay) {
			totalFacebookEvents += s.getFacebookEvents();
			totalFacebookLikes += s.getFacebookLikes();
			totalFacebookTimelineUpdates += s.getFacebookTimeLineUpdates();
		}
		
		newSocial.setFacebookEvents(totalFacebookEvents / socialActivityForDay.size());
		newSocial.setFacebookLikes(totalFacebookLikes / socialActivityForDay.size());
		newSocial.setFacebookTimeLineUpdates(totalFacebookTimelineUpdates / socialActivityForDay.size());
		
		newSocial = socialRepository.saveAndFlush(newSocial);
		return newSocial.getSocialid();
	}
	
	public String setSleepDataForDay(String userId, Long date) {
		Sleep newSleep = new Sleep();
		newSleep.setUserid(userId);
		newSleep.setDate(date);
		String day = convertTimeMillisToDay(date);
		newSleep.setDay(day);
		List<Sleep> sleepDataForDay = sleepRepository.findByday(day);
		
		int totalSleepLength = 0, totalSleepCycles = 0, totalSleepNoiseLevel = 0;
		for (Sleep s : sleepDataForDay) {
			totalSleepLength += s.getSleeplength();
			totalSleepCycles += s.getSleepCyles();
			totalSleepNoiseLevel += s.getNoiseLevel();
		}
		
		newSleep.setSleeplength(totalSleepLength / sleepDataForDay.size());
		newSleep.setSleepCyles(totalSleepCycles / sleepDataForDay.size());
		newSleep.setNoiseLevel(totalSleepNoiseLevel / sleepDataForDay.size());
		
		newSleep = sleepRepository.saveAndFlush(newSleep);
		return newSleep.getSleepId();
	}
	
	public void makePredictionForNext7Days(User user) {
		String userId = user.getUserid();
		List<PastMood> pastMood = pastMoodRepository.findAllByuserid(userId);
		List<PredictedMood> predictedMood = predictedMoodRepository.findAllByuserid(userId);
		MoodPredictor predictor = new MoodPredictor();
		predictor.writeHeadersToMoodPast();
		predictor.writeHeadersToMoodPredict();
		for (PastMood mood : pastMood) {
			List<Event> events = new ArrayList<>();
			for (String s : mood.getEvents().split(",")) {
				events.add(eventRepository.findByeventid(s));
			}
			predictor.writePredictiveDataToPastMood(events, 
					sleepRepository.findBysleepid(mood.getSleepid()), 
					socialRepository.findBysocialid(mood.getSocialId()), 
					weatherRepository.findByweatherid(mood.getWeatherId()), 
					mood);
		}
		
		for (PredictedMood mood : predictedMood) {
			List<Event> events = new ArrayList<>();
			for (String s : mood.getEvents().split(",")) {
				events.add(eventRepository.findByeventid(s));
			}
			predictor.writePredictiveDataToPredictMood(events, 
					sleepRepository.findBysleepid(mood.getSleepid()), 
					socialRepository.findBysocialid(mood.getSocialId()), 
					weatherRepository.findByweatherid(mood.getWeatherId()), 
					mood);
		}
		predictor.closeWriters();
		
		JSONArray results = predictor.predict();
		// THE MOST IMPORTANT LINE OF CODE IN
		// THIS ENTIRE PROJECT ^^^^^
		for (int index = 0; index < predictedMood.size(); index++) {
			PredictedMood mood = predictedMood.get(index);
			mood.setPrediction(results.get(index).toString());
			predictedMoodRepository.saveAndFlush(mood);
		}
		
	}
	
	public static void main(String[] args) {
		System.out.println(convertTimeMillisToHour(System.currentTimeMillis()));
	}
}
