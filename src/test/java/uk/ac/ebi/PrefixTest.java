/**
 * 
 */
package uk.ac.ebi;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BadNameException;
import uk.ac.ebi.brain.error.BadPrefixException;
import uk.ac.ebi.brain.error.BrainException;

/**
 * @author Samuel Croset
 *
 */
public class PrefixTest {

    @Test
    public void prefixTest() throws BrainException {
	Brain chebi = new Brain("http://www.chebi.com/", "http://www.chebi.com/chebi.owl");
	chebi.addClass("A");
	chebi.save("src/test/resources/chebi.owl");
	Brain brain = new Brain();
	//TODO addClass(String prefix, String className);
	//TODO addPrefixMapping(String shortCut, String full);
	//TODO method as been trimmed add prefix via prefix mapping
	brain.learn("src/test/resources/chebi.owl");
	//TODO brain.learn(Brain brainToLearn);
	brain.addClass("A-prime");
	brain.save("src/test/resources/prefix.owl");
    }

    @Test(expected = BadNameException.class)
    public void wrongNamePrefixTest() throws BrainException {
	Brain brain = new Brain();
	brain.addClass("Blood Coagulation");
    }

    @Test(expected = BadNameException.class)
    public void wrongNameCustomPrefixTest() throws BrainException {
	Brain brain = new Brain();
	brain.setPrefix("http://www.example.org/");
	brain.addClass("Blood Coagulation");
    }

    @Test(expected = BadPrefixException.class)
    public void wrongCustomPrefixTest() throws BrainException {
	Brain brain = new Brain();
	brain.setPrefix("htp://www.example.org/");
    }

}
