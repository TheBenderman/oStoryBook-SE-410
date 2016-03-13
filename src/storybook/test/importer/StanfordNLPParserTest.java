package storybook.test.importer;

import java.util.List;
import org.junit.Test;
import junit.framework.TestCase;
import storybook.importer.StanfordNLPParser;



public class StanfordNLPParserTest extends TestCase {
	@Test
	public void testGetPeopleFromStanfordNLP(){
		StanfordNLPParser parser = new StanfordNLPParser();
		
		String testLine = "Whereby any if Mr. Harold Montgomery test string";
		List<String> people = parser.getPeopleFromStanfordNLP(testLine);
		assertEquals(people.size(),1);
	}
}
