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
@Table(name = "Sleep")
public class Sleep {
	
	  @Id
	  @GenericGenerator(name = "uuid", strategy = "uuid2")
	  @GeneratedValue(generator = "uuid")
	  @Column(name = "sleepid")
	  private String sleepid;
	  @Column(name = "userid")
	  private String userid;
	  @Column(name = "date")
	  private Long date;
	  @Column(name = "day")
	  private String day;
	  @Column(name = "sleeplength")
	  private Integer sleeplength;
	  @Column(name = "cycles")
	  private Integer sleepCyles;
	  @Column(name = "noiselevel")
	  private Integer noiseLevel;

	  
	public String getSleepId() {
		return sleepid;
	}
	public void setSleepId(String sleepId) {
		this.sleepid = sleepId;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	public Integer getSleeplength() {
		return sleeplength;
	}
	public void setSleeplength(Integer sleeplength) {
		this.sleeplength = sleeplength;
	}
	public Integer getSleepCyles() {
		return sleepCyles;
	}
	public void setSleepCyles(Integer sleepCyles) {
		this.sleepCyles = sleepCyles;
	}
	public Integer getNoiseLevel() {
		return noiseLevel;
	}
	public void setNoiseLevel(Integer noiseLevel) {
		this.noiseLevel = noiseLevel;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}

}
