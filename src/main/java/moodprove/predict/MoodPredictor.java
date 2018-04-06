package moodprove.predict;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import moodprove.to.Sleep;
import moodprove.to.Event;
import moodprove.to.PastMood;
import moodprove.to.PredictedMood;
import moodprove.to.Weather;
import moodprove.to.Social;

/**
 * 
 * @author malikg
 *
 */
public class MoodPredictor {
	
	private static final String MOOD_PAST_FILE_LOCATION = "src/main/java/moodprove/predict/mood-past-test.arff";
	
	private static final String MOOD_PREDICT_FILE_LOCATION = "src/main/java/moodprove/predict/mood-predict-test.arff";
	
	private static final String FILE_HEADER = "@relation qdb-weka.filters.unsupervised.attribute.Remove-R1";
	
	private static final String[] EVENT_HEADERS = new String[] {"@attribute eventratings REAL"};
	
	private static final String[] SLEEP_HEADERS = new String[] {"@attribute sleepHours REAL", 
			"@attribute sleepCycles REAL", "@attribute sleepNoiseLevel REAL"};
	
	private static final String[] SOCIAL_HEADERS = new String[] {"@attribute facebookLikes REAL",
			"@attribute facebookEvents REAL", "@attribute facebookNewsFeed REAL"};
	
	private static final String[] WEATHER_HEADERS = new String[] {"@attribute sunriseTime REAL",
			"@attribute sunsetTime REAL", "@attribute precipIntensity REAL", "@attribute precipProbability REAL", 
			"@attribute temperature REAL", "@attribute humidity REAL",
			"@attribute cloudCover REAL", "@attribute visibility REAL"};
	
	// The base moods are the fundamental moods established within MoodProve
	// while the modifiers make the base moods more complex and personal to the user.
	private static final String[] BASE_MOODS = new String[] {"happy", "mad", "neutral", "sad", "stressed"};
	
	private static final String[] MOOD_MODIFIERS = new String[] {"anxious", "calm", "confused", "overwhelmed", "surprised"};
	
	private List<String> moodVariationsList;
	
	private PrintWriter writerMoodPast;
	
	private PrintWriter writerMoodPredict;
	
	public MoodPredictor() {
		/*try {
			this.writerMoodPast = new PrintWriter(MOOD_PAST_FILE_LOCATION, "UTF-8");
			this.writerMoodPredict = new PrintWriter(MOOD_PREDICT_FILE_LOCATION, "UTF-8");
		}
		catch (IOException ex) {
			System.out.println(MoodPredictor.class.getName());
			System.out.println("Error opening PrintWriter");
		}*/
	}
	
	// Dynamically creates the mood variations
	public String createMoodVariations() {
		StringBuilder moodVariations = new StringBuilder();
		moodVariationsList = new ArrayList<>();
		
		for (String mood : BASE_MOODS) {
			for (String modifier : MOOD_MODIFIERS) {
				moodVariations.append(modifier + " " + mood + ", ");
				moodVariationsList.add(modifier + " " + mood);
			}
			moodVariations.append(mood + ", ");
			moodVariationsList.add(mood);
		}
		
		// removes the extraneous comma and space
		return moodVariations.substring(0, moodVariations.length() - 2).toString();
	}

	
	public void writeHeadersToMoodPast() {
		writerMoodPast.println(FILE_HEADER);
		writerMoodPast.println();
		printDataType(writerMoodPast, EVENT_HEADERS);
		printDataType(writerMoodPast, SLEEP_HEADERS);
		printDataType(writerMoodPast, SOCIAL_HEADERS);
		printDataType(writerMoodPast, WEATHER_HEADERS);
		writerMoodPast.println("@attribute mood {" + createMoodVariations() + "}");
		writerMoodPast.println();
		writerMoodPast.println("@data");
	}
	
	public void writeHeadersToMoodPredict() {
		writerMoodPredict.println(FILE_HEADER);
		writerMoodPredict.println();
		printDataType(writerMoodPredict, EVENT_HEADERS);
		printDataType(writerMoodPredict, SLEEP_HEADERS);
		printDataType(writerMoodPredict, SOCIAL_HEADERS);
		printDataType(writerMoodPredict, WEATHER_HEADERS);
		writerMoodPredict.println("@attribute mood {" + createMoodVariations() + "}");
		writerMoodPredict.println();
		writerMoodPredict.println("@data");
	}
	
