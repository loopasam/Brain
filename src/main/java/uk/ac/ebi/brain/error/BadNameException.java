/**
 * 
 */
package uk.ac.ebi.brain.error;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
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
	// TODO Auto-generated constructor stub
    }

    /**
     * @param e
     */
    public BadNameException(MalformedURLException e) {
	// TODO Auto-generated constructor stub
	super(e);
    }

    /**
     * @param e
     */
    public BadNameException(URISyntaxException e) {
	// TODO Auto-generated constructor stub
	super(e);
    }

}
