package moodprove.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
		newUser.setUsername(email);
		newUser.setPassword(password);
		userRepo.saveAndFlush(newUser);
		return (userRepo.findByEmailAndPassword(email, password)).getUserid();
	}
}
