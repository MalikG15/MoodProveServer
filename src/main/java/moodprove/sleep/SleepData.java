package moodprove.sleep;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.calendar.CalendarScopes;

import moodprove.google.OAuthGoogle;
import moodprove.http.MoodProveHttp;

public class SleepData {
	
	private static final String SLEEP_CLOUD_API_CALL = "https://sleep-cloud.appspot.com/fetchRecords?timestamp=";
	
	private static final List<String> SCOPES =
            Arrays.asList(CalendarScopes.CALENDAR_READONLY, "https://www.googleapis.com/auth/userinfo.email");

    private static final String CREDENTIAL_FILE_NAME = "src/main/java/moodprove/google/google_oauth_client_secret.json";
	
	private final String userId;
	
	private OAuthGoogle oauthGoogle;
	
	public SleepData(String userId) {
		this.userId = userId;
    	try {
    		this.oauthGoogle = new OAuthGoogle(SCOPES, CREDENTIAL_FILE_NAME);
    	}
    	catch (IOException ex) {
    		System.out.println(SleepData.class.getName());
 			System.out.println("Could not create an oauthGoogle instance.");
    	}
	}
	
	public JSONArray getSleepData(Long afterTimestamp) {
		try {
			Credential credential = oauthGoogle.authorize(userId);
			JSONObject sleepCloudData = new JSONObject(MoodProveHttp.executeGet(SLEEP_CLOUD_API_CALL + afterTimestamp, credential.getAccessToken()));
			return sleepCloudData.getJSONArray("sleeps");
		}
		catch (IOException ex) {
			System.out.println(SleepData.class.getName());
 			System.out.println("Could not load Google OAuth credentials");
		}
		return null;
	}
	
	public static void main(String[] args) {
		SleepData data = new SleepData("bc0e577c-b434-4129-8ded-d4882967fa24");
		System.out.println(data.getSleepData(Long.valueOf(1514846489)).toString());
	}

}
