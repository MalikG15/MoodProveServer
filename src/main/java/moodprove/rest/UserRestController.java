package moodprove.rest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restfb.json.JsonObject;

import moodprove.data.UserRepository;
import moodprove.to.User;

/**
 * @author Malik Graham
 */

@RequestMapping("/user")
@RestController
public class UserRestController {
	
	@Autowired
	UserRepository userRepo;

	
	@RequestMapping("/create")
	public String createNewUser(@RequestParam("email") String email, @RequestParam("password") String password) {
		User newUser = new User();
		newUser.setEmail(email);
		newUser.setPassword(password);
		userRepo.saveAndFlush(newUser);
		return (userRepo.findByEmailAndPassword(email, password)).getUserid();
	}
	
	@RequestMapping("/location")
	public String addLatitudeAndLongitude(@RequestParam("userid") String userId, @RequestParam("latitude") Double latitude, 
			@RequestParam("longitude") Double longitude) {
		User u = userRepo.findByuserid(userId);
		u.setLatitude(latitude);
		u.setLongitude(longitude);
		userRepo.saveAndFlush(u);
		return "{\"result\": \"success\"}";
	}
	
	@RequestMapping("/add")
	public String addNewUser(@RequestParam("name") String name, @RequestParam("email") String email, 
			@RequestParam("password") String password, @RequestParam("timeOfCheckIn") String timeOfCheckIn) {
		User u = new User();
		u.setName(name);
		u.setEmail(email);
		u.setPassword(password);
		u.setScheduledTimeOfPrediction(timeOfCheckIn);
		// Calling auxiliary function
		u.setNewUserCheckInTime(getNextDayCheckIn(timeOfCheckIn));
		u = userRepo.saveAndFlush(u);
		return String.format("{\"result\": \"%s\"}", u.getUserid());
	}
	
	@RequestMapping("/login")
	public String login(@RequestParam("email") String email, @RequestParam("password") String password) {
		User u = userRepo.findByEmailAndPassword(email, password);
		if (u == null) return null;
		JSONObject obj = new JSONObject();
		obj.put("userid", u.getUserid());
		obj.put("name", u.getName());
		return obj.toString();
	}
	
	// Could possible find a better location for this method
	public static Long getNextDayCheckIn(String timeOfCheckIn) {
		// Get a string representation of current time plus 24 hours
		Long twentyFourHoursLaterFromNow = System.currentTimeMillis() + Long.valueOf(86400000);
		Date twentyFourHoursLaterFromNowDate = new Date(twentyFourHoursLaterFromNow);
				
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat formatter6 = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		String nextCheckInDay = df.format(twentyFourHoursLaterFromNowDate);
		// The point of 'nextCheckInDay' is to get the next day of when they sign in
		// as that is the earliest point at which they can check in.
		// However, the time now plus 24 hours may not be the actual time of designated
		// check in so I date the next day data and set the next day string to the time
		// given. That way I get the correct time of check in.
		// The timeOfCheckIn handed in has to be changed as it comes with AM and PM, and
		// we don't need that, then we place it in the nextday string;
		timeOfCheckIn = timeOfCheckIn.substring(0, timeOfCheckIn.length() - 3);
		Date nextCheckInTime = new Date();
		try {
			System.out.println(String.format(nextCheckInDay + " %s", timeOfCheckIn));
			nextCheckInTime = formatter6.parse(String.format(nextCheckInDay + " %s", timeOfCheckIn));
		}
		catch (ParseException ex) {
			System.out.println(UserRestController.class.getName());
			System.out.println("Could not parse the given string to get next check in");
		}
		
		return nextCheckInTime.getTime();
	}
	
	public static void main(String[] args) {
		SimpleDateFormat formatter6 = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		//System.out.println(getNextDayCheckIn("5:00 AM"));
		Date date = new Date(getNextDayCheckIn("5:00 AM"));
		System.out.println(formatter6.format(date));
	}
}
