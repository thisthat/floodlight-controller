package net.floodlightcontroller.prediction;

import net.floodlightcontroller.prediction.Exception.NotCorrespondingInstanceNumberException;
import net.floodlightcontroller.prediction.PredictionModule.SwitchNode;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Handle the Prediction feature for all the switches
 */
public class PredictionHandler {
	private String _folder = "prediction/";
	protected Map<String, PredictionNode> prediction = new HashMap<String, PredictionNode>();

	/**
	 * Handle the Prediction Strucutre for a node
	 */
	public class PredictionNode {
		private AbstractClassifier classifier;
		private boolean isLearning = false;
		private String modelPath = "";
		private static final String defaultUri = "prediction/default.model";
		private java.util.Date lastLoad = new java.util.Date(0);
		private DataSetInfo ds = new DataSetInfo();


		public PredictionNode(){}

		/**
		 * Create the classifier from file and setting the learning as False
		 * @param uri: file path of the model
		 */
		public PredictionNode(String uri){
			modelPath = uri;
			loadClassifierFromFile(uri);
		}

		/**
		 * Create the classifier from file
		 * @param uri: file path of the model
		 * @param learning: boolean flag to toggle the continous learning
		 */
		public PredictionNode(String uri, boolean learning){
			modelPath = uri;
			loadClassifierFromFile(uri);
			isLearning = learning;
		}

		/**
		 * Getter
		 * @return class name of classifier
		 */
		public String getClassifierName(){
			if(classifier == null){
				return "No Classifier Yet!";
			}
			return classifier.getClass().getName();
		}
		/**
		 * Getter
		 * @return the state of learning
		 */
		public boolean getLearning(){
			return isLearning;
		}

		/**
		 * Getter
		 * @return the path of the model
		 */
		public String getModelPath() {
			return modelPath;
		}

		/**
		 * Getter
		 * @return the timestamp on when the model is loaded
		 */
		public String getLoadedTimeStr(){
			String t = lastLoad.getTime() + "";
			return t.substring(0, t.length()-3);
		}

		/**
		 * Getter
		 * @return the current info of the dataset for the node is build
		 */
		public DataSetInfo getDatasetInfo(){
			return ds;
		}

		/**
		 * Setter
		 * @param d: DataSetInfo with the new dataset to store w/ the node
		 */
		public void setDatasetInfo(DataSetInfo d){
			this.ds = d;
		}

		/**
		 * Setter
		 * @param lags: Number of lags
		 * @param derivative: To use or not the derivative
		 * @param classSize: Size of buckets in the learning algorithm
		 */
		public void setDatasetInfo(int lags, boolean derivative, int classSize){
			this.ds = new DataSetInfo(lags,derivative,classSize);
		}

		/**
		 * Load a classifier from file
		 * @param uri: file path of the model
		 */
		public void loadClassifierFromFile(String uri){
			File f = new File(uri);
			if(f.exists() && !f.isDirectory()) {
				//Model Exists -> Create a new Classifier
				try {
					classifier = (AbstractClassifier) SerializationHelper.read(new FileInputStream(uri));
					lastLoad = new java.util.Date();
				} catch (Exception e) {
					System.err.println("Error in loading the model " + uri);
				}
			}
			else {
				//Load a default model
				try {
					classifier = (AbstractClassifier) SerializationHelper.read(new FileInputStream(defaultUri));
					lastLoad = new java.util.Date();
				} catch (Exception e) {
					System.err.println("Error in loading the default model");
				}
			}
		}
		public void loadClassifierFromFile(){
			loadClassifierFromFile(this.modelPath);
		}

		/**
		 * Return the name of the class of traffic for the prediction
		 * @param filePath : file path of the arff file
		 * @return the name of the class of the prediction
		 * @throws Exception : if the number of instances is not one
		 */
		public String executePredictionClassName(String filePath) throws Exception {
			ConverterUtils.DataSource ds = new ConverterUtils.DataSource(filePath);
			Instances dataset = ds.getDataSet();
			dataset.setClassIndex(dataset.numAttributes() - 1);
			//There must exists only one instance
			if(dataset.numInstances() != 1){
				throw new NotCorrespondingInstanceNumberException();
			}
			double classPred = classifier.classifyInstance(dataset.instance(0));
			return dataset.classAttribute().value((int) classPred);
		}

		/**
		 * Return the index of the class for the prediction
		 * @param filePath : file path of the arff file
		 * @return the name of the class of the prediction
		 * @throws Exception : if the number of instances is not one
		 */
		public int executePredictionClassIndex(String filePath) throws Exception {
			ConverterUtils.DataSource ds = new ConverterUtils.DataSource(filePath);
			Instances dataset = ds.getDataSet();
			dataset.setClassIndex(dataset.numAttributes() - 1);
			//There must exists only one instance
			if(dataset.numInstances() != 1){
				throw new NotCorrespondingInstanceNumberException();
			}
			double classPred = classifier.classifyInstance(dataset.instance(0));
			return (int)classPred;
		}



	}


	public PredictionHandler(){	}

	/**
	 * Set the current switches in the network
	 * @param switches: List of  the current switches in the network
	 */
	public void setSwitch(List<SwitchNode> switches){
		//Search for new elements 
		for(SwitchNode s : switches){
			String name = s.getName();
			if(!prediction.containsKey(name)){
				//Standard is {dpid}.model
				String fileModel = _folder + name + ".model";
				PredictionNode p = new PredictionNode(fileModel);
				prediction.put(name, p);
			}
		}
		//Delete the not more existing elements
		boolean exists;
		for(Iterator<Map.Entry<String,PredictionNode>> it = prediction.entrySet().iterator(); it.hasNext();){
			Map.Entry<String, PredictionNode> entry = it.next();
			exists = false;
			for(SwitchNode s : switches){
				String name = s.getName();
				if(entry.getKey().equals(name)){
					exists = true;
				}
			}
			if(!exists){
				it.remove();
			}
		}
		System.out.println("======");
		System.out.println(prediction.toString());
		System.out.println("======");
	}

	/**
	 * Get the prediction info from a switch
	 * @param dpid: Switch DPID
	 * @return Classifier information for that switch
	 */
	public PredictionNode getSwitch(String dpid){
		if(prediction.containsKey(dpid)){
			return prediction.get(dpid);
		}
		return null;
	}

	/**
	 * Get all the prediction info
	 * @return Classifiers of all switches
	 */
	public Map<String, PredictionNode> getSwitches(){
		return prediction;
	}
}
