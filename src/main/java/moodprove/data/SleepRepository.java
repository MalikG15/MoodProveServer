package moodprove.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import moodprove.to.Sleep;

public interface SleepRepository extends JpaRepository<Sleep, Long> {
	public Sleep findBydate(Long date);
	@Transactional
    void deleteBysleepid(String sleepid);
}
