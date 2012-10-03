/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * Exception thrown in case an object property couldn't be parsed.
 * @author Samuel Croset
 */
@SuppressWarnings("serial")
public class ObjectPropertyExpressionException extends BrainException {

    /**
     * @param e
     */
    public ObjectPropertyExpressionException(Exception e) {
	super(e);
    }

}
