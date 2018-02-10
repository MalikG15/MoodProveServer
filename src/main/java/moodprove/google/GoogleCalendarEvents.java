package moodprove.google;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import moodprove.google.OAuthGoogle;

public class GoogleCalendarEvents {
	
	public static final String OAUTH_GOOGLE_CALENDAR_SUCCESS_REPONSE_HTML = "<!DOCTYPE html>"
			+ "<html>"
			+ "<head>"
			+ "<style>"
			+ "div { text-align: center; font-size: 50px;  font-family: \"Times New Roman\", Times, serif;}"
			+ "</style>"
			+ "</head>"
			+ "<body>"
			+ "<div><a href=\"lawrence.moodprovemacapp://%s\"/> Click Here to Finish Authorizing MoodProve</a></div>"
			+ "</body>"
			+ "</html>";
    
    // Adding a string for userinfo.email so that the token will be compatible with
    // SleepCloud API Storage
    private static final List<String> SCOPES =
            Arrays.asList(CalendarScopes.CALENDAR_READONLY, "https://www.googleapis.com/auth/userinfo.email");

    private static final String CREDENTIAL_FILE_NAME = "src/main/java/moodprove/google/google_oauth_client_secret.json";
    
    private final String userId;
    
    private OAuthGoogle oauthGoogle;
    
    public GoogleCalendarEvents(String userId) {
    	this.userId = userId;
    	try {
    		this.oauthGoogle = new OAuthGoogle(SCOPES, CREDENTIAL_FILE_NAME);
    	}
    	catch (IOException ex) {
    		System.out.println(GoogleCalendarEvents.class.getName());
 			System.out.println("Could not create an oauthGoogle instance.");
    	}
    }
    
    public boolean isTokenValid() {
    	try {
    		oauthGoogle.isGoogleTokenValid(userId);
    	}
    	catch (IOException ex) {
    		System.out.println(GoogleCalendarEvents.class.getName());
 			System.out.println("Could not check if token is valid.");
    	}
    	return false;
    }
	
    // The credentials used for calendar are compatible with SleepCloud API storage
	public Calendar buildCalendar() {
		try {
			Credential credential = oauthGoogle.authorize(userId);
			return new Calendar
					.Builder(oauthGoogle.getHTTP_TRANSPORT(), oauthGoogle.getJsonFactory(), credential)
					.setApplicationName(oauthGoogle.getApplicationName())
					.build();
		}
		catch (IOException ex) {
			System.out.println(GoogleCalendarEvents.class.getName());
    		System.out.println("There was an error getting credential for Google Calendar Events");
		}
		
		return null;
	}
	
	// Starting a new thread that starts a new server
	// that listens and waits for the user to authenticate
	public void startGoogleCalendarAuthenticationThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				buildCalendar();
			}
		}).start();
	}
	
	public List<Event> getEventsWithinTimeFrame(Long start, Long end) {
		Calendar calendar = buildCalendar();
	
		try {
			Events events = calendar.events().list("primary")
	            .setMaxResults(10)
	            .setTimeMin(new DateTime(start))
	            .setTimeMax(new DateTime(end))
	            .setOrderBy("startTime")
	            .setSingleEvents(true)
	            .execute();
			
			return events.getItems();
		}
		catch (IOException ex) {
			System.out.println(GoogleCalendarEvents.class.getName());
    		System.out.println("There was an error getting events.");
		}
		
		return null;
	}

}
