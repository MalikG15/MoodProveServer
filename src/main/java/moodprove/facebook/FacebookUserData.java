package moodprove.facebook;

import java.util.Date;
import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.json.JsonObject;
import com.restfb.types.User;


public class FacebookUserData {

	private FacebookClient facebookClient;
	private String facebookId;
	private Date lastCheckIn;
	
	// Pages liked
	// Events
	// User feed
	// User friends
	
	public FacebookUserData(FacebookClient facebookClient, Date lastCheckIn) {
		this.facebookClient = facebookClient;
		User user = facebookClient.fetchObject("me", User.class);
		this.facebookId = user.getId();
		this.lastCheckIn = lastCheckIn;
	}
	
	public Integer getFbDataByType(String type) {
		Connection<JsonObject> fbData = facebookClient.fetchConnection(facebookId + type, JsonObject.class, Parameter.with("date_format", "U"), Parameter.with("limit", "250"), Parameter.with("redirect","false"));
		return getCountSinceLastCheckIn(fbData);
	}

	public int getCountSinceLastCheckIn(Connection<JsonObject> fbData) {
		int count = 0;
		if (fbData != null) {
			List<JsonObject> obj = fbData.getData();
			for (JsonObject j : obj) {
				Long pageLikedTime = j.getLong("created_time", 0);
				Date date = new Date(pageLikedTime);
				if (lastCheckIn.before(date)) count++;
				else break;
			}
		}
		return count;
	}
	
	public static void main(String[] args) {
		FacebookClient fbClient = new DefaultFacebookClient("EAACEdEose0cBAJnMyOqulksF3ta4BsHQHyszBkVRn34IcZC49ZA8clwTVzR85JgPfuSQJnyLBddgjTPl1tPF57CgbKvmo0PaSyqxuEZC0irmuZCh8uCvvm5FBZBZAOkZAQTO4ZClKv5tsTGWvLzPwkx3aPK0zlvXp1SDtxEZCEAGe9ubJ5pPsx7kc6INz4SYnhJwneI3i4n7M4bZCv0jEKqvyw",
				Version.LATEST);
		Date testDate = new Date(Long.valueOf("1501627289"));
		FacebookUserData userData = new FacebookUserData(fbClient, testDate);
		System.out.println(userData.getFbDataByType("/events"));
		System.out.println(userData.getFbDataByType("/likes"));
		System.out.println(userData.getFbDataByType("/feed"));
	}
	

}
