package moodprove.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import moodprove.to.PredictedMood;;

public interface PredictedMoodRepository extends JpaRepository<PredictedMood, Long> {
	public PredictedMood findBydateLessThan(Long date);
	@Transactional
    void deleteAllByuserid(String userid);
}
