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
	  private long date;
	  @Column(name = "temperature")
	  private double temperature;
	  @Column(name = "humdity")
	  private double humidity;
	  @Column(name = "cloudcover")
	  private double cloudCover;
	  @Column(name = "sleephours")
	  private int sleepHours;
	  
	  public String getPredictedMoodId() {
		return pastMoodId;
	  }
	  public void setPredictedMoodId(String pastMoodId) {
		this.pastMoodId = pastMoodId;
	  }
	  public String getUserid() {
		return userid;
	  }
	  public void setUserid(String userid) {
		this.userid = userid;
	  }
	  public long getDate() {
		return date;
	  }
	  public void setDate(long date) {
		this.date = date;
	  }
	  public double getTemperature() {
		return temperature;
	  }
	  public void setTemperature(double temperature) {
		this.temperature = temperature;
	  }
	  public double getHumidity() {
		return humidity;
	  }
	  public void setHumidity(double humidity) {
		this.humidity = humidity;
	  }
	  public double getCloudCover() {
		return cloudCover;
	  }
	  public void setCloudCover(double cloudCover) {
		this.cloudCover = cloudCover;
	  }
	  public int getSleepHours() {
		return sleepHours;
	  }
	  public void setSleepHours(int sleepHours) {
		this.sleepHours = sleepHours;
	  }

}
