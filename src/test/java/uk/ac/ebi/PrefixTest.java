/**
 * 
 */
package uk.ac.ebi;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClass;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BadNameException;
import uk.ac.ebi.brain.error.BadPrefixException;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.NewOntologyException;

/**
 * @author Samuel Croset
 *
 */
public class PrefixTest {

    //TODO method as been trimmed add prefix via prefix mapping
    //TODO brain.learn("src/test/resources/chebi.owl");
    //TODO brain.learn(Brain brainToLearn);
    //TODO addExternalAnno
    //TODO addExternalProp
    //TODO the rest


    @Test
    public void prefixTest() throws BrainException {
	Brain chebi = new Brain("http://www.chebi.com/", "http://www.chebi.com/chebi.owl");
	chebi.addClass("A");
	chebi.save("src/test/resources/chebi.owl");	
    }

    @Test
    public void addExternalClassWithPrefixTest() throws BrainException {
	Brain brain = new Brain("http://www.example.org/", "http://www.example.org/public/example.owl");
	brain.addExternalClass("http://www.chebi.org/CHEBI:45");
	OWLClass owlClass = brain.getOWLClass("CHEBI:45");
	assertNotNull(owlClass);
    }

    @Test
    public void addClassWithPrefixTest() throws BrainException {
	Brain brain = new Brain("http://www.example.org/", "http://www.example.org/public/example.owl");
	brain.addExternalClass("http://www.chebi.org/CHEBI:45");
	OWLClass owlClass = brain.getOWLClass("CHEBI:45");
	assertNotNull(owlClass);
    }

    @Test
    public void addPrefixTest() throws BrainException {
	Brain brain = new Brain("http://www.example.org/", "http://www.example.org/public/example.owl");
	brain.addExternalClass("http://www.chebi.org/ID45");
	brain.addClass("A");
	brain.prefix("http://www.chebi.org/", "chebi");
	brain.addExternalClass("B");
	brain.getOWLClass("B");
	brain.save("src/test/resources/prefix.owl");
    }

    @Test(expected = BadNameException.class)
    public void wrongNamePrefixTest() throws BrainException {
	Brain brain = new Brain();
	brain.addClass("Blood Coagulation");
    }

    @Test(expected = BadNameException.class)
    public void wrongNameCustomPrefixTest() throws BrainException {
	Brain brain = new Brain("http://www.example.org/", "example.owl");
	brain.addClass("Blood Coagulation");
    }

    @Test(expected = BadPrefixException.class)
    public void wrongCustomPrefixTest() throws BrainException {
	@SuppressWarnings("unused")
	Brain brain = new Brain("htp://www.example.org/", "example.owl");
    }

}
