/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * Exception thrown in case of problematic data range.
 * @author Samuel Croset
 */
@SuppressWarnings("serial")
public class DataRangeException extends BrainException {

    /**
     * @param e
     */
    public DataRangeException(Exception e) {
	super(e);
    }

}
