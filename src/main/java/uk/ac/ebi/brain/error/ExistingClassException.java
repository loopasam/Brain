/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * Exception thrown in case of a class is already present.
 * @author Samuel Croset
 *
 */
@SuppressWarnings("serial")
public class ExistingClassException extends ExistingEntityException {

    public ExistingClassException(String message) {
	super(message);
    }
    
}
