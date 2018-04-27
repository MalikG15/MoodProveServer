package moodprove.sleep;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.calendar.CalendarScopes;

import moodprove.google.GoogleCalendarEvents;
import moodprove.google.OAuthGoogle;
import moodprove.http.MoodProveHttp;
import moodprove.to.Sleep;

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
	
	 public boolean isTokenValid() {
		try {
			return oauthGoogle.isGoogleTokenValid(userId);
		}
		catch (IOException ex) {
			System.out.println(SleepData.class.getName());
			System.out.println("Could not check if token is valid.");
		}
		
		return false;
	 }
	
	
	public Sleep getSleepData(Long afterTimestamp) {
		try {
			Credential credential = oauthGoogle.authorize(userId);
			JSONObject sleepCloudData = new JSONObject(MoodProveHttp.executeGet(SLEEP_CLOUD_API_CALL + afterTimestamp, credential.getAccessToken()));
			JSONArray sleepData = sleepCloudData.getJSONArray("sleeps");
			Sleep sleepRecord = new Sleep();
			int sleepCycles = 0, sleepNoiseLevel = 0, sleepLength = 0;
			for (int x = 0; x < sleepData.length(); x++) {
				JSONObject current = sleepData.getJSONObject(0);
				sleepLength += current.getInt("lengthMinutes");
				sleepNoiseLevel += current.getInt("noiseLevel");
				sleepCycles += current.getInt("cycles");
			}
			
			// Set average noise level
			sleepRecord.setNoiseLevel(sleepNoiseLevel / sleepData.length());
			// Set average sleep cycles
			sleepRecord.setSleepCyles(sleepCycles / sleepData.length());
			// Set average time spent slept
			sleepRecord.setSleeplength(sleepLength / sleepData.length());
			
			return sleepRecord;
		}
		catch (IOException ex) {
			System.out.println(SleepData.class.getName());
 			System.out.println("Could not load Google OAuth credentials to retrieve sleep data.");
		}
		return null;
	}
	
	public boolean isSleepDataAvailable(Long afterTimestamp) {
		try {
			Credential credential = oauthGoogle.authorize(userId);
			JSONObject sleepCloudData = new JSONObject(MoodProveHttp.executeGet(SLEEP_CLOUD_API_CALL + afterTimestamp, credential.getAccessToken()));
			JSONArray sleepData = sleepCloudData.getJSONArray("sleeps");
			return sleepData.length() != 0;
		}
		catch (IOException ex) {
			System.out.println(SleepData.class.getName());
 			System.out.println("Could not load Google OAuth credentials to check sleep data availability.");
		}
		return false;
	}
	
	public static void main(String[] args) {
		SleepData data = new SleepData("76508320-fe58-4c71-88a3-f90c2087c117");
		System.out.println(data.getSleepData(Long.valueOf(1514846489)).toString());
	}

}
