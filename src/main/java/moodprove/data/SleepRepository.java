package moodprove.data;

import org.springframework.data.jpa.repository.JpaRepository;

import moodprove.to.Sleep;

public interface SleepRepository extends JpaRepository<Sleep, Long> {

}
