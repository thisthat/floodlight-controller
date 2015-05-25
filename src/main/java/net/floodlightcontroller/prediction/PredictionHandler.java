package net.floodlightcontroller.prediction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.prediction.PredictionModule.SwitchNode;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.SerializationHelper;

public class PredictionHandler {
	private String _folder = "prediction/";
	protected Map<String, MultilayerPerceptron> prediction = new HashMap<String, MultilayerPerceptron>();
	
	public PredictionHandler(){};
	
	public void setSwitch(List<SwitchNode> switches){
		
		//Search for new elements 
		for(SwitchNode s : switches){
			String name = s.getName();
			if(!prediction.containsKey(name)){
				//Search for existing model for that switch
				//Standard is {dpid}.model
				String fileModel = _folder + name + ".model";
				File f = new File(fileModel);
				MultilayerPerceptron p = null;
				if(f.exists() && !f.isDirectory()) { 
					//Model Exists -> Create a new Classifier
					try {
						//p = (MultilayerPerceptron) SerializationHelper.read(new FileInputStream(fileModel));
						p = new MultilayerPerceptron();
					} catch (Exception e) {
						//TODO -> Do something :p
					}
				}
				else {
					//Load a default model
					try {
						//p = (MultilayerPerceptron) SerializationHelper.read(new FileInputStream("default.model"));
					} catch (Exception e) {}
				}
				prediction.put(name, p);
			}
		}
		//Delete not more existing elements
		boolean exists;
		for(Iterator<Map.Entry<String,MultilayerPerceptron>> it = prediction.entrySet().iterator(); it.hasNext();){
			Map.Entry<String, MultilayerPerceptron> entry = it.next();
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

}
