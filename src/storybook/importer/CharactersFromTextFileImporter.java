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
		// arraylist for names of people
		ArrayList<String> people = new ArrayList<String>();
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
		int count = 0; // counter for fancy for loop
		CharSequence prefixes = "Mr.","Dr.","Ms.","Miss.","Mrs.","mr","dr","ms","miss","mrs","uncle","aunt","mom","dad","sister","brother","cousin"); // character sequences of common prefixes
		for(String name: people){ // iterates over peoples names
			if(name.contains(prefixes)){	 // check if persons' name contains a prefix
					String[] prename = name.split(" ",2); // split the prefix from the name
					people.get(count) = prename[1]; // save the name to the people's arraylist
				}
			count++; // increment counter + 1
			}
		//
		}
	}
}
