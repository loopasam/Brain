/**
 * Copyright [2013] EMBL - European Bioinformatics Institute
 * Licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of 
 * the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on 
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
 * KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations 
 * under the License.
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
