package storybook.importer;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.apache.commons.io.FileUtils;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;
import edu.stanford.nlp.util.Triple;

import java.util.ArrayList;
import java.util.List;

import storybook.ui.MainFrame;

public class CharactersFromTextFileImporter{
	MainFrame mainFrame;
	/**
	 * Creates new form DlgImport
	 * @param parent
	 * @param modal
	 */
		private person p; // person constructor object 
	
	public CharactersFromTextFileImporter(MainFrame m) {
		mainFrame = m;
	}
	
	public void importCharactersFromTextFile()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		int returnValue = fileChooser.showOpenDialog(mainFrame);
		if (returnValue != JFileChooser.APPROVE_OPTION)
			return;
		
		File textFile = fileChooser.getSelectedFile();
		String lines = "";
		
		try {
			lines = FileUtils.readFileToString(textFile);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<String> people = new ArrayList<String>();
		ArrayList<person> myperson = new ArrayList<person>(); // arraylist for names of people
		// Make sure this file is in your eclipse root folder
		AbstractSequenceClassifier<CoreLabel> classifier;
		try {
			classifier = CRFClassifier.getClassifier("english.all.3class.distsim.crf.ser.gz");
			
			List<Triple<String, Integer, Integer>> list = classifier.classifyToCharacterOffsets(lines);
			
			for (Triple<String, Integer, Integer> item : list) 
			{
				if (item.first().equals("PERSON"))
					people.add(lines.substring(item.second, item.third()));
			}
		} catch (ClassCastException | ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(String ppl: people){ // iterate list names of people
			p = new person(); // create new person object
			String First_name = ""; // set first name empty
			String Last_name = ""; // set last name empty
			String Abbreviation = ""; // set abbreviation empty
			Last_name =  ppl.substring(ppl.lastIndexOf(" ")+1); // remove first name and abbreviation from string
			p.set_lastName(Last_name); // set person object last name
			First_name = ppl.substring(0, ppl.lastIndexOf(' ')); // remove last name from string
			p.set_firstName(First_name); // set person object first name
			int x = ppl.indexOf(' '); // get index of first space
			Abbreviation = ppl.substring(0, x); // remove first and last name from string
			p.set_abrv(Abbreviation); // set person object abrreviation
			myperson.add(p); // add person object to arraylist
			}// end people loop
		}
	}
}
