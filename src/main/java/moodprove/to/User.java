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
	@Column(name = "timeofdailyprediction")
	private Long timeOfDailyPrediction;
	@Column(name = "timeoflastprediction")
	private Long timeOfLastPrediction;
	@Column(name = "timeofcheckin")
	private Long timeOfCheckIn;
	@Column(name = "timeoflastcheckin")
	private Long timeOfLastCheckIn;
	@Column(name = "longitude")
	private Double longitude;
	@Column(name = "latitude")
	private Double latitude;
	  
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return email;
	}
	public void setUsername(String email) {
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
	public String getGoogleOauthLink() {
		return googleOAuthLink;
	}
	public void setGoogleOauthLink(String googleOauthLink) {
		this.googleOAuthLink = googleOauthLink;
	}
	public boolean googleOAuthConfirmationLinkExists() {
		return googleOAuthLink != null;
	}
	  

}
