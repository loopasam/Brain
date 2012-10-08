/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * Exception thrown in case of an object property is already present.
 * @author Samuel Croset
 *
 */
@SuppressWarnings("serial")
public class ExistingObjectPropertyException extends ExistingEntityException {

    public ExistingObjectPropertyException(String message) {
	super(message);
    }

}
