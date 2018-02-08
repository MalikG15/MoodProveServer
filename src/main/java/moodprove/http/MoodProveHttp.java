package moodprove.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 * Simple HTTP request class to retrieve data
 * for SleepCloud Storage API and Weather API.
 * 
 * It is also used by the Facebook token class.
 *
 */
public class MoodProveHttp {
	
	public static String executeGet(String targetUrl, String token) {
		HttpURLConnection connection = null;

		  try {
		    //Create connection
		    URL url = new URL(targetUrl);
		    connection = (HttpURLConnection) url.openConnection();
		    connection.setRequestMethod("GET");
		    connection.setRequestProperty("Content-Type", 
		        "application/x-www-form-urlencoded");
		    // The token argument is only needed for SleepData 
		    // authorization.
		    if (!token.isEmpty()) {
		    	connection.setRequestProperty("Authorization", String.format("Bearer %s", token));
		    }
		    
		    connection.setRequestProperty("Content-Language", "en-US");  

		    connection.setUseCaches(false);
		    connection.setDoOutput(true);
		    
		    //Get Response  
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
		    String line;
		    while ((line = rd.readLine()) != null) {
		      response.append(line);
		      response.append('\r');
		    }
		    rd.close();
		    return response.toString();
		  } catch (Exception e) {
		    e.printStackTrace();
		    return null;
		  } finally {
		    if (connection != null) {
		      connection.disconnect();
		    }
		  }
	}
	
	public static void main(String[] args) {	
		String response = MoodProveHttp.executeGet("https://sleep-cloud.appspot.com/fetchRecords?timestamp=1514846489", 
				"ya29.GltcBaF_KThFcExCE8GlbhOndv1uoZfw6XbWLteJ1tkqLUS-8-h3OW3_ACHBegzXjlwlwM6sEgOcmYDTXquTUX9BesU_9Tvaf3YjCIKfzo32W_LUS5QCt9xVfprL");
		System.out.println(response);
	}
		
}
