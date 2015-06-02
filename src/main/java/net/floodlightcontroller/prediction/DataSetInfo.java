package net.floodlightcontroller.prediction;

import net.floodlightcontroller.prediction.Exception.NotCorrespondingLagSize;

import java.io.File;
import java.io.IOException;
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

    public String generateARFFFromData(String[] data) throws NotCorrespondingLagSize, IOException {
        if(data.length != lags){
            throw new NotCorrespondingLagSize();
        }
        long time = new java.util.Date().getTime();
        int rand = new Random().nextInt(10000);
        File temp = File.createTempFile("prediction" + time + "_" + rand, ".arff");
        return temp.getAbsolutePath();
    }


    /*
    @relation dataset_1
@attribute time_class {'time_0','time_1','time_2','time_3','time_4','time_5','time_6','time_7','time_8','time_9','time_10','time_11','time_12','time_13','time_14','time_15','time_16','time_17','time_18','time_19','time_20','time_21','time_22','time_23'}
@attribute bandwidth_0 numeric
@attribute d_bandwidth_0 numeric
@attribute bandwidth_1 numeric
@attribute d_bandwidth_1 numeric
@attribute bandwidth_2 numeric
@attribute d_bandwidth_2 numeric
@attribute bandwidth_3 numeric
@attribute d_bandwidth_3 numeric
@attribute bandwidth_4 numeric
@attribute prediction_class {'byte_0','byte_500','byte_1000','byte_1500','byte_2000','byte_2500','byte_3000','byte_3500','byte_4000','byte_4500','byte_5000','byte_5500','byte_6000','byte_6500','byte_7000','byte_7500','byte_8000','byte_8500','byte_9000','byte_9500','byte_10000'}
@data
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
            if(this.derivative){
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
