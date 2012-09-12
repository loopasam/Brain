/**
 * 
 */
package uk.ac.ebi.brain.core;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.ShortFormProvider;
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
    public OWLClass addOWLClass(String className) throws ExistingClassException {
	
	//TODO check if expression if yes --> error
	
	if(this.ontology.containsClassInSignature(IRI.create(this.prefixManager.getDefaultPrefix() + className))){
	    throw new ExistingClassException("The class '"+ className +"' already exists in the ontology.");
	}
	OWLClass owlClass = this.factory.getOWLClass(className, this.prefixManager);
	OWLDeclarationAxiom declarationAxiom = this.factory.getOWLDeclarationAxiom(owlClass);
	manager.addAxiom(this.ontology, declarationAxiom);
	return owlClass;
    }

    /**
     * @param string
     * @param string2
     * @throws ExistingClassException 
     */
    public void subClassOf(String subClassName, String superClassName) {

	OWLClass subClass = null;
	try {
	    subClass = this.getOWLClass(subClassName);
	} catch (NonExistingEntityException e) {
	    try {
		//TODO NAN !
		subClass = this.addOWLClass(subClassName);
	    } catch (ExistingClassException e1) {
		e1.printStackTrace();
	    }
	}

	OWLClass superClass = null;

	try {
	    superClass = this.getOWLClass(superClassName);
	} catch (NonExistingEntityException e) {
	    try {
		superClass = this.addOWLClass(superClassName);
	    } catch (ExistingClassException e1) {
		e1.printStackTrace();
	    }
	}

	OWLAxiom axiom = factory.getOWLSubClassOfAxiom(subClass, superClass);
	AddAxiom addAxiom = new AddAxiom(ontology, axiom);
	manager.applyChange(addAxiom);
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

}
