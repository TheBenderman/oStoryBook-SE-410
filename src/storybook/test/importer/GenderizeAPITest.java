package storybook.test.importer;

import junit.framework.TestCase;
import org.junit.Test;

import storybook.importer.GenderizeAPI;
import storybook.model.hbn.entity.Person;

public class GenderizeAPITest extends TestCase{
	// get the person's gender for a specific person
	@Test
	public void testGetPersonGenderString(){
		GenderizeAPI api = new GenderizeAPI();
		Person testperson = new Person();
		
		testperson.setFirstname("robert");
		assertEquals(api.getPersonGenderString(testperson),"male");
		
		testperson.setFirstname("karen");
		assertEquals(api.getPersonGenderString(testperson),"female");
	}
}
