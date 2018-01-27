package moodprove.rest;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import moodprove.data.UserRepository;
import moodprove.google.GoogleCalendarEvents;
import moodprove.google.GoogleCalendarEventsOld;
import moodprove.google.OAuthGoogle;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.CrossOrigin;


import com.restfb.DefaultWebRequestor;
import com.restfb.WebRequestor;

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
	public String getOAuthGoogleLink() {	
		try {
			System.out.println("isGoogleTokenValid: " + OAuthGoogle.isGoogleTokenValid("user"));
			if (!OAuthGoogle.isGoogleTokenValid("user")) {
				if (!OAuthGoogle.googleOAuthConfirmationLinkExists()) {
					GoogleCalendarEvents.startGoogleCalendarAuthenticationThread();
				}
				String linkToAuthenticate = OAuthGoogle.readGoogleOAuthConfirmationLinkFile();
				JSONObject responseObj = new JSONObject();
				responseObj.put("Response", linkToAuthenticate);		
				return responseObj.toString();
			}
			OAuthGoogle.deleteLinkFile();
			return OAuthGoogle.getAUTHENTICATION_STILL_VALID_RESPONSE().toString();
		}
		catch (IOException ex) {
			System.out.println(AuthenticationRestController.class.getName());
			System.out.println("Retrieving the Google access token failed.");
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
