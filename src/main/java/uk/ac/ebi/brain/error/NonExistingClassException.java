/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * Exception thrown in case of non existing query for a class expression.
 * @author Samuel Croset
 */
@SuppressWarnings("serial")
public class NonExistingClassException extends NonExistingEntityException {

    public NonExistingClassException(String message) {
	super(message);
    }

}
