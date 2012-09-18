/**
 * 
 */
package uk.ac.ebi;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BadPrefixException;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.ExistingClassException;
import uk.ac.ebi.brain.error.NewOntologyException;

/**
 * @author Samuel Croset
 *
 */
public class BrainQueryTest {

    Brain brain;

    @Before
    public void bootstrap() throws BrainException {
	brain = new Brain("http://www.domain1.com/", "onto1.owl");
	brain.addClass("A2");
	brain.learn("src/test/resources/onto2.owl");
	brain.label("A2", "labelled");
//	brain.learn("src/test/resources/output.owl");
	brain.save("src/test/resources/test.owl");
    }

    @Test
    public void test() throws BrainException {
	
    }

}
