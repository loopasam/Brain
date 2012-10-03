/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * Exception thrown in case of an annotation property is already present.
 * @author Samuel Croset
 */
@SuppressWarnings("serial")
public class ExistingAnnotationPropertyException extends ExistingEntityException {

    /**
     * @param message
     */
    public ExistingAnnotationPropertyException(String message) {
	super(message);
    }

}
