package storybook.importer;

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import storybook.model.BookModel;
import storybook.model.hbn.dao.GenderDAOImpl;
import storybook.model.hbn.entity.Gender;
import storybook.model.hbn.entity.Person;
import storybook.ui.MainFrame;

public class StanfordNLParser{
	
	private MainFrame mainFrame;
	private List<String> femaleTitles = Arrays.asList("Miss", "Ms.", "Mrs.", "Misses", "Aunt", "Grandma", "Grandmother");
	private List<String> maleTitles = Arrays.asList("Mr.", "Mister", "Uncle", "Grandpa", "Grandfather");
	private List<String> genderNeutralTitles = Arrays.asList("Professor", "Dr.", "Doctor");
	private List<Person> aperson;
	
	public StanfordNLParser(MainFrame m) 
	{
		mainFrame = m;
		aperson = new ArrayList<Person>();
	}
	
	public void processPeopleFromNLPOutput(List<Triple<String, Integer, Integer>> list, String lines)
	{
		// for each of the classifications, find all the people, and parse them
		for (Triple<String, Integer, Integer> trip : list) 
		{
			// the string in the triple contains the type of object that was classified
			// the stanford nlp processor classifies tags, locations, people.. just to name a few
			// we only want to collect people
          	if(trip.first().equals("PERSON"))
          	{
          		Person p = createPersonObject(lines, trip); // create the person object from the text file and stanford nlp
          		
          		if (p != null)
      				aperson.add(p);
          	}
      	}
	}
	
	public Person createPersonObject(String textLines, Triple<String, Integer, Integer> nlpPerson)
	{
		Person p = new Person();
      	
		// get the person's name from the text file. the 2nd parameter of the triple contains the index of the first element
		// of the name, and the 3rd parameter of the triple contains the index of the last element of the name
      	String name = textLines.substring(nlpPerson.second, nlpPerson.third());
      	name = cleanUpString(name); //remove the excess whitespace
      		
      	String[] firstAndLastNames = name.split(" "); // split first and last name
      	
      	// if the name is only a length of one, then maybe the previous word in the text file is a title
      	// fetch the title.
      	if (firstAndLastNames.length == 1)
      	{
      		String title = getPersonTitle(textLines, firstAndLastNames, nlpPerson);
      		
      		// check to see if the 
      		if (femaleTitles.contains(title) || maleTitles.contains(title) || genderNeutralTitles.contains(title))
      		{
      			p.setFirstname(title);
      			p.setLastname(firstAndLastNames[0]);
      		}
      		
      		// If we found no title, and we have not set a first/last name, then the person has a single first name.
      		if (title.equals("") || (p.getFirstname().isEmpty() && p.getLastname().isEmpty()))
      			p.setFirstname(firstAndLastNames[0]);
      	}
      	else
      	{
      		p.setFirstname(firstAndLastNames[0]); // set the first name to the first word
      		p.setLastname(firstAndLastNames[firstAndLastNames.length - 1]); // set the last name to the last word
      	}	
      	
      	if (personAlreadyExists(p)) // if the person already exists in our collection, we don't want to do anymore
      		return null;
			
		p.setAbbreviation(getPersonAbbreviation(p, firstAndLastNames));
		setCharacterGender(p);
		
		return p;
	}
	
	public boolean personAlreadyExists(Person p)
	{	
  		// Make sure we didn't already add the person to the array list
  		for (Person person : aperson)
  		{
  			if (p.getFirstname().toLowerCase().equals(person.getFirstname().toLowerCase())
  					&& p.getLastname().toLowerCase().equals(person.getLastname().toLowerCase()) )
  				return true;
  		}
  		
  		return false;
	}
	
	public String getPersonAbbreviation(Person p, String[] firstAndLastNames)
	{
		// Set person name abbreviation
      	StringBuffer abrv = new StringBuffer(p.getFirstname().substring(0, 2));
		if (firstAndLastNames.length > 1)
			abrv.append(p.getLastname().substring(0, 2));
		
		return abrv.toString();
	}
	
	public String getPersonTitle(String textLines, String[] firstAndLastNames, Triple<String, Integer, Integer> nlpPerson)
	{
		int space1 = textLines.lastIndexOf(" ", nlpPerson.second); // get the space right before first index of the name
      	
  		if (space1 != -1)
      	{
  			int space2 = textLines.lastIndexOf(" ", space1-1); // get the 2nd space right before first index of the name
      		
  			if (space2 != -1)
  			{
	      		String title = textLines.substring(space2 + 1, space1); // get the title
	      		
	      		return title;
  			}
      	}
  		
  		return "";
	}
	
	private String cleanUpString(String str)
	{
		return str.replace("\n", " ").replace("\r", " ").replaceAll("\\s+", " ").trim();
	}
}