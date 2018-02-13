package moodprove.rest;

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
	
	
	@RequestMapping("/before")
	public String getMoodBefore(@RequestParam("userid") String userId, @RequestParam("timestamp") Long timestamp) {
		List<PastMood> pastMoodBefore = pastMoodRepository.findFirst8ByuseridAndDateLessThan(userId, timestamp);
		JSONArray pastMoodBeforeArray = new JSONArray();
		for (PastMood mood : pastMoodBefore) {
			JSONObject data = new JSONObject();
			data.put("timestamp", mood.getDate());
			data.put("mood", mood.getPrediction());
			pastMoodBeforeArray.put(data);
		}
		JSONObject finalData = new JSONObject();
		finalData.put("data", pastMoodBeforeArray);
		
		return finalData.toString();
	}

}
