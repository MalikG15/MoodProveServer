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
	@Column(name = "precipprobability")
	private Double precipProbability;
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
	
	public String getWeatherId() {
		return weatherId;
	}
	public void setWeatherId(String weatherId) {
		this.weatherId = weatherId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	public Long getSunriseTime() {
		return sunriseTime;
	}
	public void setSunriseTime(Long sunriseTime) {
		this.sunriseTime = sunriseTime;
	}
	public Long getSunsetTime() {
		return sunsetTime;
	}
	public void setSunsetTime(Long sunsetTime) {
		this.sunsetTime = sunsetTime;
	}
	public Double getPrecipIntensity() {
		return precipIntensity;
	}
	public void setPrecipIntensity(Double precipIntensity) {
		this.precipIntensity = precipIntensity;
	}
	public Double getPrecipProbablity() {
		return precipProbability;
	}
	public void setPrecipProbablity(Double precipProbablity) {
		this.precipProbability = precipProbablity;
	}
	public String getPrecipType() {
		return precipType;
	}
	public void setPrecipType(String precipType) {
		this.precipType = precipType;
	}
	public Integer getTemperature() {
		return temperature;
	}
	public void setTemperature(Integer temperature) {
		this.temperature = temperature;
	}
	public Double getHumidity() {
		return humidity;
	}
	public void setHumidity(Double humidity) {
		this.humidity = humidity;
	}
	public Double getCloudCover() {
		return cloudCover;
	}
	public void setCloudCover(Double cloudCover) {
		this.cloudCover = cloudCover;
	}
	public Integer getVisibility() {
		return visibility;
	}
	public void setVisibility(Integer visibility) {
		this.visibility = visibility;
	}
	public Double getPrecipProbability() {
		return precipProbability;
	}
	public void setPrecipProbability(Double precipProbability) {
		this.precipProbability = precipProbability;
	}
	
}
