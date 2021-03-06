package moodprove.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import moodprove.data.UserRepository;
import moodprove.google.GoogleCalendarEvents;
import moodprove.google.OAuthGoogle;
import moodprove.to.User;
import moodprove.to.UserSQLAssist;
import moodprove.facebook.OAuthFacebook;

/**
 * @author Malik Graham
 */

@RequestMapping("/auth")
@RestController
public class AuthenticationRestController {
	
	@Autowired
	UserRepository userRepo;

	
	@RequestMapping("/google")
	public String getOAuthGoogleLink(@RequestParam("userid") String userId) {	
		User user = userRepo.findByuserid(userId);
		try {
			GoogleCalendarEvents calendarEvents = new GoogleCalendarEvents(userId);
			if (!calendarEvents.isTokenValid()) {
				if (user.getGoogleOAuthLink() == null || user.getGoogleOAuthLink().isEmpty()) {
					calendarEvents.startGoogleCalendarAuthenticationThread();
					Thread.sleep(500);
				}
				String linkToAuthenticate = UserSQLAssist.getLink(userId);
				JSONObject responseObj = new JSONObject();
				responseObj.put("Response", linkToAuthenticate);		
				return responseObj.toString();
			}
			return OAuthGoogle.getAUTHENTICATION_STILL_VALID_RESPONSE().toString();
		}
 		catch (InterruptedException ex) {
 			System.out.println(OAuthGoogle.class.getName());
 			System.out.println("The main thread failed to sleep");
 			return OAuthGoogle.getERROR_RETRIEVING_TOKEN().toString();
 		}
	}
	
	@RequestMapping("/checkAuthWithGoogle")
	public String checkAuthWithGoogle(@RequestParam("userid") String userId) {
		GoogleCalendarEvents calendarEvents = new GoogleCalendarEvents(userId);
		JSONObject authCheckResult = new JSONObject();
		authCheckResult.put("Result", (calendarEvents.isTokenValid() ? "true" : "false"));
		return authCheckResult.toString();
	}
	
	@RequestMapping("/checkAuthWithFacebook")
	public String checkAuthWithFacebook(@RequestParam("userid") String userId) {
		User u = userRepo.findByuserid(userId);
		JSONObject authCheckResult = new JSONObject();
		authCheckResult.put("Result", ((u.getFacebookAccessToken() != null) ? "true" : "false"));
		return authCheckResult.toString();
	}
	
	@RequestMapping("/facebook/saveToken")
	public String saveFacebookToken(@RequestParam("userid") String userId, @RequestParam("token") String token) {
		User u = userRepo.findByuserid(userId);
		String fbExtendedAccessToken = OAuthFacebook.getExtendedAccessToken(token);
		Long fbExpirationTime = OAuthFacebook.getTokenExpirationTime(fbExtendedAccessToken);
		u.setFacebookAccessToken(fbExtendedAccessToken);
		u.setFacebookTokenExpire(fbExpirationTime);
		userRepo.saveAndFlush(u);
		return "{\"result\": \"success\"}";
	}
	
	@RequestMapping(value = "/redirect", method = RequestMethod.GET)
	public void redirect(HttpServletResponse http, @RequestParam("code") String code) {
		try {
			http.sendRedirect("Lawrence.MoodProveMacApp://?code=" + code);
		}
		catch (Exception ex) {
			
		}
	}
}
