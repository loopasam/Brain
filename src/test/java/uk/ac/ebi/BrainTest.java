/**
 * 
 */
package uk.ac.ebi;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

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
	brain = new Brain("http://www.example.org#");
    }

    @Test
    public void addClassTest() throws ExistingClassException{
	brain.addOWLClass("A");
	assertEquals(true, brain.getOntology().containsClassInSignature(IRI.create(brain.getPrefixManager().getDefaultPrefix() + "A")));
    }

    @Test
    public void addExistingClassTest() throws ExistingClassException{
	OWLClass owlClass1 = brain.addOWLClass("A");
	assertEquals(true, brain.getOntology().containsClassInSignature(IRI.create(brain.getPrefixManager().getDefaultPrefix() + "A")));
	OWLClass owlClass2 = brain.addOWLClass("A");
	assertEquals(owlClass1, owlClass2);
    }

    @Test
    public void getOwlClassTest() throws NonExistingEntityException {
	brain.addOWLClass("A");
	OWLClass classA = brain.getOWLClass("A");
	assertNotNull(classA);
    }
    
    @Test(expected = NonExistingEntityException.class)
    public void getNonExsistingOwlClassTest() throws NonExistingEntityException {
	@SuppressWarnings("unused")
	OWLClass classA = brain.getOWLClass("A");
    }

    @Test
    public void subClassOfTest() throws ParserException{
	brain.addOWLClass("A");
	brain.addOWLClass("B");
	brain.subClassOf("A", "B");
    }
    
    @Test
    public void saveOntology() throws OWLOntologyStorageException, ParserException{
	brain.addOWLClass("A");
	brain.addOWLClass("B");
	brain.subClassOf("A", "B");
	brain.save("src/test/resources/output.owl");
    }
    
    @Test
    public void testProp() throws OWLOntologyStorageException, ParserException {
	brain.addOWLClass("A");
	brain.addOWLClass("B");
	brain.addOWLObjectProperty("part-of");
	brain.makeTransitive("part-of");
	brain.subClassOf("A", "part-of some B");
	brain.save("src/test/resources/output.owl");
    }



}
