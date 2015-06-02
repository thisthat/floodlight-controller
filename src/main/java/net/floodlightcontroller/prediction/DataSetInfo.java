package net.floodlightcontroller.prediction;

import net.floodlightcontroller.prediction.Exception.NotCorrespondingLagSizeException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 *
 * Created by Giovanni Liva on 02.06.15.
 * Class To store the information about how to build the dataset
 */
public class DataSetInfo {
    private int lags = 5;
    private boolean derivative = true;
    private int classSize = 500;

    public DataSetInfo(){
    }
    public DataSetInfo(int _lags, boolean _der, int _classSize){
        this.lags = _lags;
        this.derivative = _der;
        this.classSize = _classSize;
    }

    public void setLags(int l){
        this.lags = l;
    }
    public void setDerivative(boolean f){
        this.derivative = f;
    }
    public void setClassSize(int size){
        this.classSize = size;
    }

    public int getLags(){
        return lags;
    }
    public int getClassSize(){
        return classSize;
    }
    public boolean getDerivative(){
        return derivative;
    }


    public String generateARFFFromData(String[] data) throws NotCorrespondingLagSizeException, IOException {
        return generateARFFFromData(data, new Date().getTime() / 1000);
    }

    public String generateARFFFromData(String[] data, long timeClass) throws NotCorrespondingLagSizeException, IOException {
        if(data.length != lags){
            throw new NotCorrespondingLagSizeException();
        }
        long time = new java.util.Date().getTime();
        int rand = new Random().nextInt(10000);
        File temp = File.createTempFile("prediction" + time + "_" + rand, ".arff");
        FileWriter fw = new FileWriter(temp.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        //Write Header
        bw.write(generateHeader());
        //Write Instance
        bw.write(generateInstance(timeClass, data));
        bw.close();
        return temp.getAbsolutePath();
    }

    /**
     * Generate the instance
     * @return the string with the instance in ARFF format
     */
    private String generateInstance(long time, String[] data) {
        String out = getClassTime(time) + ",";
        for(int i = 0; i < data.length; i++){
            //Data is in Byte, so convert to KByte
            out += ( Integer.parseInt(data[i]) / 1024 ) + ",";
            if(this.derivative && i > 0){
                out += ( Integer.parseInt(data[i]) - Integer.parseInt(data[i-1]) ) / 1024 + ",";
            }
        }
        out += "?"; //The Prediction!
        return out;
    }

    /**
     * Convert the current unix timestamp (seconds) to the correspondent class for our weka model
     * @return class in terms of ARFF model
     */
    private String getClassTime(){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(new Date().getTime());
        return "time_" + c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Convert the unix timestamp (seconds) to the correspondent class for our weka model
     * @param time : Time of the day in second
     * @return class in terms of ARFF model
     */
    private String getClassTime(long time){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time*1000);
        return "time_" + c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Generate ARFF File Header for our model
     * @return the string that contents the header of the ARFF file
     */
    public String generateHeader(){
        String out = "@relation dataset_1\n";
        //Generate time class
        out += "@attribute time_class {";
        for(int i = 0; i < 24; i++){
            out += "'time_" + i + "'";
            if(i < 23){
                out += ",";
            }
        }
        out += "}\n";
        //Generate bandwidth
        for(int i  = 0; i < this.lags; i++){
            out += "@attribute bandwidth_" + i + " numeric\n";
            if(this.derivative && i > 0){
                out += "@attribute d_bandwidth_" + i + " numeric\n";
            }
        }
        //Generate prediction class
        out += "@attribute prediction_class {";
        for(int i = this.classSize; i <= 10000; i += this.classSize){
            out += "'byte_" + i + "'";
            if(i < 10000){
                out += ",";
            }
        }
        out += "}\n";
        out += "@data\n";
        return out;
    }


}
