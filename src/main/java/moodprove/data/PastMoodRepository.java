package moodprove.data;

import org.springframework.data.jpa.repository.JpaRepository;

import moodprove.to.PastMood;

public interface PastMoodRepository extends JpaRepository<PastMood, Long> {

}
