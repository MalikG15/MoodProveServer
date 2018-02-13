package moodprove.rest;

import java.util.List;

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
	public String getMoodBefore(@RequestParam("timestamp") Long timestamp) {
		List<PastMood> pastMoodBefore = pastMoodRepository.findFirst8BydateLessThan(timestamp);
		JSONObject pastMoodBeforeJSON = new JSONObject();
		for (int index = 0; index < pastMoodBefore.size(); index++) {
			PastMood current = pastMoodBefore.get(index);
			pastMoodBeforeJSON.put(String.valueOf(index), String.valueOf(current.getDate() + "," + current.getPrediction()));
		}
		
		return pastMoodBeforeJSON.toString();
	}

}
