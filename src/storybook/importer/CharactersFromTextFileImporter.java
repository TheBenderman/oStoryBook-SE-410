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
	private List<String> femaleTitles = Arrays.asList("Miss", "Ms.", "Mrs.", "Misses", "Aunt", "Grandma", "Grandmother");
	private List<String> maleTitles = Arrays.asList("Mr.", "Mister", "Uncle", "Grandpa", "Grandfather");
	private List<String> genderNeutralTitles = Arrays.asList("Professor", "Dr.", "Doctor");
	private List<Person> aperson;
	
	public void addCharactersToModel()
	{
		BookModel bookModel = mainFrame.getBookModel();
		bookModel.beginTransaction();
		
		for (Person person : aperson)
			bookModel.setNewPerson(person);
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
	
	public void setCharacterGenderBasedOnFirstName(Person p)
	{
		GenderizeAPI genderizeApi = new GenderizeAPI();
		String genderString = genderizeApi.getPersonGenderString(p);
		
		if(genderString.equals("male"))
			p.setGender(getMaleGender());
		else if (genderString.equals("female"))
			p.setGender(getFemaleGender());

	}
	
	private Gender getMaleGender()
	{
		BookModel bookModel = mainFrame.getBookModel();
		Session session = bookModel.beginTransaction();
		
		GenderDAOImpl genderDaoImpl = new GenderDAOImpl(session);
		
		return genderDaoImpl.findMale();
	}
	
	private Gender getFemaleGender()
	{
		BookModel bookModel = mainFrame.getBookModel();
		Session session = bookModel.beginTransaction();
		
		GenderDAOImpl genderDaoImpl = new GenderDAOImpl(session);
		
		return genderDaoImpl.findFemale();
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
		
		// Make sure this file is in your eclipse root folder
		AbstractSequenceClassifier<CoreLabel> classifier;
		try 
		{
			// this file contains the data that the stanford NLP text processor needs to find names
			classifier = CRFClassifier.getClassifier("english.all.3class.distsim.crf.ser.gz");
			
			// run the stanford nlp text processor on the text file
			List<Triple<String, Integer, Integer>> list = classifier.classifyToCharacterOffsets(lines);
			
			processPeopleFromNLPOutput(list, lines);
			addCharactersToModel(); // add the list of characters to the UI
		} 
		catch (ClassCastException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
}