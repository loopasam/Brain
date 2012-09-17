/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * @author Samuel Croset
 *
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
