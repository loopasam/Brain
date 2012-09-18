/**
 * 
 */
package uk.ac.ebi;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BadPrefixException;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.ClassExpressionException;
import uk.ac.ebi.brain.error.DataPropertyExpressionException;
import uk.ac.ebi.brain.error.ExistingClassException;
import uk.ac.ebi.brain.error.ExistingDataProperty;
import uk.ac.ebi.brain.error.ExistingObjectProperty;
import uk.ac.ebi.brain.error.NonExistingEntityException;
import uk.ac.ebi.brain.error.ObjectPropertyExpressionException;

/**
 * @author Samuel Croset
 *
 */
public class BrainTest {

    Brain brain;

    @Before
    public void bootstrap() throws BrainException {
	brain = new Brain("http://www.example.org#");
    }

    @Test(expected = BadPrefixException.class)
    public void wrongPrefixTest() throws BrainException {
	@SuppressWarnings("unused")
	Brain brainTest = new Brain("http://www.example.org");
    }

    @Test
    public void addClassTest() throws ExistingClassException{
	brain.addClass("A");
	assertEquals(true, brain.getOntology().containsClassInSignature(IRI.create(brain.getPrefixManager().getDefaultPrefix() + "A")));
    }

    @Test(expected = ExistingClassException.class)
    public void addExistingClassTest() throws ExistingClassException{
	brain.addClass("A");
	assertEquals(true, brain.getOntology().containsClassInSignature(IRI.create(brain.getPrefixManager().getDefaultPrefix() + "A")));
	brain.addClass("A");
    }

    @Test
    public void getOwlClassTest() throws BrainException {
	OWLClass owlClassA = brain.addClass("A");
	OWLClass owlClassAprime = brain.getOWLClass("A");
	assertEquals(owlClassA, owlClassAprime);
    }

    @Test(expected = NonExistingEntityException.class)
    public void getNonExsistingOwlClassTest() throws NonExistingEntityException {
	brain.getOWLClass("A");
    }

    @Test
    public void subClassOfTest() throws BrainException {
	brain.addClass("A");
	brain.addClass("B");
	brain.subClassOf("A", "B");
    }

    @Test
    public void equivalentClassesTest() throws ClassExpressionException, ExistingClassException{
	brain.addClass("A");
	brain.addClass("B");
	brain.equivalentClasses("A", "B");
    }

    @Test
    public void disjointClassesTest() throws BrainException{
	brain.addClass("A");
	brain.addClass("B");
	brain.disjointClasses("A", "B");
    }

    @Test(expected = ClassExpressionException.class)
    public void subClassOfErrorTest() throws ClassExpressionException{
	brain.subClassOf("A", "C");
    }

    @Test
    public void saveOntology() throws BrainException {
	brain.addClass("A");
	brain.addClass("B");
	brain.subClassOf("A", "B");
	brain.save("src/test/resources/output.owl");
    }

    @Test
    public void objectPropertyTest() throws ExistingObjectProperty {
	brain.addObjectProperty("part-of");
	assertEquals(true, brain.getOntology().containsObjectPropertyInSignature(IRI.create(brain.getPrefixManager().getDefaultPrefix() + "part-of")));
    }

    @Test(expected = ExistingObjectProperty.class)
    public void redundantObjectPropertyTest() throws ExistingObjectProperty {
	brain.addObjectProperty("part-of");
	assertEquals(true, brain.getOntology().containsObjectPropertyInSignature(IRI.create(brain.getPrefixManager().getDefaultPrefix() + "part-of")));
	brain.addObjectProperty("part-of");
    }

    @Test
    public void transitivePropertyTest() throws BrainException {
	brain.addObjectProperty("part-of");
	brain.transitive("part-of");
	brain.save("src/test/resources/output.owl");
    }

    @Test(expected = ObjectPropertyExpressionException.class)
    public void unknownTransitivePropertyTest() throws BrainException {
	brain.addObjectProperty("part-of");
	brain.transitive("regulates");
    }

    @Test
    public void reflexivePropertyTest() throws BrainException {
	brain.addObjectProperty("part-of");
	brain.reflexive("part-of");
	brain.save("src/test/resources/output.owl");
    }

    @Test
    public void dataPropertyTest() throws BrainException {
	brain.addDataProperty("has-age");
	assertEquals(true, brain.getOntology().containsDataPropertyInSignature(IRI.create(brain.getPrefixManager().getDefaultPrefix() + "has-age")));
	brain.save("src/test/resources/output.owl");
    }

    @Test(expected = ExistingDataProperty.class)
    public void existingDataPropertyTest() throws BrainException {
	brain.addDataProperty("has-age");
	brain.addDataProperty("has-age");
    }

