package moodprove.application;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

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

@Service
public class MoodProveCronJob extends TimerTask {
	
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private PastMoodRepository pastMoodRepo;
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
	   List<User> users = userRepo.findAll()
			   .stream()
			   .filter(u -> hour.equals(u.getScheduledTimeOfPrediction()))
			   .collect(Collectors.toList());
	   for (User u : users) {
		   List<PastMood> moodHistory = pastMoodRepo.findAllByuserid(u.getUserid());
		   if (moodHistory.size() < 7) continue;
		   PredictedMood oldestPredicted = predictedMoodRepository.findBydateLessThan(System.currentTimeMillis());
		   if (oldestPredicted != null) {
			   // going back 24 hours to retrieve data 
			   // for sleep and facebook activity
			   setPredictedToPastMood(u.getUserid(), oldestPredicted, currentTime - (86400*1000));
		   }
		   
		   // get event rating from current time to 24 hours later, for 7 days
		   List<Integer> getRatings = findEventsForNext7Days(u.getUserid(), currentTime);
		   
		   // get weather from current time to 24 hours later, for 7 days
		   
		   // get average social for each day
		   
		   // get average sleep for each day
	   }
	   
	}
	
	public static String convertTimeMillisToHour(Long timeMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
	

		int mHour = calendar.get(Calendar.HOUR);
		return String.format("%d:00", mHour);
	}
	
	public static String convertTimeMillisToDay(Long timeMillis) {
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
		String fbToken = userRepo.findfacebookAccessTokenByuserid(userId);
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
		pastMoodRepo.saveAndFlush(newPastMood);
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
	
	// Event ratings should be 1 - 10
	// 0 should indicate no events happening at all for that day
	public List<Integer> findEventsForNext7Days(String userId, Long currentTime) {
		GoogleCalendarEvents calendarEvents = new GoogleCalendarEvents(userId);
		Long twentyFourHoursInMilliseconds = Long.valueOf(86400*1000);
		List<Integer> ratingsForNext7Days = new ArrayList<>();
		for (int x = 0; x < 7; x++) {
			int totalRatingForDay = 0;
			List<com.google.api.services.calendar.model.Event> events = 
					calendarEvents.getEventsWithinTimeFrame(currentTime, currentTime + twentyFourHoursInMilliseconds);
			for (com.google.api.services.calendar.model.Event e : events) {
				Event event = eventRepository.findByeventid(e.getId());
				if (event != null) {
					totalRatingForDay += event.getRating();
				}
				// Adding 5 as that is the base addition, if the 
				// event has not been rated yet
				else totalRatingForDay += 5;
			}
			ratingsForNext7Days.add(totalRatingForDay);
			currentTime += twentyFourHoursInMilliseconds;
		}
		
		
	}
	
	public static void main(String[] args) {
		System.out.println(convertTimeMillisToDay(System.currentTimeMillis()));
	}
}
