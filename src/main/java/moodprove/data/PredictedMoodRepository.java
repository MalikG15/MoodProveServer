package moodprove.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import moodprove.to.PredictedMood;;

public interface PredictedMoodRepository extends JpaRepository<PredictedMood, Long> {
	public PredictedMood findBydateLessThan(Long date);
}
