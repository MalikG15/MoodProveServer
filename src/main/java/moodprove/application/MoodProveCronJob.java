package moodprove.application;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
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
			   // for sleep and facebook activity
			   setPredictedToPastMood(u.getUserid(), oldestPredicted, currentTime - (86400*1000));
		   }
		   
		  /*for (int x = 0; x < 7; x++) {
			  PredictedMood newMood = new PredictedMood();
			  List<Event> events = findEventsForDay(userId, currentTime)
		  }
		   
		   // get event rating from current time to 24 hours later, for 7 days
		   List<Integer> getRatings = findEventsForNext7Days(u.getUserid(), currentTime);
		   // get weather from current time to 24 hours later, for 7 days
		   
		   // get average social for each day
		   
		   // get average sleep for each day*/
		   
		   
	   }
	   
	}
	
	private static String convertTimeMillisToHour(Long timeMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
	

		int mHour = calendar.get(Calendar.HOUR);
		return String.format("%d:00", mHour);
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
				
		/*// Retrieving again for the ID
		sleepRecord = sleepRepository.findBydate(twentyFourHoursBefore);*/
				
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
		for (int x = 0; x < 7; x++) {
			PredictedMood newMood = new PredictedMood();
			newMood.setUserid(userId);
			newMood.setDate(start);
			newMood.setEvents(findEventsForDay(userId, start, start + twentyFourHoursInMilliseconds));
			// Getting weather information for that day
			newMood.setWeatherId(setWeatherDataForDay(weatherData, array.getJSONObject(x), userId, start));
			// Getting averages for facebook activity for this day
			newMood.setSocialId(setSocialActivityForDay(userId, start));
			newMood.setSleepid(setSleepDataForDay(userId, start));
			
			predictedMoodRepository.saveAndFlush(newMood);
			start += twentyFourHoursInMilliseconds;
		}
	} 
	
	// Event ratings should be 1 - 10
	// 0 should indicate no events happening at all for that day
	public String findEventsForDay(String userId, Long start, Long end) {
		GoogleCalendarEvents calendarEvents = new GoogleCalendarEvents(userId);
		List<com.google.api.services.calendar.model.Event> events =
				calendarEvents.getEventsWithinTimeFrame(start, end);
		StringBuilder eventIds = new StringBuilder();
		for (com.google.api.services.calendar.model.Event e : events) {
			eventIds.append(e.getId());
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
	
	public static void main(String[] args) {
		System.out.println(convertTimeMillisToDay(System.currentTimeMillis()));
	}
}
