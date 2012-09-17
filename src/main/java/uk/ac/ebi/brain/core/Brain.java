/**
 * 
 */
package uk.ac.ebi.brain.core;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
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

import uk.ac.ebi.brain.error.BadPrefixException;
import uk.ac.ebi.brain.error.BrainException;
import uk.ac.ebi.brain.error.ClassExpressionException;
import uk.ac.ebi.brain.error.DataPropertyExpressionException;
import uk.ac.ebi.brain.error.ExistingClassException;
import uk.ac.ebi.brain.error.ExistingDataProperty;
import uk.ac.ebi.brain.error.ExistingObjectProperty;
import uk.ac.ebi.brain.error.NewOntologyException;
import uk.ac.ebi.brain.error.NonExistingDataPropertyException;
import uk.ac.ebi.brain.error.NonExistingEntityException;
import uk.ac.ebi.brain.error.NonExistingClassException;
import uk.ac.ebi.brain.error.NonExistingObjectPropertyException;
import uk.ac.ebi.brain.error.ObjectPropertyExpressionException;
import uk.ac.ebi.brain.error.StorageException;

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
     * @throws BadPrefixException 
     * @throws NewOntologyException 
     * 
     */
    public Brain(String prefix) throws BadPrefixException, NewOntologyException {
	if(!(prefix.endsWith("#") || prefix.endsWith("/"))){
	    throw new BadPrefixException("The prefix must finish either with the symbol '/' or '#'");
	}
	this.manager = OWLManager.createOWLOntologyManager();
	this.factory = manager.getOWLDataFactory();
	try {
	    this.ontology = manager.createOntology(IRI.create(prefix));
	} catch (OWLOntologyCreationException e) {
	    throw new NewOntologyException(e);
	}
	this.prefixManager = new DefaultPrefixManager(prefix);
    }

    /**
     * @param className
     * @return 
     * @throws ExistingClassException 
     */
    public OWLClass addClass(String className) throws ExistingClassException {
	try {
	    this.getOWLClass(className);
	    throw new ExistingClassException("The class '"+ className +"' already exists.");
	} catch (NonExistingClassException e) {
	    OWLClass owlClass = this.factory.getOWLClass(className, this.prefixManager);
	    OWLDeclarationAxiom declarationAxiom = this.factory.getOWLDeclarationAxiom(owlClass);
	    manager.addAxiom(this.ontology, declarationAxiom);
	    updateShorForms();
	    return owlClass;
	}
    }

    public OWLObjectProperty addObjectProperty(String objectPropertyName) throws ExistingObjectProperty {
	try {
	    this.getOWLObjectProperty(objectPropertyName);
	    throw new ExistingObjectProperty("The object property '"+ objectPropertyName +"' already exists.");
	} catch (NonExistingObjectPropertyException e) {
	    OWLObjectProperty owlObjectProperty = this.factory.getOWLObjectProperty(objectPropertyName, this.prefixManager);
	    OWLDeclarationAxiom declarationAxiom = this.factory.getOWLDeclarationAxiom(owlObjectProperty);
	    manager.addAxiom(this.ontology, declarationAxiom);
	    updateShorForms();
	    return owlObjectProperty;
	}
    }

    public OWLDataProperty addDataProperty(String dataPropertyName) throws ExistingDataProperty {
	try {
	    this.getOWLDataProperty(dataPropertyName);
	    throw new ExistingDataProperty("The data property '"+ dataPropertyName +"' already exists.");
	} catch (NonExistingDataPropertyException e) {
	    OWLDataProperty owlDataProperty = this.factory.getOWLDataProperty(dataPropertyName, this.prefixManager);
	    OWLDeclarationAxiom declarationAxiom = this.factory.getOWLDeclarationAxiom(owlDataProperty);
	    manager.addAxiom(this.ontology, declarationAxiom);
	    updateShorForms();
	    return owlDataProperty;
	}
    }

    //TODO annotation
