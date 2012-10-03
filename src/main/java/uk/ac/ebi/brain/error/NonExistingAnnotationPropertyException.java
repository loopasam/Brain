/**
 * 
 */
package uk.ac.ebi.brain.error;


/**
 * Exception thrown in case of non existing query for an annotation property.
 * @author Samuel Croset
 */
@SuppressWarnings("serial")
public class NonExistingAnnotationPropertyException extends NonExistingEntityException {

    /**
     * @param message
     */
    public NonExistingAnnotationPropertyException(String message) {
	super(message);
    }

}
