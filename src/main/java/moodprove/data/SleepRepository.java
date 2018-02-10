package moodprove.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import moodprove.to.Sleep;

public interface SleepRepository extends JpaRepository<Sleep, Long> {
	public Sleep findBydate(Long date);
	public List<Sleep> findByday(String day);
	@Transactional
    void deleteBysleepid(String sleepid);
}
