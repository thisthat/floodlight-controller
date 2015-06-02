package net.floodlightcontroller.prediction.Exception;

/**
 * Generate Class for Handle the case in which the lags are not corresponding
 * Created by Giovanni Liva on 03.06.15.
 */
public class NotCorrespondingLagSize extends Exception {
    //Parameterless Constructor
    public NotCorrespondingLagSize() {
        super("The array passed is not the size of lags + 1");
    }

    //Constructor that accepts a message
    public NotCorrespondingLagSize(String message) {
        super(message);
    }
}