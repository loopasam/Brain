/**
 * 
 */
package uk.ac.ebi;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.ExistingClassException;

/**
 * @author Samuel Croset
 *
 */
public class BrainTest {

    Brain brain;

    @Before
    public void bootstrap() throws OWLOntologyCreationException {
	brain = new Brain("src/test/resources/dev.owl");
    }

    @Test
    public void addClassTest() throws ExistingClassException{
	brain.addOWLClass("A");
	assertEquals(true, brain.getOntology().containsClassInSignature(IRI.create(brain.getPrefixManager().getDefaultPrefix() + "A")));
    }

    @Test(expected = ExistingClassException.class)
    public void addExistingClassTest() throws ExistingClassException{
	brain.addOWLClass("A");
	assertEquals(true, brain.getOntology().containsClassInSignature(IRI.create(brain.getPrefixManager().getDefaultPrefix() + "A")));
	brain.addOWLClass("A");
    }


}