    @Test
    public void functionalDataPropertyTest() throws ExistingDataProperty, DataPropertyExpressionException {
	brain.addDataProperty("has-age");
	brain.functional("has-age");
    }

    @Test(expected = DataPropertyExpressionException.class)
    public void functionalDataPropertyAssertionTest() throws ExistingDataProperty, ExistingObjectProperty, DataPropertyExpressionException{
	brain.addObjectProperty("part-of");
	brain.addDataProperty("has-age");
	brain.functional("part-of");
    }

    @Test
    public void domainTest() throws BrainException{
	brain.addClass("A");
	brain.addObjectProperty("part-of");
	brain.domain("part-of", "A");
    }

    @Test
    public void complexDomainTest() throws BrainException{
	brain.addClass("A");
	brain.addClass("B");
	brain.addDataProperty("has-age");
	brain.addObjectProperty("part-of");
	brain.domain("has-age", "A and B");
	brain.domain("part-of", "A and B");
	brain.save("src/test/resources/output.owl");
    }

    @Test
    public void rangeObjectPropertyTest() throws BrainException{
	brain.addClass("A");
	brain.addObjectProperty("part-of");
	brain.range("part-of", "A");
    }

    @Test
    public void rangeDataPropertyTest() throws BrainException {
	brain.addClass("A");
	brain.addDataProperty("has-age");
	brain.range("has-age", brain.INTEGER);
	brain.save("src/test/resources/output.owl");
    }

    @Test
    public void equivalentObjectPropertiesTest() throws BrainException {
	brain.addObjectProperty("part-of");
	brain.addObjectProperty("is-part-of");
	brain.equivalentProperties("part-of", "is-part-of");
    }

    @Test(expected = BrainException.class)
    public void wrongEquivalentObjectPropertiesTest() throws BrainException {
	brain.addObjectProperty("part-of");
	brain.addObjectProperty("is-part-of");
	brain.equivalentProperties("part-of", "isEEE-part-of");
    }

    @Test
    public void equivalentDataPropertiesTest() throws BrainException {
	brain.addDataProperty("has-age");
	brain.addDataProperty("number-of-years");
	brain.equivalentProperties("has-age", "number-of-years");
    }

    @Test(expected = BrainException.class)
    public void mixedEquivalentPropertiesTest() throws BrainException {
	brain.addDataProperty("has-age");
	brain.addObjectProperty("part-of");
	brain.equivalentProperties("has-age", "part-of");
    }

    @Test
    public void subObjectPropertyTest() throws BrainException{
	brain.addObjectProperty("positively-regulates");
	brain.addObjectProperty("regulates");
	brain.subPropertyOf("positively-regulates", "regulates");
	brain.save("src/test/resources/output.owl");
    }

    @Test
    public void subDataPropertyTest() throws BrainException{
	brain.addDataProperty("positively-regulates");
	brain.addDataProperty("regulates");
	brain.subPropertyOf("positively-regulates", "regulates");
	brain.save("src/test/resources/output.owl");
    }

    @Test(expected = BrainException.class)
    public void corruptedSubPropertyAseertionTest() throws BrainException{
	brain.addDataProperty("positively-regulates");
	brain.addObjectProperty("regulates");
	brain.subPropertyOf("positively-regulates", "regulates");
	brain.save("src/test/resources/output.owl");
    }

    @Test
    public void ChainedPropertiesTest() throws BrainException {
	brain.addObjectProperty("part-of");
	brain.addObjectProperty("regulates");
	brain.chain("regulates o part-of", "regulates");
	brain.save("src/test/resources/output.owl");
    }

    @Test(expected = ObjectPropertyExpressionException.class)
    public void wrongChainedPropertiesTest() throws BrainException {
	brain.addObjectProperty("part-of");
	brain.addObjectProperty("regulates");
	brain.chain("regulated o part-of", "regulates");
	brain.save("src/test/resources/output.owl");
    }

    @Test
    public void labelTest() throws BrainException {
	brain.addClass("A");
	brain.label("A", "this is the content of the label");
	brain.save("src/test/resources/output.owl");
    }

    @Test
    public void annotationTest() throws BrainException {
	brain.addAnnotationProperty("definition");
	brain.addClass("A");
	brain.annotation("A", "definition", "this is the definition of the entity");
	brain.save("src/test/resources/output.owl");
    }

    @Test(expected = NonExistingEntityException.class)
    public void wrongAnnotationTest() throws BrainException {
	brain.addAnnotationProperty("definition");
	brain.annotation("A", "definitionCorrupted", "this is the definition of the entity");
    }

    @Test
    public void builtInAnnotationTest() throws BrainException {
	brain.addClass("A");
	brain.comment("A", "his is a comment");
	brain.seeAlso("A", "see also there");
	brain.isDefinedBy("A", "is defined by that");
	brain.save("src/test/resources/output.owl");
    }



}
