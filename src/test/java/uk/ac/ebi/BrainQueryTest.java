/**
 * 
 */
package uk.ac.ebi;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;


import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.ClassExpressionException;

/**
 * @author Samuel Croset
 *
 */
public class BrainQueryTest {

    Brain brain;

    @Before
    public void bootstrap() throws BrainException {
	brain = new Brain("http://www.test.org/", "public/test.owl");
	brain.learn("src/test/resources/dev.owl");
    }

    @Test
    public void owlProfileTest() throws BrainException {
	boolean satisfied = brain.hasElProfile();
	assertEquals(true, satisfied);
	List<String> violations = brain.getElProfileViolations();
	assertEquals(0, violations.size());
    }
    
    @Test
    public void getSubClassesTest() throws ClassExpressionException{
	List<String> subClasses = brain.getSubClasses("I", true);
	assertEquals(1, subClasses.size());
	List<String> subClasses1 = brain.getSubClasses("G", false);
	assertEquals(2, subClasses1.size());
    }
    
    @Test
    public void getSuperClassesTest(){
	
    }

    @Test
    public void getEquivalentClassesTest(){
	
    }

}
