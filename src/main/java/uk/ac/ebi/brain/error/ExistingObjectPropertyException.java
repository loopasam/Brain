/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * @author Samuel Croset
 *
 */
@SuppressWarnings("serial")
public class ExistingObjectPropertyException extends ExistingEntityException {

    public ExistingObjectPropertyException(String message) {
	super(message);
    }

}
