/**
 * 
 */
package uk.ac.ebi.brain.core;

import java.io.File;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.brain.error.ExistingClassException;
import uk.ac.ebi.brain.error.NonExistingEntityException;
import uk.ac.ebi.brain.error.NonExistingClassException;

/**
 * @author Samuel Croset
 *
 */
public class Brain {

    private OWLOntology ontology;
    private OWLReasoner reasoner;
    private OWLOntologyManager manager;
    private OWLDataFactory factory;
    private OWLReasonerFactory reasonerFactory;
    private ShortFormProvider shortFormProvider;
    private BidirectionalShortFormProvider bidiShortFormProvider;
    private OWLEntityChecker entityChecker;
    final Logger logger = LoggerFactory.getLogger(Brain.class);
    private PrefixManager prefixManager;

    public OWLOntology getOntology() {
	return ontology;
    }
    public void setOntology(OWLOntology ontology) {
	this.ontology = ontology;
    }
    public OWLReasoner getReasoner() {
	return reasoner;
    }
    public void setReasoner(OWLReasoner reasoner) {
	this.reasoner = reasoner;
    }
    public OWLOntologyManager getManager() {
	return manager;
    }
    public void setManager(OWLOntologyManager manager) {
	this.manager = manager;
    }
    public OWLDataFactory getFactory() {
	return factory;
    }
    public void setFactory(OWLDataFactory factory) {
	this.factory = factory;
    }
    public OWLReasonerFactory getReasonerFactory() {
	return reasonerFactory;
    }
    public void setReasonerFactory(OWLReasonerFactory reasonerFactory) {
	this.reasonerFactory = reasonerFactory;
    }
    public ShortFormProvider getShortFormProvider() {
	return shortFormProvider;
    }
    public void setShortFormProvider(ShortFormProvider shortFormProvider) {
	this.shortFormProvider = shortFormProvider;
    }
    public BidirectionalShortFormProvider getBidiShortFormProvider() {
	return bidiShortFormProvider;
    }
    public void setBidiShortFormProvider(
	    BidirectionalShortFormProvider bidiShortFormProvider) {
	this.bidiShortFormProvider = bidiShortFormProvider;
    }
    public OWLEntityChecker getEntityChecker() {
	return entityChecker;
    }
    public void setEntityChecker(OWLEntityChecker entityChecker) {
	this.entityChecker = entityChecker;
    }
    public void setPrefixManager(PrefixManager prefixManager) {
	this.prefixManager = prefixManager;
    }
    public PrefixManager getPrefixManager() {
	return prefixManager;
    }

    /**
     * @throws OWLOntologyCreationException 
     * 
     */
    public Brain(String prefix) throws OWLOntologyCreationException{
	//TODO check that the string finishes wiith / or #
	this.manager = OWLManager.createOWLOntologyManager();
	this.factory = manager.getOWLDataFactory();
	this.ontology = manager.createOntology(IRI.create(prefix));
	this.prefixManager = new DefaultPrefixManager(prefix);
    }

    /**
     * @param className
     * @return 
     * @throws ExistingClassException 
     */
    public OWLClass addOWLClass(String className) {
	//TODO throw an error if classes exists already
	OWLClass owlClass = this.factory.getOWLClass(className, this.prefixManager);
	OWLDeclarationAxiom declarationAxiom = this.factory.getOWLDeclarationAxiom(owlClass);
	manager.addAxiom(this.ontology, declarationAxiom);
	updateShorForms();
	return owlClass;
    }

    public OWLObjectProperty addOWLObjectProperty(String propName) {
	//TODO throw an error if classes exists already
	OWLObjectProperty owlprop = this.factory.getOWLObjectProperty(propName, this.prefixManager);
	OWLDeclarationAxiom declarationAxiom = this.factory.getOWLDeclarationAxiom(owlprop);
	manager.addAxiom(this.ontology, declarationAxiom);
	updateShorForms();
	return owlprop;
    }


    /**
     * 
     */
    private void updateShorForms() {
	this.shortFormProvider = new SimpleShortFormProvider();
	Set<OWLOntology> importsClosure = this.ontology.getImportsClosure();
	this.bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(this.manager, importsClosure, this.shortFormProvider);
	this.entityChecker = new ShortFormEntityChecker(this.bidiShortFormProvider);
    }

    /**
     * @param className
     * @return
     * @throws NonExistingEntityException 
     */
    public OWLClass getOWLClass(String className) throws NonExistingEntityException {
	if(this.ontology.containsClassInSignature(IRI.create(this.prefixManager.getDefaultPrefix() + className))){
	    return this.factory.getOWLClass(className, this.prefixManager);
	}else{
	    throw new NonExistingClassException("The entity '"+ className +"' is not an OWL class");
	}
    }


    /**
     * @param string
     * @param string2
     * @throws ParserException 
     * @throws NonExistingEntityException 
     * @throws ExistingClassException 
     */
    public void subClassOf(String subClassName, String superClassName) throws ParserException {
	OWLClassExpression subClassExpression = parseClassExpression(subClassName);
	OWLClassExpression superClassExpression = parseClassExpression(superClassName);
	OWLSubClassOfAxiom subClassAxiom = factory.getOWLSubClassOfAxiom(subClassExpression, superClassExpression);
	AddAxiom addAx = new AddAxiom(ontology, subClassAxiom);
	manager.applyChange(addAx);
    }

    public void makeTransitive(String propertyExpression) {
	// TODO Auto-generated method stub
	OWLObjectPropertyExpression expression = parseObjectPropertyExpression(propertyExpression);
	OWLTransitiveObjectPropertyAxiom axiom = factory.getOWLTransitiveObjectPropertyAxiom(expression);
	AddAxiom addAx = new AddAxiom(ontology, axiom);
	manager.applyChange(addAx);
    }

    private OWLObjectPropertyExpression parseObjectPropertyExpression(String expression) {
	ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(factory, expression);
	parser.setDefaultOntology(ontology);
	parser.setOWLEntityChecker(entityChecker);
	OWLObjectPropertyExpression owlExpression = null;
	try {
	    owlExpression = parser.parseObjectPropertyExpression();
	} catch (ParserException e) {
	    // TODO Auto-generated catch block
	    //TODO printing error perso
	    e.printStackTrace();
	}

	return owlExpression;
    }


    /**
     * Converts a string into an OWLExpression. If a problem is encountered, an error is thrown which can be catched-up
     * in order to know more about the error.
     * @param expression
     * @return owlExpression
     * @throws ParserException 
     */
    private OWLClassExpression parseClassExpression(String expression) {
	ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(factory, expression);
	parser.setDefaultOntology(ontology);
	parser.setOWLEntityChecker(entityChecker);
	OWLClassExpression owlExpression = null;
	try {
	    owlExpression = parser.parseClassExpression();
	} catch (ParserException e) {
	    // TODO Auto-generated catch block
	    //TODO printing error perso
	    e.printStackTrace();
	}

	return owlExpression;
    }

    /**
     * @param string
     * @throws OWLOntologyStorageException 
     */
    public void save(String path) throws OWLOntologyStorageException {
	// TODO Auto-generated method stub
	File file = new File(path);
	OWLOntologyFormat format = manager.getOntologyFormat(this.ontology);
	ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
	if (format.isPrefixOWLOntologyFormat()) {
	    manSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
	}
	manager.saveOntology(this.ontology, manSyntaxFormat, IRI.create(file));
    }


}
