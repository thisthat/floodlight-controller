package net.floodlightcontroller.prediction;

import net.floodlightcontroller.prediction.Exception.NotCorrespondingInstanceNumberException;
import net.floodlightcontroller.prediction.PredictionModule.SwitchNode;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * Handle the Prediction feature for all the switches
 */
public class PredictionHandler {
	private static final String _folder = "prediction/";
	protected Map<String, PredictionNode> prediction = new HashMap<String, PredictionNode>();
	protected MongoDBInfo mongodb;

	/**
	 * Handle the Prediction Strucutre for a node
	 */
	public class PredictionNode {
		private AbstractClassifier classifier;
		private List<String> modelPath = new ArrayList<>();
		private List<AbstractClassifier> classifiersList = new ArrayList<>();
		private int _indexModel = 0;
		private static final String defaultUri = _folder + "default.model";
		private java.util.Date lastLoad = new java.util.Date(0);
		private DataSetInfo ds = new DataSetInfo();
		private MongoDBInfo mongodb;
		private String dpid;

		/**
		 * Create the classifier from file and generate the list of classifiers
		 * @param db: MongoDB instance
		 * @param id: DPID of the node
		 */
		public PredictionNode(MongoDBInfo db, String id){
			this.mongodb = db;
			this.dpid = id;
			this.modelPath.add(defaultUri);
			//Check the folder
			File f = new File(_folder + id);
			//Check if we have the forlder, if not create it and load default model
			if(f.exists()){
				//Check all file inside the switch folder for classifier models
				File[] listOfFiles = f.listFiles();
				for (int i = 0; i < listOfFiles.length; i++) {
					if (listOfFiles[i].isFile()){ //Must be a file
						String file = listOfFiles[i].getName();
						//And it's extension a .model file
						if(file.substring(file.indexOf('.')).equals(".model")){
							System.out.println("[FOUND] Model for node <" + this.dpid + "> :: " + file);
							String tmp = _folder + this.dpid + "/" + file;
							modelPath.add(tmp);
							try {
								AbstractClassifier tmpClassfier = (AbstractClassifier) SerializationHelper.read(new FileInputStream(tmp));
								classifiersList.add(tmpClassfier);
							} catch(Exception e) {}
						}
					}
				}
				if(modelPath.size() == 1){
					System.out.println("[LOAD] Default model for node <" + this.dpid + ">");
					loadClassifierFromFile(defaultUri);
					try{
						AbstractClassifier tmpClassfier = (AbstractClassifier) SerializationHelper.read(new FileInputStream(defaultUri));
						classifiersList.add(tmpClassfier);
					} catch(Exception e) {}
				}
				else {
					_indexModel = 1;
					System.out.println("[LOAD] First model for node <" + this.dpid + "> :: " + modelPath.get(_indexModel));
					loadClassifierFromFile(modelPath.get(_indexModel));
				}
			}
			else {
				f.mkdir();
				loadClassifierFromFile(defaultUri);
			}

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
		 * @return Search in the directory and build the list of avaiable elements
		 */
		public List<String> getAllModelsPath() {
			this.modelPath.clear();
			this.modelPath.add(defaultUri);
			//Check the folder
			File f = new File(_folder + this.dpid);
			//Check if we have the forlder, if not create it and load default model
			if(f.exists()){
				//Check all file inside the switch folder for classifier models
				File[] listOfFiles = f.listFiles();
				for (int i = 0; i < listOfFiles.length; i++) {
					if (listOfFiles[i].isFile()){ //Must be a file
						String file = listOfFiles[i].getName();
						//And it's extension a .model file
						if(file.substring(file.indexOf('.')).equals(".model")){
							System.out.println("[FOUND] Model for node <" + this.dpid + "> :: " + file);
							modelPath.add(_folder + this.dpid + "/" + file);
						}
					}
				}
			}
			return modelPath;
		}

		/**
		 *
		 * @return the number of avaiable classifier
		 */
		public int getNumberOfAvaiableModels(){
			return this.modelPath.size();
		}

		/**
		 * Set the model
		 * @param index: number of model to use
		 */
		public void setModel(int index){
			this._indexModel = index;
			loadClassifierFromFile();
		}

		/**
		 *
		 * @return get the current model index
		 */
		public int getIndexModel(){
			return _indexModel;
		}

		/**
		 * Getter
		 * @return the current used model
		 */
		public String getModelPath() {
			return modelPath.get(_indexModel);
		}

		/**
		 * Getter
		 * @return the timestamp on when the model is loaded
		 */
		public String getLoadedTimeStr(){
			String t = lastLoad.getTime() + "";
			return t.substring(0, t.length() - 3);
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
			loadClassifierFromFile(this.modelPath.get(_indexModel));
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
		 * Return the name of the class of traffic for the prediction
		 * @return the name of the class of the prediction
		 * @throws Exception if it cannot write the file or the number of instance is not one
		 */
		public String executePredictionClassName() throws Exception {
			String[] data = mongodb.getSwitchLastMeasurement(this.dpid, ds.getLags());
			//Generate file
			String path = ds.generateARFFFromData(data);
			//Execute the prediction
			return executePredictionClassName(path);
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
			double classPred = 0;
			classPred = classifier.classifyInstance(dataset.instance(0));
			return (int)classPred;
		}
		/**
		 * Return the index of the class for the prediction
		 * @return the name of the class of the prediction
		 * @throws Exception : if the number of instances is not one
		 */
		public int executePredictionClassIndex() throws Exception {
			String[] data = mongodb.getSwitchLastMeasurement(this.dpid, ds.getLags());
			//Generate file
			String path = ds.generateARFFFromData(data);
			//Execute the prediction
			return executePredictionClassIndex(path);
		}

		/**
		 * Return the index class list of all classifiers
		 * @return List of predicted class
		 * @throws Exception: if something goes wrong
		 */
		public List<String> executePredictionListIndex() throws Exception {
			List<String> indexes = new ArrayList<>();
			//Get the data
			String[] data = mongodb.getSwitchLastMeasurement(this.dpid, ds.getLags());
			String path = ds.generateARFFFromData(data);
			ConverterUtils.DataSource ds = new ConverterUtils.DataSource(path);
			Instances dataset = ds.getDataSet();
			dataset.setClassIndex(dataset.numAttributes() - 1);
			//There must exists only one instance
			if(dataset.numInstances() != 1){
				throw new NotCorrespondingInstanceNumberException();
			}
			//Classify the data
			for(int i = 0; i < classifiersList.size(); i++){
				AbstractClassifier c = classifiersList.get(i);
				double classPred = 0;
				classPred = c.classifyInstance(dataset.instance(0));
				indexes.add( (int)classPred + "");
			}
			return indexes;
		}

		/**
		 * Return the class name list of all classifiers
		 * @return List of names of predicted class
		 * @throws Exception: if something goes wrong
		 */
		public List<String> executePredictionListClassName() throws Exception {
			List<String> names = new ArrayList<>();
			//Get the data
			String[] data = mongodb.getSwitchLastMeasurement(this.dpid, ds.getLags());
			String path = ds.generateARFFFromData(data);
			ConverterUtils.DataSource ds = new ConverterUtils.DataSource(path);
			Instances dataset = ds.getDataSet();
			dataset.setClassIndex(dataset.numAttributes() - 1);
			//There must exists only one instance
			if(dataset.numInstances() != 1){
				throw new NotCorrespondingInstanceNumberException();
			}
			//Classify the data
			for(int i = 0; i < classifiersList.size(); i++){
				AbstractClassifier c = classifiersList.get(i);
				double classPred = 0;
				classPred = c.classifyInstance(dataset.instance(0));
				names.add(dataset.classAttribute().value((int) classPred));
			}
			return names;
		}


	}


	public PredictionHandler(MongoDBInfo db){ this.mongodb = db; }

	/**
	 * Set the current switches in the network
	 * @param switches: List of  the current switches in the network
	 */
	public void setSwitch(List<SwitchNode> switches){
		//Search for new elements 
		for(SwitchNode s : switches){
			String name = s.getName();
			if(!prediction.containsKey(name)){
				PredictionNode p = new PredictionNode(this.mongodb, name);
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
		System.out.println("Prediction load " + prediction.size() + " switches");
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
