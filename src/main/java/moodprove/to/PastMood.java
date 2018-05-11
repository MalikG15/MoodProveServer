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
@Table(name = "PastMood")
public class PastMood {
	@Id
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@GeneratedValue(generator = "uuid")
	@Column(name = "pastmoodid")
	private String pastMoodId;
	@Column(name = "userid")
	private String userid;
	@Column(name = "date")
	private Long date;
	// This column will list event ids, to
	// display to the user what events they did this day
	@Column(name = "events", length = 1000)
	private String events;
	@Column(name = "sleepid")
	private String sleepid;
	@Column(name = "socialid")
	private String socialId;
	@Column(name = "weatherid")
	private String weatherId;
	@Column(name = "prediction", length = 2000)
	private String prediction;

	public String getPastMoodId() {
		return pastMoodId;
	}

	public void setPastMoodId(String pastMoodId) {
		this.pastMoodId = pastMoodId;
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

	public String getEvents() {
		return events;
	}

	public void setEvents(String events) {
		this.events = events;
	}

	public String getSocialId() {
		return socialId;
	}

	public void setSocialId(String socialId) {
		this.socialId = socialId;
	}

	public String getWeatherId() {
		return weatherId;
	}

	public void setWeatherId(String weatherId) {
		this.weatherId = weatherId;
	}

	public String getSleepid() {
		return sleepid;
	}

	public void setSleepid(String sleepid) {
		this.sleepid = sleepid;
	}

	public String getPrediction() {
		return prediction;
	}

	public void setPrediction(String prediction) {
		this.prediction = prediction;
	}
	  

}
