package moodprove.facebook;

import org.json.JSONObject;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;

import moodprove.http.MoodProveHttp;

import com.restfb.Version;

public class OAuthFacebook {
	
	private static String APP_ID;
	
	private static String SECRET_KEY;
	
	static {
		try {
			APP_ID = OAuthFacebookClientInfo.getClientId();
			SECRET_KEY = OAuthFacebookClientInfo.getClientPassword();
		}
		catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
	}
	
	public static Long getTokenExpirationTime(String token) {
		Long currentTime = System.currentTimeMillis() / 1000;
		JSONObject tokenInfo = new JSONObject(MoodProveHttp.executeGet("https://graph.facebook.com/oauth/access_token_info?access_token=" + token, ""));
		Long timeAlive = null;
		if (tokenInfo != null) {
			timeAlive = tokenInfo.getLong("expires_in");
		}
		if (timeAlive != null) {
			return currentTime + timeAlive; 
		}
		
		return null;
	}
	
	public static String getExtendedAccessToken(String token) {
		FacebookClient facebookClient = new DefaultFacebookClient(token, Version.LATEST);
		AccessToken extendedAccessToken = facebookClient.obtainExtendedAccessToken(APP_ID, SECRET_KEY, token);
		return extendedAccessToken.getAccessToken();
	}

	public static void main(String[] args) {

	}

}
