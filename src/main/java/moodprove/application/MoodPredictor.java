package moodprove.application;

import weka.classifiers.functions.SMOreg;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class MoodPredictor {
	
	public static void main(String[] args) throws Exception {
	    // Load training data set
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("src/main/java/moodprove/application/emotions-past.arff");
        Instances trainDataSet = source.getDataSet();
        // Set class index to the last index
        trainDataSet.setClassIndex(trainDataSet.numAttributes() - 1);

        // Build model
        SMOreg nb = new SMOreg();
        nb.buildClassifier(trainDataSet);
        System.out.println(nb);

        // Load test data set
        ConverterUtils.DataSource predictionSource = new ConverterUtils.DataSource("src/main/java/moodprove/application/emotions-predict.arff");
        Instances predictionDataSet = predictionSource.getDataSet();


        predictionDataSet.setClassIndex(predictionDataSet.numAttributes() - 1);

        for (int x = 0; x < predictionDataSet.numInstances(); x++) {
            double actualValue = predictionDataSet.instance(x).classValue();

            Instance newInst = predictionDataSet.instance(x);

            double predNB = nb.classifyInstance(newInst);

            System.out.println("-----------------------------");
            System.out.println(predNB);
            //sendData(predString);
        }


	}

}
