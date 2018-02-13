package moodprove.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import moodprove.to.PastMood;

public interface PastMoodRepository extends JpaRepository<PastMood, Long> {
	public List<PastMood> findAllByuserid(String userId);
	public List<PastMood> findFirst8BydateLessThan(Long date);
}
