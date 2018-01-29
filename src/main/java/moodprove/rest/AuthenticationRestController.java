package moodprove.rest;

import java.io.IOException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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
			if (!OAuthGoogle.isGoogleTokenValid(userId)) {
				if (!user.googleOAuthConfirmationLinkExists()) {
					GoogleCalendarEvents calendarEvents = new GoogleCalendarEvents(userId);
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
		catch (IOException ex) {
			System.out.println(AuthenticationRestController.class.getName());
			System.out.println("Retrieving the Google access token failed.");
			return OAuthGoogle.getERROR_RETRIEVING_TOKEN().toString();
		}
 		catch (InterruptedException ex) {
 			System.out.println(OAuthGoogle.class.getName());
 			System.out.println("The main thread failed to sleep");
 			return OAuthGoogle.getERROR_RETRIEVING_TOKEN().toString();
 		}
	}
	
	
	
	
	@RequestMapping("/facebook")
	public String getFacebookToken(@RequestParam("code") String code) {
		JSONObject t = new JSONObject();
		try {
			t = OAuthFacebook.getFacebookUserToken(code);
		}
		catch (IOException ex) {
			System.out.println(AuthenticationRestController.class.getName());
			System.out.println("Retrieving the access token failed.");
			t.put("Error", "Retrieving the Facebook access token failed");
			return t.toString();
		}
		
		return String.format(OAuthFacebook.getOauthSuccessReponseHtml(), t.getString("access_token"));
	}

}
