/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * Exception thrown in case of storage problem.
 * @author Samuel Croset
 */
@SuppressWarnings("serial")
public class StorageException extends BrainException {

    /**
     * @param e
     */
    public StorageException(Exception e) {
	super(e);
    }

}
