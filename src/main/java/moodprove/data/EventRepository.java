package moodprove.data;

import org.springframework.data.jpa.repository.JpaRepository;

import moodprove.to.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

}
