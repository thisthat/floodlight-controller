package net.floodlightcontroller.prediction;

/**
 *
 * Created by Giovanni Liva on 02.06.15.
 * Class To store the information about how to build the dataset
 */
public class DataSetInfo {
    private int lags = 5;
    private boolean derivative = true;
    private int classSize = 500;

    public DataSetInfo(){}
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
}
