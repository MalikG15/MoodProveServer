package moodprove.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import moodprove.data.*;

@Service
public class MoodCron {
	
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private PastMoodRepository pastMoodRepo;
	@Autowired
	private PredictedMoodRepository predictedMoodRepository;
	@Autowired
	private SleepRepository sleepRepository;
	@Autowired
	private SocialRepository socialRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private WeatherRepository weatherRepository;
	

}
