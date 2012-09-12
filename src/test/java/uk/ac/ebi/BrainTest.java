/**
 * 
 */
package uk.ac.ebi;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.ExistingClassException;
import uk.ac.ebi.brain.error.NonExistingEntityException;

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
    
    @Test
    public void getOwlClassTest() throws ExistingClassException, NonExistingEntityException {
	brain.addOWLClass("A");
	OWLClass classA = brain.getOWLClass("A");
	assertNotNull(classA);
    }
    
    @Test
    public void addSubClassOfTest() throws NonExistingEntityException{
	brain.subClassOf("A", "B");
	assertNotNull(brain.getOWLClass("A"));
	
	brain.subClassOf("A", "part-of some B");
    }


}
