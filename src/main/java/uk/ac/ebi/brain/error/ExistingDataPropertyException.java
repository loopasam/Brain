/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * Exception thrown in case of a data property is already present.
 * @author Samuel Croset
 *
 */
@SuppressWarnings("serial")
public class ExistingDataPropertyException extends ExistingEntityException {

    /**
     * @param message
     */
    public ExistingDataPropertyException(String message) {
	super(message);
    }

}
