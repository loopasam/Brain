/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * @author Samuel Croset
 *
 */
@SuppressWarnings("serial")
public class NonExistingEntityException extends BrainException {

    public NonExistingEntityException(String message) {
	super(message);
    }
}
