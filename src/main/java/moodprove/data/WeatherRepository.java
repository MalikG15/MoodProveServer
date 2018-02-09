package moodprove.data;

import org.springframework.data.jpa.repository.JpaRepository;

import moodprove.to.Weather;

public interface WeatherRepository extends JpaRepository<Weather, Long> {

}
