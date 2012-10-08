/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * Exception thrown in case of non existing query for an entity.
 * @author Samuel Croset
 */
@SuppressWarnings("serial")
public class NonExistingEntityException extends BrainException {

    public NonExistingEntityException(String message) {
	super(message);
    }
}
