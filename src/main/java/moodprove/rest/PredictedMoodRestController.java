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

@RestController("/predictedmood")
public class PredictedMoodRestController {
	
	@Autowired
	UserRepository userRepo;
	
	DateFormat dateFormatter = new SimpleDateFormat("MMM dd HH");  
	
	@RequestMapping("/getCheckInInterval")
	public String getCheckInInterval(@RequestParam("userid") String userId) {
		User u = userRepo.findByuserid(userId);
		if (u == null) return null;
		Long dayInMilliseconds = Long.valueOf(86400000);
		Long anHourInMilliseconds = Long.valueOf(3600000);
		Date dateCheckInHalfHourAfter =  new Date(u.getNewUserCheckInTime() + dayInMilliseconds + anHourInMilliseconds);
		JSONObject response = new JSONObject();
		
		response.put("checkInInterval", String.format("%s:00 - %s:00", dateFormatter.format(u.getNewUserCheckInTime()), 
				dateFormatter.format(dateCheckInHalfHourAfter)));
		return response.toString();
	}
	
	@RequestMapping("/checkIn")
	public String checkIn(@RequestParam("userid") String userId, @RequestParam("timestamp") Long timestamp,
			@RequestParam("mood") Integer mood) {
		User u = userRepo.findByuserid(userId);
		if (u == null) return null;
		Long dayInMilliseconds = Long.valueOf(86400000);
		Long anHourInMilliseconds = Long.valueOf(3600000);
		Date dateCheckInHourAfter =  new Date(u.getNewUserCheckInTime() + dayInMilliseconds + anHourInMilliseconds);
		Date dateCheckIn =  new Date(u.getNewUserCheckInTime() + dayInMilliseconds);
		Date dateUserCurrent = new Date(timestamp);
		
		if (dateUserCurrent.after(dateCheckIn) && dateUserCurrent.before(dateCheckInHourAfter)) {
			// Retrieve data
			u.setNewUserCheckInTime(timestamp);
			userRepo.saveAndFlush(u);
		}
		
		return "";
	}

}
