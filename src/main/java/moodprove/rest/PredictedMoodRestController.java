package moodprove.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import moodprove.data.UserRepository;
import moodprove.to.User;

@RequestMapping("/predicted")
@RestController
public class PredictedMoodRestController {
	
	@Autowired
	UserRepository userRepo;
	
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
			@RequestParam("mood") Integer mood) {
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
			// Retrieve data
			
			Long aDayInMilliseconds = Long.valueOf(86400000);
			u.setNewUserNextCheckInTime(timestamp + aDayInMilliseconds);
			userRepo.saveAndFlush(u);
		}
		
		return "";
	}

}
