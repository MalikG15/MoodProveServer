package moodprove.rest;

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
		u.setNewUserCheckInTime(System.currentTimeMillis());
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
}
