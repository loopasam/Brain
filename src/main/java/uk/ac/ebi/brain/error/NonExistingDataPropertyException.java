/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * Exception thrown in case of non existing query for a data property.
 * @author Samuel Croset
 */
@SuppressWarnings("serial")
public class NonExistingDataPropertyException extends NonExistingEntityException {

    /**
     * @param message
     */
    public NonExistingDataPropertyException(String message) {
	super(message);
    }

}
