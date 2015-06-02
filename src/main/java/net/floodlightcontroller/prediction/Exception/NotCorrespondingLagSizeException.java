package net.floodlightcontroller.prediction.Exception;

/**
 * Generate Class for Handle the case in which the lags are not corresponding
 * Created by Giovanni Liva on 03.06.15.
 */
public class NotCorrespondingLagSizeException extends Exception {
    //Parameterless Constructor
    public NotCorrespondingLagSizeException() {
        super("The array passed is not the size of lags");
    }

    //Constructor that accepts a message
    public NotCorrespondingLagSizeException(String message) {
        super(message);
    }
}