/**
 * Copyright [2013] EMBL - European Bioinformatics Institute
 * Licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of 
 * the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on 
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
 * KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations 
 * under the License.
 */
package uk.ac.ebi.brain.core;

import java.io.File;
import java.net.*;
import java.util.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.coode.owlapi.manchesterowlsyntax.*;
import org.semanticweb.elk.owlapi.*;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.profiles.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import uk.ac.ebi.brain.error.*;

/**
 * Facade class to manipulate the OWL-API.
 * The documentation is available at https://github.com/loopasam/Brain/wiki
 * @author Samuel Croset
 */
public class Brain {

	private OWLOntology ontology;
	private OWLReasoner reasoner;
	private OWLOntologyManager manager;
	private OWLDataFactory factory;
	private OWLReasonerFactory reasonerFactory;
	private BidirectionalShortFormProvider bidiShortFormProvider;
	private OWLEntityChecker entityChecker;
	private DefaultPrefixManager prefixManager;
	public OWLDatatype INTEGER;
	public OWLDatatype FLOAT;
	public OWLDatatype BOOLEAN;
	public static final String DEFAULT_PREFIX = "brain#";
	private boolean isClassified;
	private ElkReasonerConfiguration configuration;

	public OWLOntology getOntology() {
		return ontology;
	}

