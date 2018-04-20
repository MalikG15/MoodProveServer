package moodprove.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

@RequestMapping("/past")
@RestController
public class PastMoodRestController {
	
	@Autowired
	PastMoodRepository pastMoodRepository;
	
	// Test Purposes Only
	// 1523815790000
	// 1523902190000
	// 1523988590000
	// 1524074990000
	// 1524161390000
	// 1524247790000
	// 1524334190000
	
	
	
	@RequestMapping("/beforeOrAfter")
	public String getMoodBeforeOrAfter(@RequestParam("userid") String userId, @RequestParam("timestamp") Long timestamp,
			@RequestParam("type") String type) {
		JSONObject finalData = new JSONObject();
		List<PastMood> pastMoodBefore = new ArrayList<>();
		if (type.equals("before")) {
			pastMoodBefore = pastMoodRepository.findFirst8ByuseridAndDateLessThan(userId, timestamp);
		}
		else if (type.equals("after")) {
			pastMoodBefore = pastMoodRepository.findFirst8ByuseridAndDateGreaterThan(userId, timestamp);
		}
		else {
			finalData.put("data", "Request Invalid");
			return finalData.toString();
		}
		
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
	
	@RequestMapping("/check")
	public String checkMoodHistory(@RequestParam("userid") String userId) {
		List<PastMood> pastMood = pastMoodRepository.findAllByuserid(userId);

		return String.format("{\"data:\" \"%s\"}", (pastMood == null || pastMood.size() < 7) ? "Insufficient" : "Sufficient");
	}
	
	public static void main(String[] args) {
		
	}

}
