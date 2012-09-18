/**
 * 
 */
package uk.ac.ebi.brain.error;

import java.net.MalformedURLException;

/**
 * @author Samuel Croset
 *
 */
@SuppressWarnings("serial")
public class BadPrefixException extends  BrainException {

    public BadPrefixException(String message) {
	super(message);
    }

    /**
     * @param e
     */
    public BadPrefixException(MalformedURLException e) {
	super(e);
    }

}