	@Deprecated
	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}
	public OWLReasoner getReasoner() {
		return reasoner;
	}

	@Deprecated
	public void setReasoner(OWLReasoner reasoner) {
		this.reasoner = reasoner;
	}
	public OWLOntologyManager getManager() {
		return manager;
	}

	@Deprecated
	public void setManager(OWLOntologyManager manager) {
		this.manager = manager;
	}
	public OWLDataFactory getFactory() {
		return factory;
	}

	@Deprecated
	public void setFactory(OWLDataFactory factory) {
		this.factory = factory;
	}
	public OWLReasonerFactory getReasonerFactory() {
		return reasonerFactory;
	}

	@Deprecated
	public void setReasonerFactory(OWLReasonerFactory reasonerFactory) {
		this.reasonerFactory = reasonerFactory;
	}
	public BidirectionalShortFormProvider getBidiShortFormProvider() {
		return bidiShortFormProvider;
	}

	@Deprecated
	public void setBidiShortFormProvider(BidirectionalShortFormProvider bidiShortFormProvider) {
		this.bidiShortFormProvider = bidiShortFormProvider;
	}
	public OWLEntityChecker getEntityChecker() {
		return entityChecker;
	}

	@Deprecated
	public void setEntityChecker(OWLEntityChecker entityChecker) {
		this.entityChecker = entityChecker;
	}

	@Deprecated
	public void setPrefixManager(DefaultPrefixManager prefixManager) {
		this.prefixManager = prefixManager;
	}
	public PrefixManager getPrefixManager() {
		return prefixManager;
	}

	@Deprecated
	public void setClassified(boolean isClassified) {
		this.isClassified = isClassified;
	}
	public boolean isClassified() {
		return isClassified;
	}

	/**
	 * Creates a Brain instance with the specified prefix and ontology IRI.
	 * The OWL entities created will inherit from this prefix.
	 * The number of working threads for reasoning is set to the maximum (default).
	 * @param prefix
	 * @param ontologyIri
	 * @throws BadPrefixException 
	 * @throws NewOntologyException 
	 */
	public Brain(String prefix, String ontologyIri) throws NewOntologyException, BadPrefixException {
		this(prefix, ontologyIri, -1);
	}

	/**
	 * Creates a Brain instance with the a default prefix (<brain#>) and ontology IRI (<brain.owl>).
	 * The number of working threads for reasoning is set to the maximum (default).
	 * @throws BadPrefixException 
	 * @throws NewOntologyException 
	 */
	public Brain() throws NewOntologyException, BadPrefixException {
		this(DEFAULT_PREFIX, "brain.owl", -1);
	}

	/**
	 * Creates a Brain instance from an ontology object.
	 * This method is useful for people with a pre-loaded ontology in memory and willing
	 * to quickly create a Brain instance from it.
	 * The default prefix (<brain#>) and ontology IRI (<brain.owl>) are used.
	 * The number of working threads for reasoning is set to the maximum (default).
	 * @param ontology
	 * @throws BadPrefixException 
	 * @throws NewOntologyException 
	 */
	public Brain(OWLOntology ontology) throws NewOntologyException {

		this.prefixManager = new DefaultPrefixManager(DEFAULT_PREFIX);

		this.manager = ontology.getOWLOntologyManager();
		this.ontology = ontology;

		this.factory = this.manager.getOWLDataFactory();

		ShortFormProvider shortFormProvider = new SimpleShortFormProvider();	
		Set<OWLOntology> importsClosure = this.ontology.getImportsClosure();
		this.bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(this.manager, importsClosure, shortFormProvider);
		this.entityChecker = new ShortFormEntityChecker(this.bidiShortFormProvider);

		Logger.getLogger("org.semanticweb.elk").setLevel(Level.OFF);

		this.reasonerFactory = new ElkReasonerFactory();
		this.reasoner = this.getReasonerFactory().createReasoner(this.ontology);

		this.isClassified = false;

		this.INTEGER = this.factory.getIntegerOWLDatatype();
		this.FLOAT = this.factory.getFloatOWLDatatype();
		this.BOOLEAN = this.factory.getBooleanOWLDatatype();
		update();
		try {
			this.addClass("http://www.w3.org/2002/07/owl#Thing");
		} catch (BrainException e) {
			//If the class throws an error it's no big deal.
			//It means that the top class was already present in the loaded
			//ontology
		}
	}


	/**
	 * Creates a Brain instance with the specified prefix and ontology IRI.
	 * The OWL entities created will inherit from this prefix.
	 * The number of working threads for reasoning is set to the passed value (numberOfWorkers parameter).
	 * @param prefix
	 * @param ontologyIri
	 * @param numberOfWorkers
	 * @throws BadPrefixException 
	 * @throws NewOntologyException 
	 */
	public Brain(String prefix, String ontologyIri, int numberOfWorkers) throws NewOntologyException, BadPrefixException {

		if(!prefix.equals(DEFAULT_PREFIX)){
			try {
				@SuppressWarnings("unused")
				URL url = new URL(prefix);
			} catch (MalformedURLException e) {
				throw new BadPrefixException(e);
			}
			if(!(prefix.endsWith("#") || prefix.endsWith("/"))){
				throw new BadPrefixException("The separator symbol must either be '/' or '#'");
			}
		}

		this.prefixManager = new DefaultPrefixManager(prefix);
		this.manager = OWLManager.createOWLOntologyManager();
		this.factory = manager.getOWLDataFactory();
		try {
			this.ontology = manager.createOntology(IRI.create(ontologyIri));
		} catch (OWLOntologyCreationException e) {
			throw new NewOntologyException(e);
		}

		ShortFormProvider shortFormProvider = new SimpleShortFormProvider();	
		Set<OWLOntology> importsClosure = this.ontology.getImportsClosure();
		this.bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(this.manager, importsClosure, shortFormProvider);
		this.entityChecker = new ShortFormEntityChecker(this.bidiShortFormProvider);

		Logger.getLogger("org.semanticweb.elk").setLevel(Level.OFF);

		if(numberOfWorkers != -1){
			this.configuration = new ElkReasonerConfiguration();
			this.configuration.getElkConfiguration().setParameter(ReasonerConfiguration.NUM_OF_WORKING_THREADS, Integer.toString(numberOfWorkers));
			this.reasonerFactory = new ElkReasonerFactory();
			this.reasoner = this.getReasonerFactory().createReasoner(this.ontology, this.configuration);
		}else{
			this.reasonerFactory = new ElkReasonerFactory();
			this.reasoner = this.getReasonerFactory().createReasoner(this.ontology);
		}

		this.isClassified = false;

		this.INTEGER = this.factory.getIntegerOWLDatatype();
		this.FLOAT = this.factory.getFloatOWLDatatype();
		this.BOOLEAN = this.factory.getBooleanOWLDatatype();
		update();
		try {
			this.addClass("http://www.w3.org/2002/07/owl#Thing");
		} catch (BrainException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Declare a prefix mapping. Will be used when the ontology is serialized
	 * (.owl file). Example of usage:
	 * <code>
	 * <pre>
	 * {@code
	 * Brain brain = new Brain();
	 * brain.prefix("http://www.example.org/", "example");
	 * }
	 * </pre>
	 * </code>
	 * @param longForm
	 * @param abbreviation
	 */
	public void prefix(String longForm, String abbreviation) {
		this.prefixManager.setPrefix(abbreviation + ":", longForm);
	}

	/**
	 * Add an OWL class to the ontology. An OWLClass
	 * object is returned. The input value can either be
	 * the short form of the class or the full IRI. Examples of input
	 * parameter: "http://example.org/MyClass" or "MyClass".
	 * @param className
	 * @return owlClass
	 * @throws ExistingClassException 
	 * @throws BadNameException 
	 */
	public OWLClass addClass(String className) throws BadNameException, ExistingClassException {
		if(isExternalEntity(className)){
			return addExternalClass(className);
		}else{
			try {
				this.getOWLClass(className);
				throw new ExistingClassException("The class '"+ className +"' already exists.");
			} catch (NonExistingClassException e) {
				validate(className);
				OWLClass owlClass = null;
				try{
					owlClass = this.factory.getOWLClass(className, this.prefixManager);
				} catch(RuntimeException re) {
					throw new BadNameException("The prefix you are using is not recognized. Use either no prefix or a valid URI. More info: " + re);
				}
				declare(owlClass);
				update();
				return owlClass;
			}
		}
	}

	/**
	 * Add an OWL class to the ontology. The class refers to an external class (external ontology).
	 * @param className
	 * @return owlClass
	 * @throws ExistingClassException 
	 * @throws BadNameException 
	 */
	private OWLClass addExternalClass(String className) throws ExistingClassException, BadNameException {
		if(this.ontology.containsClassInSignature(IRI.create(className))){
			throw new ExistingClassException("The class already '"+ className +"' already exists.");
		}
		validateExternalEntity(className);
		OWLClass owlClass = this.factory.getOWLClass(IRI.create(className));
		SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		String sf = shortFormProvider.getShortForm(owlClass);
		try {
			this.getOWLClass(sf);
			removeExternalClass(className);
			throw new ExistingClassException("A class as already the short form '"+ sf +"'. A class name as to be unique.");
		} catch (NonExistingClassException e) {
			declare(owlClass);
			update();
			return owlClass;
		}
	}    

	/**
	 * Check whether an entity is an external entity (URL present) or not.
	 * @param entityName
	 * @return isExternalEntity
	 */
	private boolean isExternalEntity(String entityName) {
		try {
			new URL(entityName);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}

	/**
	 * Remove an OWL class from the ontology. The class refers to an external class (external ontology).
	 * @param externalClass
	 */
	private void removeExternalClass(String externalClass) {
		OWLEntityRemover remover = new OWLEntityRemover(this.manager, Collections.singleton(this.ontology));
		OWLClass owlClassToRemove = this.factory.getOWLClass(IRI.create(externalClass));
		owlClassToRemove.accept(remover);
		this.manager.applyChanges(remover.getChanges());
		remover.reset();
		update();
	}

	/**
	 * Declare (register) an OWL entity in the ontology.
	 * @param owlEntity
	 */
	private void declare(OWLEntity owlEntity) {
		OWLDeclarationAxiom declarationAxiom = this.factory.getOWLDeclarationAxiom(owlEntity);
		manager.addAxiom(this.ontology, declarationAxiom);
	}

	/**
	 * Validate an OWL entity.
	 * @param entityName
	 * @throws BadNameException
	 */
	private void validate(String entityName) throws BadNameException {
		URL url;
		String prefix = null;
		if(this.prefixManager.getDefaultPrefix().equals(DEFAULT_PREFIX)){
			prefix = "http://localhost/";
		}else{
			prefix = this.prefixManager.getDefaultPrefix();
		}
		try {
			url = new URL(prefix + entityName);
			url.toURI();
		} catch (MalformedURLException e) {
			throw new BadNameException(e);
		} catch (URISyntaxException e) {
			throw new BadNameException("'"+entityName+"' is not valid valid name for an OWL entity. Use only characters that are valid for a URI (no space, etc...).");
		}
	}

	/**
	 * Validate an external OWL entity.
	 * @param entityIri
	 * @throws BadNameException
	 */
	private void validateExternalEntity(String entityIri) throws BadNameException {
		URL url;
		try {
			url = new URL(entityIri);
			url.toURI();
		} catch (MalformedURLException e) {
			throw new BadNameException(e);
		} catch (URISyntaxException e) {
			throw new BadNameException("'"+entityIri+"' is not valid valid name for an OWL entity. Use only characters that are valid for a URI (no space, etc...).");
		}
	}

	/**
	 * Add the axiom to the ontology.
	 * @param owlAxiom 
	 */
	private void addAxiom(OWLAxiom owlAxiom) {
		AddAxiom addAx = new AddAxiom(this.ontology, owlAxiom);
		this.manager.applyChange(addAx);
		update();
	}

	/**
	 * Add an OWL object property to the ontology. An OWLObjectProperty
	 * object is returned. The input value can either be
	 * the short form of the property or the full IRI. Examples of input
	 * parameter: "http://example.org/MyProperty" or "MyProperty".
	 * @param objectPropertyName
	 * @return owlObjectProperty
	 * @throws ExistingObjectPropertyException 
	 * @throws BadNameException 
	 */
	public OWLObjectProperty addObjectProperty(String objectPropertyName) throws ExistingObjectPropertyException, BadNameException {
		if(isExternalEntity(objectPropertyName)){
			return addExternalObjectProperty(objectPropertyName);
		}else{
			try {
				this.getOWLObjectProperty(objectPropertyName);
				throw new ExistingObjectPropertyException("The object property '"+ objectPropertyName +"' already exists.");
			} catch (NonExistingObjectPropertyException e) {
				validate(objectPropertyName);
				OWLObjectProperty owlObjectProperty = null;
				try{
					owlObjectProperty = this.factory.getOWLObjectProperty(objectPropertyName, this.prefixManager);
				} catch(RuntimeException re) {
					throw new BadNameException("The prefix you are using is not recognized. Use either no prefix or a valid URI. More info: " + re);
				}
				declare(owlObjectProperty);
				update();
				return owlObjectProperty;
			}
		}
	}

	/**
	 * Add an external OWL object property to the ontology.
	 * @param objectPropertyName
	 * @return owlObjectProperty
	 * @throws ExistingObjectPropertyException 
	 * @throws BadNameException 
	 */
	private OWLObjectProperty addExternalObjectProperty(String objectPropertyName) throws BadNameException, ExistingObjectPropertyException {
		if(this.ontology.containsObjectPropertyInSignature(IRI.create(objectPropertyName))){
			throw new ExistingObjectPropertyException("The object property already '"+ objectPropertyName +"' already exists.");
		}
		validateExternalEntity(objectPropertyName);
		OWLObjectProperty owlObjectProperty = this.factory.getOWLObjectProperty(IRI.create(objectPropertyName));
		SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		String sf = shortFormProvider.getShortForm(owlObjectProperty);
		try {
			this.getOWLObjectProperty(sf);
			removeExternalObjectProperty(objectPropertyName);
			throw new ExistingObjectPropertyException("An object property as already the short form '"+ sf +"'. The short form cannot be re-used name as to be unique.");
		} catch (NonExistingObjectPropertyException e) {
			declare(owlObjectProperty);
			update();
			return owlObjectProperty;
		}
	}

	/**
	 * Remove an external OWL object property to the ontology.
	 * @param externalObjectProperty
	 */
	private void removeExternalObjectProperty(String externalObjectProperty) {
		OWLEntityRemover remover = new OWLEntityRemover(this.manager, Collections.singleton(this.ontology));
		OWLObjectProperty owlObjectPropertyToRemove = this.factory.getOWLObjectProperty(IRI.create(externalObjectProperty));
		owlObjectPropertyToRemove.accept(remover);
		this.manager.applyChanges(remover.getChanges());
		remover.reset();
		update();
	}

	/**
	 * Add an OWL data property to the ontology. An OWLDataProperty
	 * object is returned. The input value can either be
	 * the short form of the property or the full IRI. Examples of input
	 * parameter: "http://example.org/MyProperty" or "MyProperty".
	 * @param dataPropertyName
	 * @return owlDataProperty
	 * @throws ExistingDataPropertyException 
	 * @throws BadNameException 
	 */
	public OWLDataProperty addDataProperty(String dataPropertyName) throws ExistingDataPropertyException, BadNameException {
		if(isExternalEntity(dataPropertyName)){
			return addExternalDataProperty(dataPropertyName);
		}else{
			try {
				this.getOWLDataProperty(dataPropertyName);
				throw new ExistingDataPropertyException("The data property '"+ dataPropertyName +"' already exists.");
			} catch (NonExistingDataPropertyException e) {
				validate(dataPropertyName);
				OWLDataProperty owlDataProperty = null;
				try{
					owlDataProperty = this.factory.getOWLDataProperty(dataPropertyName, this.prefixManager);
				} catch(RuntimeException re) {
					throw new BadNameException("The prefix you are using is not recognized. Use either no prefix or a valid URI. More info: " + re);
				}
				declare(owlDataProperty);
				update();
				return owlDataProperty;
			}
		}
	}

	/**
	 * Add an external OWL data property to the ontology.
	 * @param dataPropertyName
	 * @return owlDataProperty
	 * @throws ExistingDataPropertyException 
	 * @throws BadNameException 
	 */
	private OWLDataProperty addExternalDataProperty(String dataPropertyName) throws BadNameException, ExistingDataPropertyException {
		if(this.ontology.containsDataPropertyInSignature(IRI.create(dataPropertyName))){
			throw new ExistingDataPropertyException("The data property already '"+ dataPropertyName +"' already exists.");
		}
		validateExternalEntity(dataPropertyName);
		OWLDataProperty owlDataProperty = this.factory.getOWLDataProperty(IRI.create(dataPropertyName));
		SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		String sf = shortFormProvider.getShortForm(owlDataProperty);
		try {
			this.getOWLDataProperty(sf);
			removeExternalDataProperty(dataPropertyName);
			throw new ExistingDataPropertyException("An entity as already the short form '"+ sf +"'. The short form cannot be re-used name as to be unique.");
		} catch (NonExistingDataPropertyException e) {
			declare(owlDataProperty);
			update();
			return owlDataProperty;
		}
	}

	/**
	 * Remove an external OWL data property to the ontology.
	 * @param externalDataProperty
	 */
	private void removeExternalDataProperty(String externalDataProperty) {
		OWLEntityRemover remover = new OWLEntityRemover(this.manager, Collections.singleton(this.ontology));
		OWLDataProperty owlDataPropertyToRemove = this.factory.getOWLDataProperty(IRI.create(externalDataProperty));
		owlDataPropertyToRemove.accept(remover);
		this.manager.applyChanges(remover.getChanges());
		remover.reset();
		update();
	}

	/**
	 * Add an OWL annotation property to the ontology. An OWLAnnotationProperty
	 * object is returned. The input value can either be
	 * the short form of the property or the full IRI. Examples of input
	 * parameter: "http://example.org/MyProperty" or "MyProperty".
	 * @param annotationProperty
	 * @return owlAnnotationProperty
	 * @throws ExistingDataPropertyException 
	 * @throws BadNameException 
	 */
	public OWLAnnotationProperty addAnnotationProperty(String annotationProperty) throws ExistingAnnotationPropertyException, BadNameException {
		if(isExternalEntity(annotationProperty)){
			return addExternalAnnotationProperty(annotationProperty);
		}else{
			try {
				this.getOWLAnnotationProperty(annotationProperty);
				throw new ExistingAnnotationPropertyException("The annotation property '"+ annotationProperty +"' already exists.");
			} catch (NonExistingAnnotationPropertyException e) {
				validate(annotationProperty);
				OWLAnnotationProperty owlAnnotationProperty = null;
				try{
					owlAnnotationProperty = this.factory.getOWLAnnotationProperty(annotationProperty, this.prefixManager);
				} catch(RuntimeException re) {
					throw new BadNameException("The prefix you are using is not recognized. Use either no prefix or a valid URI. More info: " + re);
				}
				declare(owlAnnotationProperty);
				update();
				return owlAnnotationProperty;
			}
		}
	}

	/**
	 * Add an external OWL annotation property to the ontology.
	 * @param annotationPropertyName
	 * @return owlAnnotationProperty
	 * @throws ExistingAnnotationPropertyException 
	 * @throws BadNameException 
	 */
	private OWLAnnotationProperty addExternalAnnotationProperty(String annotationPropertyName) throws BadNameException, ExistingAnnotationPropertyException {
		if(this.ontology.containsAnnotationPropertyInSignature(IRI.create(annotationPropertyName))){
			throw new ExistingAnnotationPropertyException("The annotation property already '"+ annotationPropertyName +"' already exists.");
		}
		validateExternalEntity(annotationPropertyName);
		OWLAnnotationProperty owlAnnotationProperty = this.factory.getOWLAnnotationProperty(IRI.create(annotationPropertyName));
		SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		String sf = shortFormProvider.getShortForm(owlAnnotationProperty);
		try {
			this.getOWLAnnotationProperty(sf);
			removeExternalAnnotationProperty(annotationPropertyName);
			throw new ExistingAnnotationPropertyException("An entity as already the short form '"+ sf +"'. The short form cannot be re-used name as to be unique.");
		} catch (NonExistingAnnotationPropertyException e) {
			declare(owlAnnotationProperty);
			update();
			return owlAnnotationProperty;
		}
	}

	/**
	 * Remove an external OWL annotation property to the ontology.
	 * @param externalAnnotationProperty
	 */
	private void removeExternalAnnotationProperty(String externalAnnotationProperty) {
		OWLEntityRemover remover = new OWLEntityRemover(this.manager, Collections.singleton(this.ontology));
		OWLAnnotationProperty owlAnnotationPropertyToRemove = this.factory.getOWLAnnotationProperty(IRI.create(externalAnnotationProperty));
		owlAnnotationPropertyToRemove.accept(remover);
		this.manager.applyChanges(remover.getChanges());
		remover.reset();
		update();
	}

	/**
	 * Set the reasoner is the not classified state and flush it.
	 * A classification will have to be redone.
	 */
	private void update() {
		this.reasoner.flush();
		this.isClassified = false;
	}

	/**
	 * Returns the OWL class corresponding to the input string (short form).
	 * @param className
	 * @return owlClass
	 * @throws NonExistingClassException 
	 */
	public OWLClass getOWLClass(String className) throws NonExistingClassException {
		OWLEntity entity = this.bidiShortFormProvider.getEntity(className);
		if(entity != null && entity.isOWLClass()){
			return (OWLClass) entity;
		}else{
			throw new NonExistingClassException("The entity '"+ className +"' does not exist or is not an OWL class.");
		}
	}

	/**
	 * Returns the OWL annotation property corresponding to the input string (short form).
	 * @param propertyName
	 * @return owlAnnotationProperty
	 * @throws NonExistingAnnotationPropertyException 
	 */
	public OWLAnnotationProperty getOWLAnnotationProperty(String propertyName) throws NonExistingAnnotationPropertyException {
		OWLEntity entity = this.bidiShortFormProvider.getEntity(propertyName);
		if(entity != null && entity.isOWLAnnotationProperty()){
			return (OWLAnnotationProperty) entity;
		}else{
			throw new NonExistingAnnotationPropertyException("The annotation property '"+ propertyName +"' does not exist.");
		}
	}

	/**
	 * Returns the OWL object property corresponding to the input string (short form).
	 * @param objectPropertyName
	 * @return owlObjectProperty
	 * @throws NonExistingObjectPropertyException 
	 */
	public OWLObjectProperty getOWLObjectProperty(String objectPropertyName) throws NonExistingObjectPropertyException {
		OWLEntity entity = this.bidiShortFormProvider.getEntity(objectPropertyName);
		if(entity != null && entity.isOWLObjectProperty()){
			return (OWLObjectProperty) entity;
		}else{
			throw new NonExistingObjectPropertyException("The object property '"+ objectPropertyName +"' does not exist.");
		}
	}

	/**
	 * Returns the OWL data property corresponding to the input string (short form).
	 * @param dataPropertyName
	 * @return owlDataProperty
	 * @throws NonExistingDataPropertyException 
	 */
	public OWLDataProperty getOWLDataProperty(String dataPropertyName) throws NonExistingDataPropertyException {
		OWLEntity entity = this.bidiShortFormProvider.getEntity(dataPropertyName);
		if(entity != null && entity.isOWLDataProperty()){
			return (OWLDataProperty) entity;
		}else{
			throw new NonExistingDataPropertyException("The data property '"+ dataPropertyName +"' does not exist.");
		}
	}

	/**
	 * Declare a subClassOf axiom.
	 * @param subClass
	 * @param superClass
	 * @throws ClassExpressionException 
	 */
	public void subClassOf(String subClass, String superClass) throws ClassExpressionException {
		OWLClassExpression subClassExpression = parseClassExpression(subClass);
		OWLClassExpression superClassExpression = parseClassExpression(superClass);
		OWLSubClassOfAxiom subClassAxiom = this.factory.getOWLSubClassOfAxiom(subClassExpression, superClassExpression);
		addAxiom(subClassAxiom);
	}

	/**
	 * Declare an equivalence axiom.
	 * @param class1
	 * @param class2
	 * @throws ClassExpressionException 
	 */
	public void equivalentClasses(String class1, String class2) throws ClassExpressionException {
		OWLClassExpression classExpression1 = parseClassExpression(class1);
		OWLClassExpression classExpression2 = parseClassExpression(class2);
		OWLEquivalentClassesAxiom equivalentClassAxiom = this.factory.getOWLEquivalentClassesAxiom(classExpression1, classExpression2);
		addAxiom(equivalentClassAxiom);
	}

	/**
	 * Declare an disjoint axiom.
	 * @param class1
	 * @param class2
	 * @throws ClassExpressionException 
	 */
	public void disjointClasses(String class1, String class2) throws ClassExpressionException {
		OWLClassExpression classExpression1 = parseClassExpression(class1);
		OWLClassExpression classExpression2 = parseClassExpression(class2);
		OWLDisjointClassesAxiom disjointClassAxiom = this.factory.getOWLDisjointClassesAxiom(classExpression1, classExpression2);
		addAxiom(disjointClassAxiom);
	}

	/**
	 * Declare an transitive axiom.
	 * @param propertyExpression
	 * @throws ObjectPropertyExpressionException 
	 */
	public void transitive(String propertyExpression) throws ObjectPropertyExpressionException {
		OWLObjectPropertyExpression expression = parseObjectPropertyExpression(propertyExpression);
		OWLTransitiveObjectPropertyAxiom axiom = this.factory.getOWLTransitiveObjectPropertyAxiom(expression);
		addAxiom(axiom);
	}

	/**
	 * Declare an reflexive axiom.
	 * @param propertyExpression
	 * @throws ObjectPropertyExpressionException 
	 */
	public void reflexive(String propertyExpression) throws ObjectPropertyExpressionException {
		OWLObjectPropertyExpression expression = parseObjectPropertyExpression(propertyExpression);
		OWLReflexiveObjectPropertyAxiom axiom = this.factory.getOWLReflexiveObjectPropertyAxiom(expression);
		addAxiom(axiom);
	}

	/**
	 * Declare an functional axiom.
	 * @param propertyExpression
	 * @throws DataPropertyExpressionException 
	 */
	public void functional(String propertyExpression) throws DataPropertyExpressionException {
		OWLDataPropertyExpression expression = parseDataPropertyExpression(propertyExpression);
		OWLFunctionalDataPropertyAxiom axiom = this.factory.getOWLFunctionalDataPropertyAxiom(expression);
		addAxiom(axiom);
	}

	/**
	 * Declare an domain axiom.
	 * @param propertyExpression
	 * @param classExpression
	 * @throws ClassExpressionException
	 * @throws NonExistingEntityException
	 */
	public void domain(String propertyExpression, String classExpression) throws ClassExpressionException, NonExistingEntityException{
		OWLClassExpression domainExpression = parseClassExpression(classExpression);
		try {
			OWLObjectPropertyExpression owlPropertyExpression = parseObjectPropertyExpression(propertyExpression);
			OWLObjectPropertyDomainAxiom axiom = this.factory.getOWLObjectPropertyDomainAxiom(owlPropertyExpression, domainExpression);
			addAxiom(axiom);
		} catch (ObjectPropertyExpressionException e) {
			OWLDataPropertyExpression owlPropertyExpression;
			try {
				owlPropertyExpression = parseDataPropertyExpression(propertyExpression);
				OWLDataPropertyDomainAxiom axiom = this.factory.getOWLDataPropertyDomainAxiom(owlPropertyExpression, domainExpression);
				addAxiom(axiom);
			} catch (DataPropertyExpressionException e1) {
				throw new NonExistingEntityException("The property '"+propertyExpression+"' does not exist.");
			}
		}
	}

	/**
	 * Declare an range axiom.
	 * @param propertyExpression
	 * @param classExpression
	 * @throws ClassExpressionException
	 * @throws ObjectPropertyExpressionException
	 */
	public void range(String propertyExpression, String classExpression) throws ClassExpressionException, ObjectPropertyExpressionException {
		OWLObjectPropertyExpression owlPropertyExpression = parseObjectPropertyExpression(propertyExpression);
		OWLClassExpression rangeExpression = parseClassExpression(classExpression);
		OWLObjectPropertyRangeAxiom axiom = this.factory.getOWLObjectPropertyRangeAxiom(owlPropertyExpression, rangeExpression);
		addAxiom(axiom);
	}

	/**
	 * Declare an range axiom.
	 * @param propertyExpression
	 * @param dataType
	 * @throws DataPropertyExpressionException
	 */
	public void range(String propertyExpression, OWLDatatype dataType) throws DataPropertyExpressionException {
		OWLDataPropertyExpression owlPropertyExpression = parseDataPropertyExpression(propertyExpression);
		OWLDataPropertyRangeAxiom axiom = this.factory.getOWLDataPropertyRangeAxiom(owlPropertyExpression, dataType);
		addAxiom(axiom);
	}

	/**
	 * Declare an equivalentProperties axiom.
	 * @param property1
	 * @param property2
	 * @throws BrainException
	 */
	public void equivalentProperties(String property1, String property2) throws BrainException {
		try {
			OWLObjectPropertyExpression owlProperty1 = parseObjectPropertyExpression(property1);
			OWLObjectPropertyExpression owlProperty2 = parseObjectPropertyExpression(property2);
			OWLEquivalentObjectPropertiesAxiom axiom = this.factory.getOWLEquivalentObjectPropertiesAxiom(owlProperty1, owlProperty2);
			addAxiom(axiom);
		} catch (ObjectPropertyExpressionException e) {
			try{
				OWLDataPropertyExpression owlProperty1 = parseDataPropertyExpression(property1);
				OWLDataPropertyExpression owlProperty2 = parseDataPropertyExpression(property2);
				OWLEquivalentDataPropertiesAxiom axiom = this.factory.getOWLEquivalentDataPropertiesAxiom(owlProperty1, owlProperty2);
				addAxiom(axiom);
			} catch (DataPropertyExpressionException e1) {
				throw new BrainException("One of the properties ('"+property1+"' or '"+property2+"') does not exist or the properties have different types.");
			}
		}
	}

	/**
	 * Declare an subPropertyOf axiom.
	 * @param subProperty
	 * @param superProperty
	 * @throws BrainException
	 */
	public void subPropertyOf(String subProperty, String superProperty) throws BrainException {
		try{
			OWLObjectPropertyExpression subPropertyExpression = parseObjectPropertyExpression(subProperty);
			OWLObjectPropertyExpression superPropertyExpression = parseObjectPropertyExpression(superProperty);
			OWLSubObjectPropertyOfAxiom subPropertyAxiom = this.factory.getOWLSubObjectPropertyOfAxiom(subPropertyExpression, superPropertyExpression);
			addAxiom(subPropertyAxiom);
		} catch(ObjectPropertyExpressionException e){
			try{
				OWLDataPropertyExpression subPropertyExpression = parseDataPropertyExpression(subProperty);
				OWLDataPropertyExpression superPropertyExpression = parseDataPropertyExpression(superProperty);
				OWLSubDataPropertyOfAxiom subPropertyAxiom = this.factory.getOWLSubDataPropertyOfAxiom(subPropertyExpression, superPropertyExpression);
				addAxiom(subPropertyAxiom);
			}catch (DataPropertyExpressionException e1) {
				throw new BrainException("One of the properties ('"+subProperty+"' or '"+superProperty+"') does not exist or the properties have different types.");
			}
		}
	}

	/**
	 * Declare an chain axiom.
	 * @param chain
	 * @param superProperty
	 * @throws ObjectPropertyExpressionException
	 */
	public void chain(String chain, String superProperty) throws ObjectPropertyExpressionException {
		List<OWLObjectPropertyExpression> chainExpression = parseObjectPropertyChain(chain);
		OWLObjectPropertyExpression superPropertyExpression = parseObjectPropertyExpression(superProperty);
		OWLSubPropertyChainOfAxiom axiom = this.factory.getOWLSubPropertyChainOfAxiom(chainExpression, superPropertyExpression);
		addAxiom(axiom);
	}

	/**
	 * Declare an annotation axiom.
	 * @param entity
	 * @param annotationProperty
	 * @param content
	 * @throws NonExistingEntityException
	 */
	public void annotation(String entity, String annotationProperty, String content) throws NonExistingEntityException {
		if(this.bidiShortFormProvider.getEntity(entity) !=  null){
			OWLEntity owlEntity = this.bidiShortFormProvider.getEntity(entity);
			OWLAnnotationProperty owlAnnotationProperty = this.getOWLAnnotationProperty(annotationProperty);
			OWLAnnotation labelAnnotation = this.factory.getOWLAnnotation(owlAnnotationProperty, this.factory.getOWLLiteral(content));
			OWLAxiom axiom = this.factory.getOWLAnnotationAssertionAxiom(owlEntity.getIRI(), labelAnnotation);
			addAxiom(axiom);
		}else{
			throw new NonExistingEntityException("The entity '"+entity+"' does not exist.");
		}
	}

	/**
	 * Declare an annotation axiom.
	 * @param entity
	 * @param annotationProperty
	 * @param content
	 * @throws NonExistingEntityException
	 */
	private void annotation(String entity, OWLAnnotationProperty annotationProperty, String content) throws NonExistingEntityException {
		if(this.bidiShortFormProvider.getEntity(entity) !=  null){
			OWLEntity owlEntity = this.bidiShortFormProvider.getEntity(entity);
			OWLAnnotation labelAnnotation = this.factory.getOWLAnnotation(annotationProperty, this.factory.getOWLLiteral(content));
			OWLAxiom axiom = this.factory.getOWLAnnotationAssertionAxiom(owlEntity.getIRI(), labelAnnotation);
			addAxiom(axiom);
		}else{
			throw new NonExistingEntityException("The entity '"+entity+"' does not exist.");
		}
	}

	/**
	 * Retrieve the content of an annotation.
	 * @param entity
	 * @param annotationProperty
	 * @throws NonExistingEntityException
	 * @return content
	 */
	private String getAnnotation(String entity, OWLAnnotationProperty annotationProperty) throws NonExistingEntityException {
		if(this.bidiShortFormProvider.getEntity(entity) !=  null){
			OWLEntity owlEntity = this.bidiShortFormProvider.getEntity(entity);
			for (OWLAnnotation annotation : owlEntity.getAnnotations(this.ontology, annotationProperty)) {
				if (annotation.getValue() instanceof OWLLiteral) {
					OWLLiteral val = (OWLLiteral) annotation.getValue();
					return val.getLiteral();
				}
			}
		}else{
			throw new NonExistingEntityException("The entity '"+entity+"' does not exist.");
		}
		throw new NonExistingEntityException("The entity '"+entity+"' has no annotation of this type attached to it.");
	}

	/**
	 * Retrieve the content of an annotation.
	 * @param entity
	 * @param annotationProperty
	 * @throws NonExistingEntityException
	 * @return content
	 */
	public String getAnnotation(String entity, String annotationProperty) throws NonExistingEntityException {
		OWLAnnotationProperty owlAnnotationProperty = this.getOWLAnnotationProperty(annotationProperty);
		return this.getAnnotation(entity, owlAnnotationProperty);
	}

	/**
	 * Set the content of the label.
	 * @param entity
	 * @param label
	 * @throws NonExistingEntityException
	 */
	public void label(String entity, String label) throws NonExistingEntityException {
		this.annotation(entity, this.factory.getRDFSLabel(), label);
	}

	/**
	 * Get the content of the label.
	 * @param entity
	 * @throws NonExistingEntityException
	 * @return label
	 */
	public String getLabel(String entity) throws NonExistingEntityException {
		return this.getAnnotation(entity, this.factory.getRDFSLabel());
	}

	/**
	 * Set the content of the comment.
	 * @param entity
	 * @param comment
	 * @throws NonExistingEntityException
	 */
	public void comment(String entity, String comment) throws NonExistingEntityException {
		this.annotation(entity, this.factory.getRDFSComment(), comment);
	}

	/**
	 * Get the content of the comment.
	 * @param entity
	 * @throws NonExistingEntityException
	 * @return comment
	 */
	public String getComment(String entity) throws NonExistingEntityException {
		return this.getAnnotation(entity, this.factory.getRDFSComment());
	}

	/**
	 * Set the content of rdfs:isDefinedBy.
	 * @param entity
	 * @param isDefinedBy
	 * @throws NonExistingEntityException
	 */
	public void isDefinedBy(String entity, String isDefinedBy) throws NonExistingEntityException {
		this.annotation(entity, this.factory.getRDFSIsDefinedBy(), isDefinedBy);
	}

	/**
	 * Get the content of the rdfs:isDefinedBy.
	 * @param entity
	 * @throws NonExistingEntityException
	 * @return rdfs:isDefinedBy
	 */
	public String getIsDefinedBy(String entity) throws NonExistingEntityException {
		return this.getAnnotation(entity, this.factory.getRDFSIsDefinedBy());
	}

	/**
	 * Set the content of rdfs:seeAlso.
	 * @param entity
	 * @param seeAlso
	 * @throws NonExistingEntityException
	 */
	public void seeAlso(String entity, String seeAlso) throws NonExistingEntityException {
		this.annotation(entity, this.factory.getRDFSSeeAlso(), seeAlso);
	}

	/**
	 * Get the content of the rdfs:seeAlso.
	 * @param entity
	 * @throws NonExistingEntityException
	 * @return rdfs:isDefinedBy
	 */
	public String getSeeAlso(String entity) throws NonExistingEntityException {
		return this.getAnnotation(entity, this.factory.getRDFSSeeAlso());
	}

	/**
	 * Converts a string into an OWLClassExpression. If a problem is encountered, an error is thrown.
	 * @param expression
	 * @return owlClassExpression
	 * @throws ClassExpressionException 
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


	/**
	 * Converts a string representing an OWL class expression with labels into 
	 * an OWLClassExpression object. If a problem is 
	 * encountered, an error is thrown.
	 * @param labelClassExpression
	 * @return owlClassExpression
	 * @throws ClassExpressionException 
	 */
	public OWLClassExpression parseLabelClassExpression(String labelClassExpression) throws ClassExpressionException {
		OWLClassExpression owlClassExpression = null;
		ManchesterOWLSyntaxEditorParser parser = getLabelParser(labelClassExpression);
		try {
			owlClassExpression = parser.parseClassExpression();
		} catch (ParserException e) {
			throw new ClassExpressionException(e);
		}
		return owlClassExpression;
	}


	/**
	 * Converts a string into an OWLObjectPropertyExpression. If a problem is encountered, an error is thrown.
	 * @param objectPropertyExpression
	 * @return owlObjectPropertyExpression
	 * @throws ObjectPropertyExpressionException 
	 */
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

	/**
	 * Converts a string into an OWLDataPropertyExpression. If a problem is encountered, an error is thrown.
	 * @param dataPropertyExpression
	 * @return owlDataPropertyExpression
	 * @throws DataPropertyExpressionException 
	 */
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

	/**
	 * Converts a string into a list of OWLObjectPropertyExpression. If a problem is encountered, an error is thrown.
	 * @param chainExpression
	 * @return owlObjectPropertyExpressions
	 * @throws ObjectPropertyExpressionException 
	 */
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
	 * Instantiate a new Manchester syntax parser.
	 * @param expression
	 * @return parser
	 */
	private ManchesterOWLSyntaxEditorParser getParser(String expression) {	
		ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(this.factory, expression);
		parser.setDefaultOntology(this.ontology);
		parser.setOWLEntityChecker(this.entityChecker);
		return parser;
	}

	/**
	 * Instantiate a new Manchester syntax parser for labels.
	 * @param labelClassExpression
	 * @return parser
	 */
	private ManchesterOWLSyntaxEditorParser getLabelParser(String labelClassExpression) {
		List<OWLAnnotationProperty> rdfsLabels = Arrays.asList(this.factory.getRDFSLabel());
		ShortFormProvider sfp = new AnnotationValueShortFormProvider(rdfsLabels, 
				Collections.<OWLAnnotationProperty, List<String>> emptyMap(), this.manager);
		BidirectionalShortFormProvider bidiLabel = new BidirectionalShortFormProviderAdapter(this.manager.getOntologies(), sfp);
		ShortFormEntityChecker labelChecker = new ShortFormEntityChecker(bidiLabel);
		ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(this.factory, labelClassExpression);
		parser.setOWLEntityChecker(labelChecker);
		parser.setDefaultOntology(this.ontology);
		return parser;
	}


	/**
	 * Save the ontology at the specified location.
	 * The ontology is serialized using Manchester syntax.
	 * @param path
	 * @throws StorageException 
	 */
	public void save(String path) throws StorageException {
		File file = new File(path);
		OWLOntologyFormat format = this.manager.getOntologyFormat(this.ontology);
		ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
		if (format.isPrefixOWLOntologyFormat()) {
			manSyntaxFormat.copyPrefixesFrom(this.prefixManager);
		}
		try {
			this.manager.saveOntology(this.ontology, manSyntaxFormat, IRI.create(file));
		} catch (OWLOntologyStorageException e) {
			throw new StorageException(e);
		}
	}

	/**
	 * Load an external ontology from it's IRI or from a local file
	 * and merge it with the current ontology.
	 * @param pathToOntology
	 * @throws NewOntologyException 
	 * @throws ExistingEntityException 
	 */
	public void learn(String pathToOntology) throws NewOntologyException, ExistingEntityException {
		OWLOntologyManager newManager = OWLManager.createOWLOntologyManager();
		OWLOntology newOnto;
		if(isExternalEntity(pathToOntology)){
			IRI iriOnto = IRI.create(pathToOntology);
			try {
				newOnto = newManager.loadOntologyFromOntologyDocument(iriOnto);
			} catch (OWLOntologyCreationException e) {
				throw new NewOntologyException(e);
			}
		}else{
			File file = new File(pathToOntology);
			try {
				newOnto = newManager.loadOntologyFromOntologyDocument(file);
			} catch (OWLOntologyCreationException e) {
				throw new NewOntologyException(e);
			}
		}

		SimpleShortFormProvider sf = new SimpleShortFormProvider();
		Set<OWLOntology> importsClosure = newOnto.getImportsClosure();
		BidirectionalShortFormProviderAdapter newBidiShortFormProvider = new BidirectionalShortFormProviderAdapter(newManager, importsClosure, sf);
		for (String shortFromNewOnto : newBidiShortFormProvider.getShortForms()) {
			if(this.bidiShortFormProvider.getEntity(shortFromNewOnto) != null){
				OWLEntity existingEntity = this.bidiShortFormProvider.getEntity(shortFromNewOnto);
				OWLEntity newEntity = newBidiShortFormProvider.getEntity(shortFromNewOnto);
				boolean identicalEntities = false;
				if(newEntity.getIRI().equals(existingEntity.getIRI()) && newEntity.getEntityType().equals(existingEntity.getEntityType())){
					identicalEntities = true;
				}

				if(!shortFromNewOnto.equals("Thing") && !identicalEntities){
					throw new ExistingEntityException("The entity '"+shortFromNewOnto+"' already exists and is of different type or has a" +
							"different prefix.");
				}
			}
		}

		//Keep the prefix information if present
		OWLOntologyFormat format = newManager.getOntologyFormat(newOnto);
		if (format.isPrefixOWLOntologyFormat()) {
			PrefixOWLOntologyFormat newPrefixesFormat = format.asPrefixOWLOntologyFormat();
			Set<String> newPrefixes = newPrefixesFormat.getPrefixNames();
			for (String prefix : newPrefixes) {
				if(!this.prefixManager.containsPrefixMapping(prefix)){
					prefix(newPrefixesFormat.getPrefix(prefix), prefix.replaceAll(":", ""));
				}
			}
		}

		//Transfer all the axioms from the old ontology into the new one
		//Bottleneck
		this.manager.addAxioms(this.ontology, newOnto.getAxioms());
		update();
	}


	/**
	 * Load another brain object. The axioms are extracted
	 * and merged with the current one.
	 * 
	 * @throws NewOntologyException 
	 * @throws ExistingEntityException 
	 */
	public void learn(Brain brainToLearn) throws ExistingEntityException {

		for (String shortFromNewOnto : brainToLearn.getBidiShortFormProvider().getShortForms()) {
			if(this.bidiShortFormProvider.getEntity(shortFromNewOnto) != null){
				OWLEntity existingEntity = this.bidiShortFormProvider.getEntity(shortFromNewOnto);
				OWLEntity newEntity = brainToLearn.getBidiShortFormProvider().getEntity(shortFromNewOnto);
				boolean identicalEntities = false;
				if(newEntity.getIRI().equals(existingEntity.getIRI()) && newEntity.getEntityType().equals(existingEntity.getEntityType())){
					identicalEntities = true;
				}

				if(!shortFromNewOnto.equals("Thing") && !identicalEntities){
					throw new ExistingEntityException("The entity '"+shortFromNewOnto+"' already exists and is of different type or has a" +
							"different prefix.");
				}
			}
		}

		//Keep the prefix information if present
		for (String prefix : brainToLearn.getPrefixManager().getPrefixNames()) {
			if(!this.prefixManager.containsPrefixMapping(prefix)){
				prefix(brainToLearn.getPrefixManager().getPrefix(prefix), prefix.replaceAll(":", ""));
			}
		}

		this.manager.addAxioms(this.ontology, brainToLearn.getOntology().getAxioms());
		update();
	}


	/**
	 * Classify the ontology. It is usually the most expensive
	 * operation, so use it carefully!
	 */
	public void classify() {
		this.reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		this.isClassified = true;
	}

	/**
	 * Test whether the ontology is following an OWL 2 EL profile or not.
	 * @return hasElProfile
	 */
	public boolean hasElProfile() {
		OWL2ELProfile profile = new OWL2ELProfile();
		OWLProfileReport report = profile.checkOntology(this.ontology);
		if(report.getViolations().size() == 0){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Returns the list of violations of the OWL 2 EL profile.
	 * @return violations
	 */
	public List<String> getElProfileViolations() {
		OWL2ELProfile profile = new OWL2ELProfile();
		OWLProfileReport report = profile.checkOntology(this.ontology);
		ArrayList<String> violations = new ArrayList<String>();
		if(report.getViolations().size() != 0){
			for (OWLProfileViolation violation : report.getViolations()) {
				violations.add(violation.toString());
			}
		}	
		return violations;
	}

	/**
	 * Returns the list of subclasses from the expression.
	 * The second parameter is a flag telling whether only the direct classes
	 * should be returned. The method is synchronized in order
	 * to avoid concurrency problems that could arise in threaded
	 * environment (such as a web server with a shared Brain instance).
	 * @param classExpression
	 * @param direct
	 * @return subClasses
	 * @throws ClassExpressionException 
	 */
	public synchronized List<String> getSubClasses(String classExpression, boolean direct) throws ClassExpressionException {
		OWLClassExpression owlClassExpression = parseClassExpression(classExpression);
		return getSubClasses(owlClassExpression, direct);
	}

	/**
	 * Returns the list of subclasses from the OWL expression.
	 * The second parameter is a flag telling whether only the direct classes
	 * should be returned.
	 * @param owlClassExpression
	 * @param direct
	 * @return subClasses
	 */
	private List<String> getSubClasses(OWLClassExpression owlClassExpression, boolean direct) {
		Set<OWLClass> subClasses = null;
		//Can be simplified once Elk would have implemented a better way to deal with anonymous classes
		if(owlClassExpression.isAnonymous()){
			OWLClass anonymousClass = getTemporaryAnonymousClass(owlClassExpression);
			this.classify();
			subClasses = this.reasoner.getSubClasses(anonymousClass, direct).getFlattened();
			removeTemporaryAnonymousClass(anonymousClass);
		}else{
			if(!this.isClassified){
				this.classify();
			}
			subClasses = this.reasoner.getSubClasses(owlClassExpression, direct).getFlattened();
		}
		return sortClasses(subClasses);
	}


	/**
	 * Returns the list of subclasses from the label expression.
	 * The second parameter is a flag telling whether only the direct classes
	 * should be returned. The method is synchronized in order
	 * to avoid concurrency problems that could arise in threaded
	 * environment (such as a web server with a shared Brain instance).
	 * @param labelClassExpression
	 * @param direct
	 * @return subClasses
	 * @throws ClassExpressionException 
	 */
	public synchronized List<String> getSubClassesFromLabel(String labelClassExpression, boolean direct) throws ClassExpressionException {
		OWLClassExpression owlClassExpression = parseLabelClassExpression(labelClassExpression);
		return getSubClasses(owlClassExpression, direct);
	}

	/**
	 * Returns the list of super classes from the expression.
	 * The second parameter is a flag telling whether only the direct classes
	 * should be returned. The method is synchronized in order
	 * to avoid concurrency problems that could arise in threaded
	 * environment (such as a web server with a shared Brain instance).
	 * @param classExpression
	 * @param direct
	 * @return superClasses
	 * @throws ClassExpressionException 
	 */
	public synchronized List<String> getSuperClasses(String classExpression, boolean direct) throws ClassExpressionException {
		OWLClassExpression owlClassExpression = parseClassExpression(classExpression);
		return getSuperClasses(owlClassExpression, direct);
	}

	/**
	 * Returns the list of super classes from the OWL expression.
	 * The second parameter is a flag telling whether only the direct classes
	 * should be returned.
	 * @param owlClassExpression
	 * @param direct
	 * @return subClasses
	 */
	private List<String> getSuperClasses(OWLClassExpression owlClassExpression, boolean direct) {
		Set<OWLClass> superClasses = null;
		//Can be simplified once Elk would have implemented a better way to deal with anonymous classes
		if(owlClassExpression.isAnonymous()){
			OWLClass anonymousClass = getTemporaryAnonymousClass(owlClassExpression);
			this.classify();
			superClasses = this.reasoner.getSuperClasses(anonymousClass, direct).getFlattened();
			removeTemporaryAnonymousClass(anonymousClass);
		}else{
			if(!this.isClassified){
				this.classify();
			}
			superClasses = this.reasoner.getSuperClasses(owlClassExpression, direct).getFlattened();
		}
		return sortClasses(superClasses);
	}


	/**
	 * Returns the list of super classes from the label expression.
	 * The second parameter is a flag telling whether only the direct classes
	 * should be returned. The method is synchronized in order
	 * to avoid concurrency problems that could arise in threaded
	 * environment (such as a web server with a shared Brain instance).
	 * @param labelClassExpression
	 * @param direct
	 * @return superClasses
	 * @throws ClassExpressionException 
	 */
	public synchronized List<String> getSuperClassesFromLabel(String labelClassExpression, boolean direct) throws ClassExpressionException {
		OWLClassExpression owlClassExpression = parseLabelClassExpression(labelClassExpression);
		return getSuperClasses(owlClassExpression, direct);
	}


	/**
	 * Retrieves the named equivalent classes corresponding to the class expression.
	 * The method is synchronized in order to avoid concurrency problems 
	 * that could arise in threaded
	 * environment (such as a web server with a shared Brain instance).
	 * @return equivalentClasses
	 * @throws ClassExpressionException 
	 * @throws ClassExpressionException 
	 */
	public synchronized List<String> getEquivalentClasses(String classExpression) throws ClassExpressionException {
		OWLClassExpression owlClassExpression = parseClassExpression(classExpression);
		return getEquivalentClasses(owlClassExpression);
	}


	/**
	 * Returns the list of equivalent classes from the OWL expression.
	 * should be returned.
	 * @param owlClassExpression
	 * @return equivalentClasses
	 */
	private List<String> getEquivalentClasses(OWLClassExpression owlClassExpression) {
		Set<OWLClass> equivalentClasses = null;
		//Can be simplified once Elk would have implemented a better way to deal with anonymous classes
		if(owlClassExpression.isAnonymous()){
			OWLClass anonymousClass = getTemporaryAnonymousClass(owlClassExpression);
			this.classify();
			equivalentClasses = this.reasoner.getEquivalentClasses(anonymousClass).getEntitiesMinus(anonymousClass);
			removeTemporaryAnonymousClass(anonymousClass);
		}else{
			if(!this.isClassified){
				this.classify();
			}
			equivalentClasses = this.reasoner.getEquivalentClasses(owlClassExpression).getEntitiesMinus((OWLClass) owlClassExpression);
		}
		return sortClasses(equivalentClasses);
	}

	/**
	 * Retrieves the named equivalent classes corresponding to the label class expression.
	 * The method is synchronized in order to avoid concurrency problems 
	 * that could arise in threaded
	 * environment (such as a web server with a shared Brain instance).
	 * @return equivalentClasses
	 * @throws ClassExpressionException 
	 * @throws ClassExpressionException 
	 */
	public synchronized List<String> getEquivalentClassesFromLabel(String labelClassExpression) throws ClassExpressionException {
		OWLClassExpression owlClassExpression = parseLabelClassExpression(labelClassExpression);
		return getEquivalentClasses(owlClassExpression);
	}

	/**
	 * Removes any class. Should be used to remove temporary classes used for expressions only at the moment.
	 * @param anonymousClass
	 */
	private void removeTemporaryAnonymousClass(OWLClass anonymousClass) {
		OWLEntityRemover remover = new OWLEntityRemover(this.manager, Collections.singleton(this.ontology));
		anonymousClass.accept(remover);
		this.manager.applyChanges(remover.getChanges());
		remover.reset();
		update();
	}

	/**
	 * Add a temporary class to the ontology based on the expression. Helpful because Elk doesn't support
	 * anonymous queries at the moment. To be removed in the future.
	 * @param classExpression
	 */
	private OWLClass getTemporaryAnonymousClass(OWLClassExpression classExpression) {
		IRI anonymousIri = IRI.create("temp");
		int counter = 0;
		while (this.ontology.containsClassInSignature(anonymousIri)) {
			anonymousIri = IRI.create("temp" + counter);
			counter++;
		}
		OWLClass anonymousClass = this.factory.getOWLClass(anonymousIri);
		OWLEquivalentClassesAxiom equivalenceAxiom = this.factory.getOWLEquivalentClassesAxiom(anonymousClass, classExpression);
		addAxiom(equivalenceAxiom);
		this.reasoner.flush();
		return anonymousClass;
	}

	/**
	 * Sort the classes based on their short forms.
	 * @param classes
	 * @return listClasses
	 */
	private List<String> sortClasses(Set<OWLClass> classes) {
		List<String> listClasses = new ArrayList<String>();
		for (OWLClass owlClass : classes) {
			if(!owlClass.isOWLNothing()){
				listClasses.add(this.bidiShortFormProvider.getShortForm(owlClass));
			}
		}
		Collections.sort(listClasses);
		return listClasses;
	}


	/**
	 * Test whether an OWLClass is the subclass of an other.
	 * @param presumedSubClass
	 * @param presumedSuperClass
	 * @param direct
	 * @return isASubClass
	 * @throws ClassExpressionException 
	 */
	public boolean isSubClass(String presumedSubClass, String presumedSuperClass, boolean direct) throws ClassExpressionException {
		List<String> subClasses = this.getSubClasses(presumedSuperClass, direct);
		boolean contained = subClasses.contains(presumedSubClass);
		return contained;
	}

	/**
	 * Test whether an OWLClass is the super class of an other.
	 * @param presumedSuperClass
	 * @param presumedSubClass
	 * @param direct
	 * @return isASuperClass
	 * @throws ClassExpressionException 
	 */
	public boolean isSuperClass(String presumedSuperClass, String presumedSubClass, boolean direct) throws ClassExpressionException {
		List<String> superClasses = this.getSubClasses(presumedSuperClass, direct);
		boolean contained = superClasses.contains(presumedSubClass);
		return contained;
	}

	/**
	 * Returns the unsatisfiable classes (aka no possible instances).
	 * @return unsatisfiableClasses
	 */
	public List<String> getUnsatisfiableClasses() {
		if(!this.isClassified){
			this.classify();
		}
		Set<OWLClass> unsatisfiableClasses = this.reasoner.getUnsatisfiableClasses().getEntitiesMinusBottom();
		return sortClasses(unsatisfiableClasses);
	}

	/**
	 * Free the resources used by the reasoner.
	 * Once this method is called, the reasoner is destroyed and replaced
	 * by a fresh one. Re-classification is needed before re-querying.
	 */
	public void sleep() {
		this.reasoner.flush();
		this.reasoner.dispose();
		if(this.configuration == null){
			this.reasoner = this.getReasonerFactory().createReasoner(this.ontology);
		}else{
			this.reasoner = this.getReasonerFactory().createReasoner(this.ontology, this.configuration);
		}
		this.isClassified = false;
	}

	/**
	 * Checks if the class (short form) is already inside the brain. Useful while
	 * parsing in order to avoid errors.
	 * @return whether the class is known or not.
	 */
	public boolean knowsClass(String owlClass) {
		try {
			this.getOWLClass(owlClass);
			return true;
		} catch (NonExistingClassException e) {
			return false;
		}
	}

	/**
	 * Checks if the object property (short form) is already inside the brain. Useful while
	 * parsing in order to avoid errors.
	 * @return whether the class is known or not.
	 */
	public boolean knowsObjectProperty(String owlObjectProperty) {
		try {
			this.getOWLObjectProperty(owlObjectProperty);
			return true;
		}catch(NonExistingObjectPropertyException e){
			return false;
		}
	}

	/**
	 * Checks if the data property is already inside the brain. Useful while
	 * parsing in order to avoid errors.
	 * @return whether the class is known or not.
	 */
	public boolean knowsDataProperty(String owlDataProperty) {
		try {
			this.getOWLDataProperty(owlDataProperty);
			return true;
		}catch(NonExistingDataPropertyException e){
			return false;
		}
	}

	/**
	 * Checks if the annotation property is already inside the brain. Useful while
	 * parsing in order to avoid errors.
	 * @return whether the class is known or not.
	 */
	public boolean knowsAnnotationProperty(String owlAnnotationProperty) {
		try {
			this.getOWLAnnotationProperty(owlAnnotationProperty);
			return true;
		}catch(NonExistingAnnotationPropertyException e){
			return false;
		}
	}

	/**
	 * Calculates the Jaccard index between two classes of the ontology.
	 * The super classes of each classes are compared...
	 * http://en.wikipedia.org/wiki/Jaccard_index
	 * http://webwhompers.com/set-theory.html
	 * @return The Jaccard index
	 * @throws ClassExpressionException 
	 */
	//TODO finish the doc
	public float getJaccardSimilarityIndex(String class1, String class2) throws ClassExpressionException {
		float intersection = 0;
		List<String> superClasses1 = this.getSuperClasses(class1, false);
		superClasses1.add(class1);
		List<String> superClasses2 = this.getSuperClasses(class2, false);
		superClasses2.add(class2);

		float sizeSet1 = 0;
		float sizeSet2 = superClasses2.size();
		for (String superClass1 : superClasses1) {
			if(superClasses2.contains(superClass1)){
				intersection++;
				sizeSet2--;
			}else{
				sizeSet1++;
			}
		}

		float union = intersection + sizeSet2 + sizeSet1;
		float index = intersection/union;
		return index;
	}

	//TODO doc
	//Groupwise similarity coefficient
	public float getJaccardSimilarityIndex(List<String> classes1, List<String> classes2) throws ClassExpressionException {
		List<String> allClasses1 = new ArrayList<String>();
		List<String> allClasses2 = new ArrayList<String>();

		for (String class1 : classes1) {
			List<String> superClasses1 = this.getSuperClasses(class1, false);
			superClasses1.add(class1);
			for (String superClass1 : superClasses1) {
				if(!allClasses1.contains(superClass1)){
					allClasses1.add(superClass1);
				}
			}
		}

		for (String class2 : classes2) {
			List<String> superClasses2 = this.getSuperClasses(class2, false);
			superClasses2.add(class2);
			for (String superClass2 : superClasses2) {
				if(!allClasses2.contains(superClass2)){
					allClasses2.add(superClass2);
				}
			}
		}

		float intersection = 0;
		float sizeSet1 = 0;
		float sizeSet2 = allClasses2.size();
		for (String superClass1 : allClasses1) {
			if(allClasses2.contains(superClass1)){
				intersection++;
				sizeSet2--;
			}else{
				sizeSet1++;
			}
		}

		float union = intersection + sizeSet2 + sizeSet1;
		float index = intersection/union;
		return index;
	}

}
