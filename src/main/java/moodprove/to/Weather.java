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
@Table(name = "Weather")
public class Weather {

	@Id
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@GeneratedValue(generator = "uuid")
	@Column(name = "weatherid")
	private String weatherId;
	@Column(name = "userid")
	private String userId;
	@Column(name = "date")
	private Long date;
	@Column(name = "sunrisetime")
	private Long sunriseTime;
	@Column(name = "sunsettime")
	private Long sunsetTime;
	@Column(name = "precipintensity")
	private Double precipIntensity;
	@Column(name = "precipprobablity")
	private Double precipProbablity;
	@Column(name = "preciptype")
	private String precipType;
	@Column(name = "temperature")
	private Integer temperature;
	@Column(name = "humidity")
	private Double humidity;
	@Column(name = "cloudcover")
	private Double cloudCover;
	@Column(name = "visibility")
	private Integer visibility;
	
}
