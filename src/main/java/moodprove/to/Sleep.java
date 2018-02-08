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
	  private String sleepId;
	  @Column(name = "date")
	  private Long date;
	  @Column(name = "sleeplength")
	  private Integer sleeplength;
	  @Column(name = "cycles")
	  private Integer sleepCyles;
	  @Column(name = "noiselevel")
	  private Integer noiseLevel;

}
