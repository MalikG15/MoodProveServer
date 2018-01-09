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
	  @GenericGenerator(name = "uuid", strategy = "uuid2")
	  @GeneratedValue(generator = "uuid")
	  @Column(name = "eventid")
	  private String eventid;
	  @Column(name = "userid")
	  private String userid;
	  @Column(name = "rating")
	  private int rating;
	  
	  
}
