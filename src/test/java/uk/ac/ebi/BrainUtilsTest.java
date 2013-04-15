package uk.ac.ebi;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.ebi.brain.core.Brain;
import uk.ac.ebi.brain.error.BrainException;

public class BrainUtilsTest {

	@Test
	public void jaccardSimilarityTest() throws BrainException {
		Brain brain = new Brain();
		brain.learn("src/test/resources/cars.owl");
		float index = brain.getJaccardSimilarityIndex("Car", "MiniVan");
		assertEquals(0.5, index, 1e-15);
		
		float index1 = brain.getJaccardSimilarityIndex("Sedan", "Car");
		assertEquals(0.800, index1, 1e-3);
		
		float index2 = brain.getJaccardSimilarityIndex("Thing", "Thing");
		assertEquals(1.000, index2, 1e-3);
		
		brain.sleep();
	}
	
}
