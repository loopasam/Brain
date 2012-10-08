/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * Exception thrown in case a class expression couldn't be parsed.
 * @author Samuel Croset
 */
@SuppressWarnings("serial")
public class ClassExpressionException extends BrainException {

    public ClassExpressionException(Exception e) {
	super(e);
    }
}
