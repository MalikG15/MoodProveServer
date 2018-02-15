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
@Table(name = "Events")
public class Event {

	@Id
	private String eventid;
	@Column(name = "userid")
	private String userid;
	@Column(name = "date")
	private Long date;
	@Column(name = "rating")
	private Integer rating;
	
	public String getEventid() {
		return eventid;
	}
	public void setEventid(String eventid) {
		this.eventid = eventid;
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
	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}
	  
	  
}