	private void printDataType(PrintWriter writer, String[] dataType) {
		for (String s : dataType) {
			writer.println(s);
		}
	}
	
	public void writePredictiveDataToPastMood(List<Event> events, Sleep sleepData, Social socialData, Weather weatherData, PastMood pastMood) {
		int eventRatings = getTotalEventRatings(events);
		writerMoodPast.println(String.format("%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d", eventRatings, sleepData.getSleeplength(), sleepData.getSleepCyles(),
				sleepData.getNoiseLevel(), socialData.getFacebookLikes(), socialData.getFacebookEvents(), socialData.getFacebookTimeLineUpdates(),
				weatherData.getSunriseTime(), weatherData.getSunsetTime(), weatherData.getPrecipIntensity(), weatherData.getPrecipProbablity(),
				weatherData.getTemperature(), weatherData.getHumidity(), weatherData.getCloudCover(), 
				weatherData.getVisibility(), pastMood.getPrediction()));
	}
	
	public void writePredictiveDataToPredictMood(List<Event> events, Sleep sleepData, Social socialData, Weather weatherData, PredictedMood predictedMood) {
		int eventRatings = getTotalEventRatings(events);
		writerMoodPredict.println(String.format("%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %s", eventRatings, sleepData.getSleeplength(), sleepData.getSleepCyles(),
				sleepData.getNoiseLevel(), socialData.getFacebookLikes(), socialData.getFacebookEvents(), socialData.getFacebookTimeLineUpdates(),
				weatherData.getSunriseTime(), weatherData.getSunsetTime(), weatherData.getPrecipIntensity(), weatherData.getPrecipProbablity(),
				weatherData.getTemperature(), weatherData.getHumidity(), weatherData.getCloudCover(), 
				weatherData.getVisibility(), "?"));
	}
	
	
	
	// Event rating should 0 to indicate no event is
	// happening on the specific day
	public int getTotalEventRatings(List<Event> events) {
		int sum = 0;
		for (Event e : events) {
			sum += e.getRating();
		}
		return sum;
	}
	
	public void closeWriters() {
		writerMoodPast.close();
		writerMoodPredict.close();
	}
	
	public Map<String, Long> predict() {
		Map<String, Long> moodProbabilities = new HashMap<>();
		try {
			// Load training data set
	        ConverterUtils.DataSource source = new ConverterUtils.DataSource(MOOD_PAST_FILE_LOCATION);
	        Instances trainDataSet = source.getDataSet();
	        // Set class index to the last index
	        System.out.println(trainDataSet.numAttributes());
	        trainDataSet.setClassIndex(trainDataSet.numAttributes() - 1);
	
	        // Build model
	        NaiveBayes nb = new NaiveBayes();
	        
	        nb.buildClassifier(trainDataSet);
	        System.out.println(nb);
	
	        // Load test data set
	        ConverterUtils.DataSource predictionSource = new ConverterUtils.DataSource(MOOD_PREDICT_FILE_LOCATION);
	        Instances predictionDataSet = predictionSource.getDataSet();
	
	
	        predictionDataSet.setClassIndex(predictionDataSet.numAttributes() - 1);
	        
	        // Iterating through predictive instances,
	        // and calculating the probability the user
	        // will feel a particular mood based on history
	        for (int x = 0; x < predictionDataSet.numInstances(); x++) {
	            Instance newInst = predictionDataSet.instance(x);
	            int index = 0;
	            for (double d : nb.distributionForInstance(newInst)) {
	            	long probability = Math.round(d*100);
	            	if (probability > 0) {
	            		moodProbabilities.put(moodVariationsList.get(index), probability);
	            	} 
	            	System.out.print(probability + " ");
	            	index++;
	            }
	            System.out.println();
	        }
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(MoodPredictor.class.getName());
			System.out.println("There was an error getting predictions.");
		}
		
		return moodProbabilities;
	}
	
	public static void main(String[] args) throws Exception {
		MoodPredictor moodPredictor = new MoodPredictor();
		moodPredictor.createMoodVariations();
	}
	

}
