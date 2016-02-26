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

public class CharactersFromTextFileImporter{
	
	private MainFrame mainFrame;
	
	private List<String> femaleTitles = Arrays.asList("Miss", "Ms.", "Mrs.", "Misses");
	private List<String> maleTitles = Arrays.asList("Mr.", "Mister");
	private List<String> genderNeutralTitles = Arrays.asList("Professor", "Dr.", "Doctor");
	
	public CharactersFromTextFileImporter(MainFrame m) {
		mainFrame = m;
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
			System.out.println(e);
		}
		
		ArrayList<Person> aperson = new ArrayList<Person>(); // arraylist of person objects
		
		// Make sure this file is in your eclipse root folder
		AbstractSequenceClassifier<CoreLabel> classifier;
		try 
		{
			// this file contains the data that the stanford NLP text processor needs to find names
			classifier = CRFClassifier.getClassifier("english.all.3class.distsim.crf.ser.gz");
			
			// run the stanford nlp text processor on the text file
			List<Triple<String, Integer, Integer>> list = classifier.classifyToCharacterOffsets(lines);
			
			// for each of the classifications, find all the people, and parse them
			for (Triple<String, Integer, Integer> trip : list) 
			{
				// the string in the triple contains the type of object that was classified
				// the stanford nlp processor classifies tags, locations, people.. just to name a few
				// we only want to collect people
	          	if(trip.first().equals("PERSON"))
	          	{
	          		Person p = createPersonObject(lines, trip); // create the person object from the text file and stanford nlp
	          		
	          		boolean foundPerson = false;
	          		
	          		// Make sure we didn't already add the person to the array list
	          		for (Person person : aperson)
	          		{
	          			if (p.getFirstname().toLowerCase().equals(person.getFirstname().toLowerCase())
	          					&& p.getLastname().toLowerCase().equals(person.getLastname().toLowerCase()) )
	          				foundPerson = true;
	          		}
	          		
	          		if (!foundPerson)
          				aperson.add(p);
	          	}
          	}
			setGenders(aperson); // call Gender API set gender for list of people
			addCharactersToModel(aperson); // add the list of characters to the UI
		} 
		catch (ClassCastException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
	
	public File getTextFileFromUser()
	{
		JFileChooser fileChooser = new JFileChooser(); // create a new file chooser
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // only allow the user to select files
		
		int returnValue = fileChooser.showOpenDialog(mainFrame);
		
		if (returnValue != JFileChooser.APPROVE_OPTION) // if the user clicks "cancel" or exits the dialog, dont return anything
			return null;
		
		return fileChooser.getSelectedFile(); // return the selected file
	}
	
	public Person createPersonObject(String textLines, Triple<String, Integer, Integer> nlpPerson)
	{
		Person p = new Person();
      	
		// get the person's name from the text file. the 2nd parameter of the triple contains the index of the first element
		// of the name, and the 3rd parameter of the triple contains the index of the last element of the name
      	String name = textLines.substring(nlpPerson.second, nlpPerson.third());
      	name = name.replace("\n", " ").replace("\r", " ").replaceAll("\\s+", " ").trim(); //remove the excess whitespace
      		
      	String[] names = name.split(" "); // split first and last name
      	
      	// if the name is only a length of one, then maybe the previous word in the text file is a title
      	// fetch the title.
      	if (names.length == 1)
      	{
      		int space1 = textLines.lastIndexOf(" ", nlpPerson.second); // get the space right before first index of the name
	      	
      		if (space1 != -1)
	      	{
      			int space2 = textLines.lastIndexOf(" ", space1-1); // get the 2nd space right before first index of the name
	      		
      			if (space2 != -1)
      			{
		      		String title = textLines.substring(space2 + 1, space1); // get the title
		      		
		      		// check to see if the 
		      		if (femaleTitles.contains(title) || maleTitles.contains(title) || genderNeutralTitles.contains(title))
		      		{
		      			p.setFirstname(title);
		      			p.setLastname(names[0]);
		      		}
      			}
	      	}
      		
      		// if the first and last name are both empty, that means there was no title. So it must be a single first name
      		if (p.getFirstname().isEmpty() && p.getLastname().isEmpty())
      			p.setFirstname(names[0]);
      	}
      	else
      	{
      		p.setFirstname(names[0]); // set the first name to the first word
      		p.setLastname(names[names.length - 1]); // set the last name to the last word
      	}
      	
      	StringBuffer abrv = new StringBuffer(p.getFirstname().substring(0, 2));
		if (names.length > 1) {
			abrv.append(p.getLastname().substring(0, 2));
		}
			
		p.setAbbreviation(abrv.toString());
		
		setCharacterGender(p);
		
		return p;
	}
	
	// Set the gender of the character
	public void setCharacterGender(Person p)
	{
		BookModel bookModel = mainFrame.getBookModel();
		Session session = bookModel.beginTransaction();
		
		GenderDAOImpl genderDaoImpl = new GenderDAOImpl(session);
		Gender male = genderDaoImpl.findMale(); // get the default male gender
		Gender female = genderDaoImpl.findFemale(); // get the default female gender
		
		// First, check if the first name is a title
		// If it is a title, check if it is a gender specific title
		if (femaleTitles.contains(p.getFirstname()))
			p.setGender(female);
		else if (maleTitles.contains(p.getFirstname()))
			p.setGender(male);
	}
	
	public void addCharactersToModel(ArrayList<Person> people)
	{
		BookModel bookModel = mainFrame.getBookModel();
		bookModel.beginTransaction();
		
		for (Person person : people)
			bookModel.setNewPerson(person);
	}
	// set genders for a person in people
	public void setGenders(ArrayList<Person> people){
		List<NameGender> genders = null;
		try{
			genders = api.getGenders(people); 
		}
		catch(Exception e){
			System.out.println(e);
		}
		
		for(int k = 0; k < people.size(); k++){
			Person p = people.get(k); 
			if(genders != null){
				NameGender gender = genders.get(k);
					if(gender.isMale()){
						p.setGender(male);
					}
					else{
						p.setGender(female);
					}
			}
		}
		
	}
}