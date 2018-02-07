package moodprove.facebook;

import java.io.IOException;

import org.json.JSONObject;

import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultWebRequestor;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.Version;
import com.restfb.WebRequestor;

public class OAuthFacebook {
	
	private static final String OAUTH_SUCCESS_REPONSE_HTML = "<!DOCTYPE html>"
			+ "<html>"
			+ "<head>"
			+ "<style>"
			+ "div { text-align: center; font-size: 50px;  font-family: \"Times New Roman\", Times, serif;}"
			+ "</style>"
			+ "</head>"
			+ "<body>"
			+ "<div><a href=\"lawrence.moodprovemacapp://%s\"/> Click Here to Finish Authorizing MoodProve to use Facebook Data </a></div>"
			+ "</body>"
			+ "</html>";
	
	private static final String REDIRECT_URL = "http://localhost:8080/auth/facebook";
	
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
	
	public static JSONObject getFacebookUserToken(String code) throws IOException {
	    WebRequestor wr = new DefaultWebRequestor();
	    WebRequestor.Response accessTokenResponse = wr.executeGet(
	            "https://graph.facebook.com/oauth/access_token?client_id=" + APP_ID + "&redirect_uri=" + REDIRECT_URL
	            + "&client_secret=" + SECRET_KEY + "&code=" + code);

	    JSONObject obj = new JSONObject(accessTokenResponse.getBody());
	    return obj;
	}
	
	public static String getExtendedAccessToken(String token) {
		FacebookClient facebookClient = new DefaultFacebookClient(token, Version.LATEST);
		AccessToken extendedAccessToken = facebookClient.obtainExtendedAccessToken(APP_ID, SECRET_KEY, token);
		return extendedAccessToken.getAccessToken();
	}

	public static String getOauthSuccessReponseHtml() {
		return OAUTH_SUCCESS_REPONSE_HTML;
	}

	public static String getRedirectUrl() {
		return REDIRECT_URL;
	}
	
	public static void main(String[] args) {
		
	}

}
