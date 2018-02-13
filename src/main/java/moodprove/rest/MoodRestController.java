package moodprove.rest;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import moodprove.data.PastMoodRepository;
import moodprove.to.PastMood;

@RequestMapping("/mood")
@RestController
public class MoodRestController {
	
	@Autowired
	PastMoodRepository pastMoodRepository;
	
	
	@RequestMapping("/beforeOrAfter")
	public String getMoodBeforeOrAfter(@RequestParam("userid") String userId, @RequestParam("timestamp") Long timestamp,
			@RequestParam("type") String type) {
		List<PastMood> pastMoodBefore = new ArrayList<>();
		if (type.equals("before")) {
			pastMoodBefore = pastMoodRepository.findFirst8ByuseridAndDateLessThan(userId, timestamp);
		}
		else if (type.equals("after")) {
			pastMoodBefore = pastMoodRepository.findFirst8ByuseridAndDateGreaterThan(userId, timestamp);
		}
		else {
			return "Request Invalid";
		}
		
		JSONObject finalData = new JSONObject();
		
		if (pastMoodBefore.size() == 0) {
			finalData.put("data", "No Valid Data");
			return finalData.toString();
		}
		
		JSONArray pastMoodBeforeArray = new JSONArray();
		for (PastMood mood : pastMoodBefore) {
			JSONObject data = new JSONObject();
			data.put("timestamp", mood.getDate());
			data.put("mood", mood.getPrediction());
			pastMoodBeforeArray.put(data);
		}
		
		finalData.put("data", pastMoodBeforeArray);
		
		return finalData.toString();
	}

}
