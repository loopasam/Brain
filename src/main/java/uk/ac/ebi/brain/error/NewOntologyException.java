/**
 * 
 */
package uk.ac.ebi.brain.error;

/**
 * Exception thrown in case of problem while creating a new ontology.
 * @author Samuel Croset
 *
 */
@SuppressWarnings("serial")
public class NewOntologyException extends BrainException {

    /**
     * @param e
     */
    public NewOntologyException(Exception e) {
	super(e);
    }

}
