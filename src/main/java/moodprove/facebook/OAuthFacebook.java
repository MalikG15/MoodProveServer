package moodprove.facebook;

import java.io.IOException;

import org.json.JSONObject;

import com.restfb.DefaultWebRequestor;
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
	
	public static JSONObject getFacebookUserToken(String code) throws IOException {
	    String appId = OAuthFacebookClientInfo.getClientId();
	    String secretKey = OAuthFacebookClientInfo.getClientPassword();

	    WebRequestor wr = new DefaultWebRequestor();
	    WebRequestor.Response accessTokenResponse = wr.executeGet(
	            "https://graph.facebook.com/oauth/access_token?client_id=" + appId + "&redirect_uri=" + REDIRECT_URL
	            + "&client_secret=" + secretKey + "&code=" + code);

	    JSONObject obj = new JSONObject(accessTokenResponse.getBody());
	    return obj;
	}

	public static String getOauthSuccessReponseHtml() {
		return OAUTH_SUCCESS_REPONSE_HTML;
	}

	public static String getRedirectUrl() {
		return REDIRECT_URL;
	}

}
