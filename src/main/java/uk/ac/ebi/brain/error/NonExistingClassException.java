/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * @author Samuel Croset
 *
 */
@SuppressWarnings("serial")
public class NonExistingClassException extends NonExistingEntityException {

    public NonExistingClassException(String message) {
	super(message);
    }

}
