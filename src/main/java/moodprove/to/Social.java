package moodprove.to;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 
 * @author malikg
 *
 */
@Entity
@Table(name = "Social")
public class Social {

	  @Id
	  @GenericGenerator(name = "uuid", strategy = "uuid2")
	  @GeneratedValue(generator = "uuid")
	  @Column(name = "socialid")
	  private String socialid;
	  @Column(name = "userid")
	  private String userid;
	  @Column(name = "date")
	  private Long date;
	  @Column(name = "day")
	  private String day;
	  @Column(name = "facebooklikes")
	  private Integer facebookLikes;
	  @Column(name = "facebookevents")
	  private Integer facebookEvents;
	  @Column(name = "facebooktimelineupdates")
	  private Integer facebookTimeLineUpdates;

	public String getSocialid() {
		return socialid;
	}
	public void setSocialid(String socialid) {
		this.socialid = socialid;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public Integer getFacebookLikes() {
		return facebookLikes;
	}
	public void setFacebookLikes(Integer facebookLikes) {
		this.facebookLikes = facebookLikes;
	}
	public Integer getFacebookEvents() {
		return facebookEvents;
	}
	public void setFacebookEvents(Integer facebookEvents) {
		this.facebookEvents = facebookEvents;
	}
	public Integer getFacebookTimeLineUpdates() {
		return facebookTimeLineUpdates;
	}
	public void setFacebookTimeLineUpdates(Integer facebookTimeLineUpdates) {
		this.facebookTimeLineUpdates = facebookTimeLineUpdates;
	}
	  
}
