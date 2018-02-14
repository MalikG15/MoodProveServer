package moodprove.rest;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.services.calendar.model.Event;

import moodprove.data.EventRepository;
import moodprove.google.GoogleCalendarEvents;
import moodprove.to.*;

@RequestMapping("/events")
@RestController
public class EventRestController {

	@Autowired
	EventRepository eventRepository;
	
	
	public String unratedEvents(@RequestParam("userid") String userId) {
		GoogleCalendarEvents calendarEvents = new GoogleCalendarEvents(userId);
		List<com.google.api.services.calendar.model.Event> events = new ArrayList<>();
		if (calendarEvents.isTokenValid()) {
			events = calendarEvents.getAllEvents();
		}
		
		JSONArray jsonArray = new JSONArray();
		JSONObject finalData = new JSONObject();
		for (com.google.api.services.calendar.model.Event e : events) {
			if (eventRepository.findByeventid(e.getId()) != null) 
				continue;
			JSONObject eventJson = new JSONObject();
			eventJson.put("eventid", e.getId());
			if (e.getStart() != null)
				eventJson.put("date", e.getStart().getDate().getValue());
			eventJson.put("eventTitle", e.getSummary());
			eventJson.put("eventDescription", e.getDescription());
			jsonArray.put(eventJson);
		}
		
		
		finalData.put("events", "finalData");
		return finalData.toString();
	}
	
	public void rateEvent(@RequestParam("userid") String userid, @RequestParam("eventid") String eventId, 
			@RequestParam("date") Long date, @RequestParam("rating") Integer rating) {
		moodprove.to.Event e = new moodprove.to.Event();
		e.setEventid(eventId);
		e.setUserid(userid);
		e.setRating(rating);
		e.setDate(date);
		eventRepository.saveAndFlush(e);
	}
	
	
}
