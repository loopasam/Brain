/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * Generic type of expression
 * @author Samuel Croset
 *
 */
@SuppressWarnings("serial")
public class BrainException extends Exception {

    public BrainException(String message) {
	super(message);
    }

    /**
     * @param e
     */
    public BrainException(Exception e) {
	super(e);
    }
}
