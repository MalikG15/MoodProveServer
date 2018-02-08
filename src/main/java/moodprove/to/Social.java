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
	  @Column(name = "facebooklikes")
	  private Long facebookLikes;
	  @Column(name = "facebookevents")
	  private Long facebookEvents;
	  @Column(name = "facebooktimelineupdates")
	  private Long facebookTimeLineUpdates;
	  
}
