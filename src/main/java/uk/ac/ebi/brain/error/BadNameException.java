/**
 * 
 */
package uk.ac.ebi.brain.error;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * Exception thrown in case of problematic name for OWL entities.
 * @author Samuel Croset
 *
 */
@SuppressWarnings("serial")
public class BadNameException extends BrainException {

    /**
     * @param message
     */
    public BadNameException(String message) {
	super(message);
    }

    /**
     * @param e
     */
    public BadNameException(MalformedURLException e) {
	super(e);
    }

    /**
     * @param e
     */
    public BadNameException(URISyntaxException e) {
	super(e);
    }

}
