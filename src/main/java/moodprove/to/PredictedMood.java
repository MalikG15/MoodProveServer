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
@Table(name = "PredictedMood")
public class PredictedMood {

	  @Id
	  @GenericGenerator(name = "uuid", strategy = "uuid2")
	  @GeneratedValue(generator = "uuid")
	  @Column(name = "predictedmoodid")
	  private String predictedMoodId;
	  @Column(name = "userid")
	  private String userid;
	  @Column(name = "date")
	  private Long date;
	  // This column will list event ids, to
	  // display to the user what events they did this day
	  @Column(name = "events", length = 1000)
	  private String events;
	  @Column(name = "sleephours")
	  private Long sleephours;
	  @Column(name = "socialid")
	  private String socialId;
	  @Column(name = "weatherid")
	  private String weatherId;
	  @Column(name = "prediction")
	  private Integer prediction;
	  
	public String getPredictedMoodId() {
		return predictedMoodId;
	}
	public void setPredictedMoodId(String predictedMoodId) {
		this.predictedMoodId = predictedMoodId;
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
	public Long getSleephours() {
		return sleephours;
	}
	public void setSleephours(Long sleephours) {
		this.sleephours = sleephours;
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
	public Integer getPrediction() {
		return prediction;
	}
	public void setPrediction(Integer prediction) {
		this.prediction = prediction;
	}

	  


}
