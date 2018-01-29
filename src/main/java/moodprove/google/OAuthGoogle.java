package moodprove.google;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.json.JSONObject;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Clock;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;

import moodprove.google.AuthorizationCodeInstalledApp;

public class OAuthGoogle {
	
	/** Application name. */
    private static final String APPLICATION_NAME = "MoodProve";
    
	/** Directory to store user credentials for this application. */
    private static final File DATA_STORE_DIR = new File(
        System.getProperty("user.home"), ".credentials/calendar-java-moodprove");
    
    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    
    private static JSONObject AUTHENTICATION_STILL_VALID_RESPONSE = new JSONObject();;
    
    private static JSONObject ERROR_RETRIEVING_TOKEN = new JSONObject();;
    
    /** Global instance of the scopes required by this quickstart.
    *
    * If modifying these scopes, delete your previously saved credentials
    * at ~/.credentials/calendar-java-quickstart
    */
    private List<String> scopes;
    
    /** File that contains OAuth Credentials **/
    private String credentialFileName;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();
    
    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;
    
    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
            AUTHENTICATION_STILL_VALID_RESPONSE.put("Response", "Google token still valid");
            ERROR_RETRIEVING_TOKEN.put("Response", "Error retreiving token");
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public OAuthGoogle(List<String> scopes, String credentialFileName) throws IOException {
    	this.scopes = scopes;
    	this.credentialFileName = credentialFileName;
    }
    
    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public Credential authorize(String userId) throws IOException {
        // Load client secrets.
    	FileInputStream f = new FileInputStream(credentialFileName);
    	
    	GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(f));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver(userId)).authorize(userId);
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }
 	
 	public static boolean isGoogleTokenValid(String userId) throws IOException {
 		DataStore<StoredCredential> credentialDataStore = DATA_STORE_FACTORY.getDataStore(StoredCredential.class.getSimpleName());

 	    if (credentialDataStore != null) {
 	       StoredCredential stored = credentialDataStore.get(userId);
 	       if (stored != null) {
 	    	  Long expirationTimeMilliseconds = stored.getExpirationTimeMilliseconds();
 	    	  String refreshToken = stored.getRefreshToken();
 	    	  return ((expirationTimeMilliseconds - Clock.SYSTEM.currentTimeMillis()) / 1000) > 60 &&
 	    			  refreshToken != null;
 	       }
 	    }
 	    
 	    return false;
 	}
 	
 	public static String getAccessToken(String userId) throws IOException {
 		DataStore<StoredCredential> credentialDataStore = DATA_STORE_FACTORY.getDataStore(StoredCredential.class.getSimpleName());

 	    if (credentialDataStore != null) {
 	       StoredCredential stored = credentialDataStore.get(userId);
 	      if (stored != null) {
 	    	return stored.getAccessToken();
 	      }
 	    }
 	      
 	      return null;
 	}

	public HttpTransport getHTTP_TRANSPORT() {
		return HTTP_TRANSPORT;
	}

	public String getApplicationName() {
		return APPLICATION_NAME;
	}

	public JsonFactory getJsonFactory() {
		return JSON_FACTORY;
	}

	public static JSONObject getAUTHENTICATION_STILL_VALID_RESPONSE() {
		return AUTHENTICATION_STILL_VALID_RESPONSE;
	}

	public static JSONObject getERROR_RETRIEVING_TOKEN() {
		return ERROR_RETRIEVING_TOKEN;
	}
}
