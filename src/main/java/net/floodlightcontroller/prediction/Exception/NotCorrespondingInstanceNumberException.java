package net.floodlightcontroller.prediction.Exception;

/**
 * Generate Class for Handle the case in which the number of instances for the prediction is not one as expected
 * Created by Giovanni Liva on 03.06.15.
 */
public class NotCorrespondingInstanceNumberException extends Exception {
    //Parameterless Constructor
    public NotCorrespondingInstanceNumberException() {
        super("There must exists only one instance for the prediction");
    }

    //Constructor that accepts a message
    public NotCorrespondingInstanceNumberException(String message) {
        super(message);
    }
}