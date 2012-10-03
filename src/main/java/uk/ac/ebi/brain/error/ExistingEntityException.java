/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * Exception thrown in case of an entity is already present.
 * @author Samuel Croset
 *
 */
@SuppressWarnings("serial")
public class ExistingEntityException extends BrainException {

    /**
     * @param message
     */
    public ExistingEntityException(String message) {
	super(message);
    }

}
