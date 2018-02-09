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
	@Column(name = "sleephours")
	private Long sleephours;
	@Column(name = "socialid")
	private String socialId;
	@Column(name = "weatherid")
	private String weatherId;
	@Column(name = "prediction")
	private Integer prediction;
	
	public void setPrediction(Integer prediction) {
		this.prediction = prediction;
	}
	
	public Integer getPrediction() {
		return prediction;
	}
	  

}