//    public OWLDataProperty addAnnotation(String annotation) {
//	try {
//	    this.getOWLDataProperty(dataPropertyName);
//	    throw new ExistingDataProperty("The data property '"+ dataPropertyName +"' already exists.");
//	} catch (NonExistingDataPropertyException e) {
//
//	    this.factory.getOWLAnnotationProperty(annotation, this.prefixManager);
//
//	    OWLAnnotation owlAnnotation = this.factory.getOWLAnnotation(property, value)(dataPropertyName, this.prefixManager);
//	    OWLDeclarationAxiom declarationAxiom = this.factory.getOWLDeclarationAxiom(owlDataProperty);
//	    manager.addAxiom(this.ontology, declarationAxiom);
//	    updateShorForms();
//	    return owlDataProperty;
//	}
//    }


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
    public OWLClass getOWLClass(String className) throws NonExistingClassException {
	if(this.ontology.containsClassInSignature(IRI.create(this.prefixManager.getDefaultPrefix() + className))){
	    return this.factory.getOWLClass(className, this.prefixManager);
	}else{
	    throw new NonExistingClassException("The entity '"+ className +"' does not exist or is not an OWL class.");
	}
    }

    /**
     * @param className
     * @return
     * @throws NonExistingObjectPropertyException 
     * @throws NonExistingEntityException 
     */
    public OWLObjectProperty getOWLObjectProperty(String objectPropertyName) throws NonExistingObjectPropertyException {
	if(this.ontology.containsObjectPropertyInSignature(IRI.create(this.prefixManager.getDefaultPrefix() + objectPropertyName))){
	    return this.factory.getOWLObjectProperty(objectPropertyName, this.prefixManager);
	}else{
	    throw new NonExistingObjectPropertyException("The entity '"+ objectPropertyName +"' does not exist or is not an object property.");
	}
    }

    public OWLDataProperty getOWLDataProperty(String dataPropertyName) throws NonExistingDataPropertyException {
	if(this.ontology.containsDataPropertyInSignature(IRI.create(this.prefixManager.getDefaultPrefix() + dataPropertyName))){
	    return this.factory.getOWLDataProperty(dataPropertyName, this.prefixManager);
	}else{
	    throw new NonExistingDataPropertyException("The entity '"+ dataPropertyName +"' does not exist or is not a data property.");
	}
    }


    /**
     * @param string
     * @param string2
     * @throws ClassExpressionException 
     * @throws ParserException 
     * @throws NonExistingEntityException 
     */
    public void subClassOf(String subClass, String superClass) throws ClassExpressionException {
	OWLClassExpression subClassExpression = parseClassExpression(subClass);
	OWLClassExpression superClassExpression = parseClassExpression(superClass);
	OWLSubClassOfAxiom subClassAxiom = factory.getOWLSubClassOfAxiom(subClassExpression, superClassExpression);
	AddAxiom addAx = new AddAxiom(ontology, subClassAxiom);
	manager.applyChange(addAx);
    }

    public void equivalentClasses(String class1, String class2) throws ClassExpressionException {
	OWLClassExpression classExpression1 = parseClassExpression(class1);
	OWLClassExpression classExpression2 = parseClassExpression(class2);
	OWLEquivalentClassesAxiom equivalentClassAxiom = factory.getOWLEquivalentClassesAxiom(classExpression1, classExpression2);
	AddAxiom addAx = new AddAxiom(ontology, equivalentClassAxiom);
	manager.applyChange(addAx);
    }

    public void disjointClasses(String class1, String class2) throws ClassExpressionException {
	OWLClassExpression classExpression1 = parseClassExpression(class1);
	OWLClassExpression classExpression2 = parseClassExpression(class2);
	OWLDisjointClassesAxiom equivalentClassAxiom = factory.getOWLDisjointClassesAxiom(classExpression1, classExpression2);
	AddAxiom addAx = new AddAxiom(ontology, equivalentClassAxiom);
	manager.applyChange(addAx);
    }

    public void transitive(String propertyExpression) throws ObjectPropertyExpressionException {
	OWLObjectPropertyExpression expression = parseObjectPropertyExpression(propertyExpression);
	OWLTransitiveObjectPropertyAxiom axiom = factory.getOWLTransitiveObjectPropertyAxiom(expression);
	AddAxiom addAx = new AddAxiom(ontology, axiom);
	manager.applyChange(addAx);
    }

    public void reflexive(String propertyExpression) throws ObjectPropertyExpressionException {
	OWLObjectPropertyExpression expression = parseObjectPropertyExpression(propertyExpression);
	OWLReflexiveObjectPropertyAxiom axiom = factory.getOWLReflexiveObjectPropertyAxiom(expression);
	AddAxiom addAx = new AddAxiom(ontology, axiom);
	manager.applyChange(addAx);
    }

    public void functional(String propertyExpression) throws DataPropertyExpressionException {
	OWLDataPropertyExpression expression = parseDataPropertyExpression(propertyExpression);
	OWLFunctionalDataPropertyAxiom axiom = factory.getOWLFunctionalDataPropertyAxiom(expression);
	AddAxiom addAx = new AddAxiom(ontology, axiom);
	manager.applyChange(addAx);
    }

    public void domain(String propertyExpression, String classExpression) throws ClassExpressionException, NonExistingEntityException{
	OWLClassExpression domainExpression = parseClassExpression(classExpression);
	try {
	    OWLObjectPropertyExpression owlPropertyExpression = parseObjectPropertyExpression(propertyExpression);
	    OWLObjectPropertyDomainAxiom axiom = factory.getOWLObjectPropertyDomainAxiom(owlPropertyExpression, domainExpression);
	    AddAxiom addAx = new AddAxiom(ontology, axiom);
	    manager.applyChange(addAx);
	} catch (ObjectPropertyExpressionException e) {
	    OWLDataPropertyExpression owlPropertyExpression;
	    try {
		owlPropertyExpression = parseDataPropertyExpression(propertyExpression);
		OWLDataPropertyDomainAxiom axiom = factory.getOWLDataPropertyDomainAxiom(owlPropertyExpression, domainExpression);
		AddAxiom addAx = new AddAxiom(ontology, axiom);
		manager.applyChange(addAx);
	    } catch (DataPropertyExpressionException e1) {
		throw new NonExistingEntityException("The property '"+propertyExpression+"' does not exist.");
	    }
	}
    }

    //TODO does not deal with dataProperties ranges at the moment - an error is thrown
    public void range(String propertyExpression, String classExpression) throws ClassExpressionException, ObjectPropertyExpressionException {
	OWLObjectPropertyExpression owlPropertyExpression = parseObjectPropertyExpression(propertyExpression);
	OWLClassExpression rangeExpression = parseClassExpression(classExpression);
	OWLObjectPropertyRangeAxiom axiom = factory.getOWLObjectPropertyRangeAxiom(owlPropertyExpression, rangeExpression);
	AddAxiom addAx = new AddAxiom(ontology, axiom);
	manager.applyChange(addAx);
    }

    public void equivalentProperties(String property1, String property2) throws BrainException {
	try {
	    OWLObjectPropertyExpression owlProperty1 = parseObjectPropertyExpression(property1);
	    OWLObjectPropertyExpression owlProperty2 = parseObjectPropertyExpression(property2);
	    OWLEquivalentObjectPropertiesAxiom axiom = factory.getOWLEquivalentObjectPropertiesAxiom(owlProperty1, owlProperty2);
	    AddAxiom addAx = new AddAxiom(ontology, axiom);
	    manager.applyChange(addAx);
	} catch (ObjectPropertyExpressionException e) {
	    try{
		OWLDataPropertyExpression owlProperty1 = parseDataPropertyExpression(property1);
		OWLDataPropertyExpression owlProperty2 = parseDataPropertyExpression(property2);
		OWLEquivalentDataPropertiesAxiom axiom = factory.getOWLEquivalentDataPropertiesAxiom(owlProperty1, owlProperty2);
		AddAxiom addAx = new AddAxiom(ontology, axiom);
		manager.applyChange(addAx);
	    } catch (DataPropertyExpressionException e1) {
		throw new BrainException("One of the properties ('"+property1+"' or '"+property2+"') does not exist or the properties have different types.");
	    }
	}
    }

    public void subPropertyOf(String subProperty, String superProperty) throws BrainException {
	try{
	    OWLObjectPropertyExpression subPropertyExpression = parseObjectPropertyExpression(subProperty);
	    OWLObjectPropertyExpression superPropertyExpression = parseObjectPropertyExpression(superProperty);
	    OWLSubObjectPropertyOfAxiom subClassAxiom = factory.getOWLSubObjectPropertyOfAxiom(subPropertyExpression, superPropertyExpression);
	    AddAxiom addAx = new AddAxiom(ontology, subClassAxiom);
	    manager.applyChange(addAx);
	} catch(ObjectPropertyExpressionException e){
	    try{
		OWLDataPropertyExpression subPropertyExpression = parseDataPropertyExpression(subProperty);
		OWLDataPropertyExpression superPropertyExpression = parseDataPropertyExpression(superProperty);
		OWLSubDataPropertyOfAxiom subClassAxiom = factory.getOWLSubDataPropertyOfAxiom(subPropertyExpression, superPropertyExpression);
		AddAxiom addAx = new AddAxiom(ontology, subClassAxiom);
		manager.applyChange(addAx);
	    }catch (DataPropertyExpressionException e1) {
		throw new BrainException("One of the properties ('"+subProperty+"' or '"+superProperty+"') does not exist or the properties have different types.");
	    }
	}
    }

    public void chain(String chain, String superProperty) throws ObjectPropertyExpressionException {
	List<OWLObjectPropertyExpression> chainExpression = parseObjectPropertyChain(chain);
	OWLObjectPropertyExpression superPropertyExpression = parseObjectPropertyExpression(superProperty);
	OWLSubPropertyChainOfAxiom axiom = this.factory.getOWLSubPropertyChainOfAxiom(chainExpression, superPropertyExpression);
	AddAxiom addAx = new AddAxiom(ontology, axiom);
	manager.applyChange(addAx);
    }

    public void label(String entity, String label) throws NonExistingEntityException{
	if(this.bidiShortFormProvider.getEntity(entity) !=  null){
	    OWLEntity owlEntity = this.bidiShortFormProvider.getEntity(entity);
	    OWLAnnotation labelAnnotation = this.factory.getOWLAnnotation(this.factory.getRDFSLabel(), this.factory.getOWLLiteral(label));
	    OWLAxiom axiom = this.factory.getOWLAnnotationAssertionAxiom(owlEntity.getIRI(), labelAnnotation);
	    this.manager.applyChange(new AddAxiom(this.ontology, axiom));	
	}else{
	    throw new NonExistingEntityException("The entity '"+entity+"' does not exist.");
	}
    }


    /**
     * Converts a string into an OWLExpression. If a problem is encountered, an error is thrown which can be catched-up
     * in order to know more about the error.
     * @param expression
     * @return owlExpression
     * @throws ClassExpressionException 
     * @throws ParserException 
     */
    private OWLClassExpression parseClassExpression(String expression) throws ClassExpressionException {
	OWLClassExpression owlClassExpression = null;
	ManchesterOWLSyntaxEditorParser parser = getParser(expression);
	try {
	    owlClassExpression = parser.parseClassExpression();
	} catch (ParserException e) {
	    throw new ClassExpressionException(e);
	}
	return owlClassExpression;
    }


    private OWLObjectPropertyExpression parseObjectPropertyExpression(String objectPropertyExpression) throws ObjectPropertyExpressionException {
	OWLObjectPropertyExpression owlObjectPropertyExpression = null;
	ManchesterOWLSyntaxEditorParser parser = getParser(objectPropertyExpression);
	try {
	    owlObjectPropertyExpression = parser.parseObjectPropertyExpression();
	} catch (ParserException e) {
	    throw new ObjectPropertyExpressionException(e);
	}
	return owlObjectPropertyExpression;
    }


    private OWLDataPropertyExpression parseDataPropertyExpression(String dataPropertyExpression) throws DataPropertyExpressionException {
	OWLDataPropertyExpression owlDataPropertyExpression = null;
	ManchesterOWLSyntaxEditorParser parser = getParser(dataPropertyExpression);
	try {
	    owlDataPropertyExpression = parser.parseDataPropertyExpression();
	} catch (ParserException e) {
	    throw new DataPropertyExpressionException(e);
	}
	return owlDataPropertyExpression;
    }

    private List<OWLObjectPropertyExpression> parseObjectPropertyChain(String chainExpression) throws ObjectPropertyExpressionException {
	List<OWLObjectPropertyExpression> owlObjectPropertyExpressions = null;
	ManchesterOWLSyntaxEditorParser parser = getParser(chainExpression);
	try {
	    owlObjectPropertyExpressions = parser.parseObjectPropertyChain();
	} catch (ParserException e) {
	    throw new ObjectPropertyExpressionException(e);
	}
	return owlObjectPropertyExpressions;
    }


    /**
     * @param expression 
     * @return
     */
    private ManchesterOWLSyntaxEditorParser getParser(String expression) {	
	ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(factory, expression);
	parser.setDefaultOntology(ontology);
	parser.setOWLEntityChecker(entityChecker);
	return parser;
    }

    /**
     * @param string
     * @throws StorageException 
     * @throws OWLOntologyStorageException 
     */
    public void save(String path) throws StorageException {
	File file = new File(path);
	OWLOntologyFormat format = manager.getOntologyFormat(this.ontology);
	ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
	if (format.isPrefixOWLOntologyFormat()) {
	    manSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
	}
	try {
	    manager.saveOntology(this.ontology, manSyntaxFormat, IRI.create(file));
	} catch (OWLOntologyStorageException e) {
	    throw new StorageException(e);
	}
    }

}
