package moodprove.to;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author Malik Graham
 */
@Entity
@Table(name = "User")
public class User {
	
	@Id
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@GeneratedValue(generator = "uuid")
	@Column(name = "userid")
	private String userid;
	@Column(name = "name")
	private String name;
	@Column(name = "email")
	private String email;
	@Column(name = "password")
	private String password;
	@Column(name = "facebookaccesstoken")
	private String facebookAccessToken;
	@Column(name = "facebooktokenexpire")
	private Long facebookTokenExpire;
	@Column(name = "googleoauthlink", length = 500)
	private String googleOAuthLink;
	@Column(name = "scheduledtimeofprediction")
	private String scheduledTimeOfPrediction;
	@Column(name = "longitude")
	private Double longitude;
	@Column(name = "latitude")
	private Double latitude;
	@Column(name = "newUserNextCheckInTime")
	private Long newUserNextCheckInTime;
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFacebookAccessToken() {
		return facebookAccessToken;
	}
	public void setFacebookAccessToken(String facebookAccessToken) {
		this.facebookAccessToken = facebookAccessToken;
	}
	public Long getFacebookTokenExpire() {
		return facebookTokenExpire;
	}
	public void setFacebookTokenExpire(Long facebookTokenExpire) {
		this.facebookTokenExpire = facebookTokenExpire;
	}
	public String getGoogleOAuthLink() {
		return googleOAuthLink;
	}
	public void setGoogleOAuthLink(String googleOAuthLink) {
		this.googleOAuthLink = googleOAuthLink;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public String getScheduledTimeOfPrediction() {
		return scheduledTimeOfPrediction;
	}
	public void setScheduledTimeOfPrediction(String scheduledTimeOfPrediction) {
		this.scheduledTimeOfPrediction = scheduledTimeOfPrediction;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getNewUserNextCheckInTime() {
		return newUserNextCheckInTime;
	}
	public void setNewUserNextCheckInTime(Long newUserNextCheckInTime) {
		this.newUserNextCheckInTime = newUserNextCheckInTime;
	}
	
	  

}
