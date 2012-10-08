/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * Exception thrown in case a data property couldn't be parsed.
 * @author Samuel Croset
 *
 */
@SuppressWarnings("serial")
public class DataPropertyExpressionException extends BrainException {

    /**
     * @param e
     */
    public DataPropertyExpressionException(Exception e) {
	super(e);
    }

}
