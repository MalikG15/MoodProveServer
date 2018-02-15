package moodprove.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@RequestMapping("/event")
@RestController
public class EventRestController {

	@Autowired
	EventRepository eventRepository;
	
	
	@RequestMapping("/unratedevents")
	public String unratedEvents(@RequestParam("userid") String userId) {
		GoogleCalendarEvents calendarEvents = new GoogleCalendarEvents(userId);
		if (!calendarEvents.isTokenValid()) {
			return null;
		}
		List<com.google.api.services.calendar.model.Event> events = calendarEvents.getAllEvents();
		JSONArray jsonArray = new JSONArray();
		JSONObject finalData = new JSONObject();
		Map<String, com.google.api.services.calendar.model.Event> singularEvents = new HashMap<>();
		for (com.google.api.services.calendar.model.Event e : events) {
			moodprove.to.Event result = eventRepository.findByeventid(e.getRecurringEventId());
			if (eventRepository.findByeventid(e.getRecurringEventId()) != null
					|| singularEvents.containsKey(e.getRecurringEventId())) {
				continue;
			}
			singularEvents.put(e.getRecurringEventId(), e);
		}

		for (Event e : singularEvents.values()) {
			JSONObject eventJson = new JSONObject();
			eventJson.put("eventid", e.getRecurringEventId());
			if (e.getStart() != null && e.getStart().getDate() != null) {
				eventJson.put("date", e.getStart().getDate().getValue());
			}
			eventJson.put("eventTitle", e.getSummary());
			eventJson.put("eventDescription", e.getDescription());
			jsonArray.put(eventJson);
		}
		
		
		finalData.put("events", jsonArray);
		return finalData.toString();
	}
	
	@RequestMapping("/rate")
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
