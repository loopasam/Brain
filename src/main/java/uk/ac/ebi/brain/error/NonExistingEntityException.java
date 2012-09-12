/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * @author Samuel Croset
 *
 */
@SuppressWarnings("serial")
public class NonExistingEntityException extends Exception {

    public NonExistingEntityException(String message) {
	super(message);
    }
}
