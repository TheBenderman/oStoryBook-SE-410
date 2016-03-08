package storybook.importer;

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import storybook.model.BookModel;
import storybook.model.hbn.dao.GenderDAOImpl;
import storybook.model.hbn.entity.Gender;
import storybook.model.hbn.entity.Person;
import storybook.ui.MainFrame;

public class CharactersFromTextFileImporter{
	
	private MainFrame mainFrame;
	
	private List<String> femaleTitles = Arrays.asList("Miss", "Ms.", "Mrs.", "Misses", "Aunt", "Grandma", "Grandmother", "Madam");
	private List<String> maleTitles = Arrays.asList("Mr.", "Mister", "Uncle", "Grandpa", "Grandfather", "Sir");
	private List<String> genderNeutralTitles = Arrays.asList("Professor", "Prof", "Dr.", "Doctor");
	
	private List<Person> aperson;
	
	public CharactersFromTextFileImporter(MainFrame m) 
	{
		mainFrame = m;
		aperson = new ArrayList<Person>();
	}
	
	public void importCharactersFromTextFile()
	{
		File textFile = getTextFileFromUser(); // get the text file from a jdialog box
		
		if (textFile == null)
			return;
		
		String lines = "";
		try 
		{
			lines = FileUtils.readFileToString(textFile); // store the entire textfile in string
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
			
		StanfordNLPParser nlp = new StanfordNLPParser();
		processPeople(nlp.getPeopleFromStanfordNLP(lines), lines); // get the people from the stanfordnlp library, then process the people
		addCharactersToModel(); // add the list of characters to the UI
	}
	
	// get a file from user input and retrieve the file
	public File getTextFileFromUser()
	{
		JFileChooser fileChooser = new JFileChooser(); // create a new file chooser
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // only allow the user to select files
		
		int returnValue = fileChooser.showOpenDialog(mainFrame);
		
		if (returnValue != JFileChooser.APPROVE_OPTION) // if the user clicks "cancel" or exits the dialog, dont return anything
			return null;
		
		return fileChooser.getSelectedFile(); // return the selected file
	}
	
	// process all of the people retrieved from the stanfordnlp library
	public void processPeople(List<String> names, String lines)
	{
		for (String name : names)
		{
      		Person p = createPersonObject(lines, name); // create the person object from the text file and stanford nlp
      		
      		if (p != null) // if the person is null, then that means we have already added them. so skip the person
  				aperson.add(p);
      	}
	}
	
	// Create the person object from the person's name
	public Person createPersonObject(String textLines, String name)
	{
		Person p = new Person();
      	
		// get the person's name from the text file. the 2nd parameter of the triple contains the index of the first element
		// of the name, and the 3rd parameter of the triple contains the index of the last element of the name
      	name = cleanUpString(name); //remove the excess whitespace
      		
      	String[] firstAndLastNames = name.split(" "); // split first and last name
      	
      	// if the name is only a length of one, then maybe the previous word in the text file is a title
      	// fetch the title.
      	if (firstAndLastNames.length == 1)
      	{
      		String title = getPersonTitle(textLines, firstAndLastNames);
      		
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
      	else // if we have more than 1 name, then we have a first and last name
      	{
      		p.setFirstname(firstAndLastNames[0]); // set the first name to the first word
      		p.setLastname(firstAndLastNames[firstAndLastNames.length - 1]); // set the last name to the last word
      	}	
      	
      	if (personAlreadyExists(p)) // if the person already exists in our collection, we don't want to do anymore
      		return null;
			
		p.setAbbreviation(getPersonAbbreviation(p, firstAndLastNames)); // set the character's abbreviation from their name
		setCharacterGender(p); // set the character's gender
		
		return p;
	}
	
	// Determine if we have already added the person to our collection of people
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
	
	// get the person's abbreviation from their name
	public String getPersonAbbreviation(Person p, String[] firstAndLastNames)
	{
		// Set person name abbreviation
		// E.g: If their name is Harry Potter, their abbreviation is HaPo
      	StringBuffer abrv = new StringBuffer(p.getFirstname().substring(0, 2));
		if (firstAndLastNames.length > 1)
			abrv.append(p.getLastname().substring(0, 2));
		
		return abrv.toString();
	}
	
	// get the person's title from the text
	public String getPersonTitle(String textLines, String[] firstAndLastNames)
	{
		int space1 = textLines.lastIndexOf(" ", textLines.indexOf(firstAndLastNames[0])); // get the space right before first index of the name
      	
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
	
	// set the character gender, either based off the title or the person's first name
	public void setCharacterGender(Person p)
	{
		setCharacterGenderBasedOnTitle(p); // Try to set the character's gender based on title like Mr.
		
		// If we have not set the gender already, try to set the gender by the first name
		if (p.getGender() != getMaleGender() && p.getGender() != getFemaleGender())
			setCharacterGenderBasedOnFirstName(p);
	}
	
	// Set the gender of the character
	public void setCharacterGenderBasedOnTitle(Person p)
	{
		// First, check if the first name is a title
		// If it is a title, check if it is a gender specific title
		if (femaleTitles.contains(p.getFirstname()))
			p.setGender(getFemaleGender());
		else if (maleTitles.contains(p.getFirstname()))
			p.setGender(getMaleGender());
	}
	
	// Use the genderize api to set the gender based off the first name of the person
	public void setCharacterGenderBasedOnFirstName(Person p)
	{
		GenderizeAPI genderizeApi = new GenderizeAPI();
		String genderString = genderizeApi.getPersonGenderString(p);
		
		if(genderString.equals("male"))
			p.setGender(getMaleGender());
		else if (genderString.equals("female"))
			p.setGender(getFemaleGender());

	}
	
	// return the default male gender that is created by the book model
	private Gender getMaleGender()
	{
		BookModel bookModel = mainFrame.getBookModel();
		Session session = bookModel.beginTransaction();
		
		GenderDAOImpl genderDaoImpl = new GenderDAOImpl(session);
		
		return genderDaoImpl.findMale();
	}
	
	// return the default female gender that is created by the book model
	private Gender getFemaleGender()
	{
		BookModel bookModel = mainFrame.getBookModel();
		Session session = bookModel.beginTransaction();
		
		GenderDAOImpl genderDaoImpl = new GenderDAOImpl(session);
		
		return genderDaoImpl.findFemale();
	}
	
	// Add all of the characters to the model, so the characters now show up in the GUI under characters
	public void addCharactersToModel()
	{
		BookModel bookModel = mainFrame.getBookModel();
		bookModel.beginTransaction();
		
		for (Person person : aperson)
			bookModel.setNewPerson(person);
	}
	
	// Remove whitespace and escape characters from a string
	private String cleanUpString(String str)
	{
		return str.replace("\n", " ").replace("\r", " ").replaceAll("\\s+", " ").trim();
	}
}