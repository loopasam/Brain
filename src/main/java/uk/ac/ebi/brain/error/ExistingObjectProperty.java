/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * @author Samuel Croset
 *
 */
@SuppressWarnings("serial")
public class ExistingObjectProperty extends ExistingEntityException {

    public ExistingObjectProperty(String message) {
	super(message);
    }

}
