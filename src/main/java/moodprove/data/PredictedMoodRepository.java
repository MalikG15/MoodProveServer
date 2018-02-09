package moodprove.data;

import org.springframework.data.jpa.repository.JpaRepository;

import moodprove.to.PredictedMood;;

public interface PredictedMoodRepository extends JpaRepository<PredictedMood, Long> {

}
