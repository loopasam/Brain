/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * Exception thrown in case of non existing query for an object property.
 * @author Samuel Croset
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
