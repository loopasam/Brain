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
    public void bootstrap() throws BrainException, MalformedURLException, URISyntaxException {
	brain = new Brain("http://www.example.org/");
	brain.addClass("Nucleus");
	brain.addClass("Cell");
	brain.addClass("Blood Coagulation");
	brain.addClass("Wound Healing");
	brain.addObjectProperty("part-of");
//	brain.subClassOf("Blood Coagulation", "part-of some Cell");
	brain.save("src/test/resources/output.owl");
    }

    @Test
    public void test() throws BrainException {
	
    }

}
