package moodprove.predict;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.functions.SMOreg;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import moodprove.to.Sleep;
import moodprove.to.Event;
import moodprove.to.PastMood;
import moodprove.to.Weather;
import moodprove.to.Social;

/**
 * 
 * @author malikg
 *
 */
public class MoodPredictor {
	
	private static final String FILE_HEADER = "@relation qdb-weka.filters.unsupervised.attribute.Remove-R1";
	
	private static final String[] EVENT_HEADERS = new String[] {"@attribute eventratings REAL"};
	
	private static final String[] SLEEP_HEADERS = new String[] {"@attribute sleepHours REAL", 
			"@attribute sleepCycles REAL", "@attribute sleepNoiseLevel REAL"};
	
	private static final String[] SOCIAL_HEADERS = new String[] {"@attribute facebookLikes REAL",
			"@attribute facebookEvents REAL", "@attribute facebookNewsFeed REAL"};
	
	private static final String[] WEATHER_HEADERS = new String[] {"@attribute sunriseTime REAL",
			"@attribute sunsetTime REAL", "@attribute precipIntensity REAL", "@attribute precipProbability REAL",
			"@attribute precipType STRING", "@attribute temperature REAL", "@attribute humidity REAL",
			"@attribute cloudCover REAL", "@attribute visibility REAL"};
	
	private PrintWriter writer;
	
	public MoodPredictor(String fileName) {
		try {
			this.writer = new PrintWriter(fileName, "UTF-8");
		}
		catch (IOException ex) {
			System.out.println(MoodPredictor.class.getName());
			System.out.println("Error opening PrintWriter for " + fileName);
		}
	}

	
	public void writeHeadersToFiles(String fileName) {
		writer.println(FILE_HEADER);
		writer.println();
		printDataType(EVENT_HEADERS);
		printDataType(SLEEP_HEADERS);
		printDataType(SOCIAL_HEADERS);
		printDataType(WEATHER_HEADERS);
		writer.println();
		writer.println();
		writer.println("@data");
	}
	
	private void printDataType(String[] dataType) {
		for (String s : dataType) {
			writer.println(s);
		}
	}
	
	public void writePredictiveData(List<Event> events, Sleep sleepData, Social socialData, Weather weatherData, PastMood pastMood) {
		int eventRatings = getTotalEventRatings(events);
		writer.println(String.format("%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %s, %d, %d, %d, %d, %d", eventRatings, sleepData.getSleeplength(), sleepData.getSleepCyles(),
				sleepData.getNoiseLevel(), socialData.getFacebookLikes(), socialData.getFacebookEvents(), socialData.getFacebookTimeLineUpdates(),
				weatherData.getSunriseTime(), weatherData.getSunsetTime(), weatherData.getPrecipIntensity(), weatherData.getPrecipProbablity(),
				weatherData.getPrecipType(), weatherData.getTemperature(), weatherData.getHumidity(), weatherData.getCloudCover(), 
				weatherData.getVisibility(), pastMood.getPrediction()));
	}
	
	public int getTotalEventRatings(List<Event> events) {
		int sum = 0;
		for (Event e : events) {
			sum += e.getRating();
		}
		return sum;
	}
	
	public void closeWriter() {
		writer.close();
	}
	
	public List<Long> predict() {
		List<Long> predictions = new ArrayList<>();
		try {
			// Load training data set
	        ConverterUtils.DataSource source = new ConverterUtils.DataSource("src/main/java/moodprove/predict/emotions-past.arff");
	        Instances trainDataSet = source.getDataSet();
	        // Set class index to the last index
	        trainDataSet.setClassIndex(trainDataSet.numAttributes() - 1);
	
	        // Build model
	        SMOreg nb = new SMOreg();
	        nb.buildClassifier(trainDataSet);
	        System.out.println(nb);
	
	        // Load test data set
	        ConverterUtils.DataSource predictionSource = new ConverterUtils.DataSource("src/main/java/moodprove/predict/emotions-predict.arff");
	        Instances predictionDataSet = predictionSource.getDataSet();
	
	
	        predictionDataSet.setClassIndex(predictionDataSet.numAttributes() - 1);
	        
	        for (int x = 0; x < predictionDataSet.numInstances(); x++) {
	            Instance newInst = predictionDataSet.instance(x);
	            double predNB = nb.classifyInstance(newInst);
	            predictions.add(Math.round(predNB));
	        }
		}
		catch (Exception ex) {
			System.out.println(MoodPredictor.class.getName());
			System.out.println("There was an error getting predictions.");
		}
		
		return predictions;
	}
	
	public static void main(String[] args) throws Exception {

	}

}
