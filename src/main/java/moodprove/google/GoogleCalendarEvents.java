package moodprove.google;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import moodprove.google.OAuthGoogle;
import moodprove.rest.AuthenticationRestController;

public class GoogleCalendarEvents {
	
	private static final String OAUTH_GOOGLE_CALENDAR_SUCCESS_REPONSE_HTML = "<!DOCTYPE html>"
			+ "<html>"
			+ "<head>"
			+ "<style>"
			+ "div { text-align: center; font-size: 50px;  font-family: \"Times New Roman\", Times, serif;}"
			+ "</style>"
			+ "</head>"
			+ "<body>"
			+ "<div><a href=\"lawrence.moodprovemacapp://%s\"/> Click Here to Finish Authorizing MoodProve to use Google Calendar Data </a></div>"
			+ "</body>"
			+ "</html>";
    
    // Adding a string for userinfo.email so that the token will be compatible with
    // SleepCloud API Storage
    private static final List<String> SCOPES =
            Arrays.asList(CalendarScopes.CALENDAR_READONLY, "https://www.googleapis.com/auth/userinfo.email");

    private static final String CREDENTIAL_FILE_NAME = "src/main/java/moodprove/google/google_oauth_client_secret.json";
	
    // The credentials used for calendar are compatible with SleepCloud API storage
	public static Calendar buildCalendar() {
		try {
			OAuthGoogle oauthGoogle = new OAuthGoogle(SCOPES, CREDENTIAL_FILE_NAME);
			Credential credential = oauthGoogle.authorize();
			return new Calendar
					.Builder(oauthGoogle.getHTTP_TRANSPORT(), oauthGoogle.getJsonFactory(), credential)
					.setApplicationName(oauthGoogle.getApplicationName())
					.build();
		}
		catch (IOException ex) {
			System.out.println(GoogleCalendarEvents.class.getName());
    		System.out.println("There was getting credential for Google Calendar Events");
		}
		
		return null;
	}
	
	// Starting a new thread that starts a new server
	// that listens and waits for the user to authenticate
	public static void startGoogleCalendarAuthenticationThread() {
		Thread authenticationThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					GoogleCalendarEvents.getCalendarEvents();
				}
				catch (IOException ex) {
					System.out.println(GoogleCalendarEvents.class.getName());
					System.out.println("There was an error getting the Google calendar events");
				}
			}
		});
		authenticationThread.start();
	}
	
	
	public static void getCalendarEvents() throws IOException {
		Calendar calendar = buildCalendar();
		if (calendar == null) return;
		
		
		// List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = calendar.events().list("primary")
            .setMaxResults(10)
            .setTimeMin(now)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute();
        List<Event> items = events.getItems();
        if (items.size() == 0) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }
        
	}

}