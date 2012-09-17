/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * @author Samuel Croset
 *
 */
@SuppressWarnings("serial")
public class NonExistingObjectPropertyException extends BrainException {

    /**
     * @param message
     */
    public NonExistingObjectPropertyException(String message) {
	super(message);
    }

}
