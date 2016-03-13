package storybook.test.importer;

import org.junit.Test;
import junit.framework.TestCase;
import storybook.importer.CharactersFromTextFileImporter;
import storybook.model.hbn.entity.Person;
import storybook.ui.MainFrame;


public class CharactersFromTextFileImporterTest extends TestCase{
	
	protected CharactersFromTextFileImporter tester;
		
	protected void setUp(){
		tester = new CharactersFromTextFileImporter(new MainFrame());
	}

	@Test
	public void testPersonAlreadyExists(){
		Person testPerson = new Person();
		testPerson.setFirstname("John");
		testPerson.setLastname("Smith");
		
		assertFalse( tester.personAlreadyExists(testPerson) );
	}
	
	@Test
	public void testGetPersonAbbreviation(){
		Person testPerson = new Person();
		testPerson.setFirstname("John");
		testPerson.setLastname("Smith");
		String[] firstLast = {"John", "Smith"};
		
		assertEquals(tester.getPersonAbbreviation(testPerson, firstLast),"JoSm");
	}

	
	@Test
	public void testSetCharacterGenderFirstname(){
		Person testPerson = new Person();
		testPerson.setFirstname("John");
		testPerson.setLastname("Smith");
		
		tester.setCharacterGender(testPerson);
		assertEquals(testPerson.getGender().toString(),"male");
	}
	
	@Test
	public void testSetCharacterGenderTitle(){
		Person testPerson = new Person();
		testPerson.setFirstname("Mr");
		testPerson.setLastname("Smith");
		
		tester.setCharacterGender(testPerson);
		assertEquals(testPerson.getGender().toString(),"male");
	}
	
	@Test
	public void testCreatePersonObject(){
		String testLine = "Whereby any if Mr. Harold Montgomery test string";
		String testName = "Harold Montgomery";
		
		Person testResult = tester.createPersonObject(testLine, testName);
		
		assertEquals(testResult.getFirstname(), "Harold");
		assertEquals(testResult.getLastname(), "Montgomery");
		assertEquals(testResult.getGender().toString(), "male");
	}
	
	@Test
	public void testGetPersonTitle(){
		String testLine = "Whereby any if Mr. Harold Montgomery test string";
		String[] testName = {"Harold","Montgomery"};
		
		assertEquals(tester.getPersonTitle(testLine, testName), "Mr.");
	}
}
