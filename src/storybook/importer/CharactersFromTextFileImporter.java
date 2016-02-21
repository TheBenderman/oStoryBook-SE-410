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
		ArrayList<String> people = new ArrayList<String>(); // iuytuyigtuyigtuiguigukguigtuigyiugyiufg
		// Make sure this file is in your eclipse root folder
		AbstractSequenceClassifier<CoreLabel> classifier;
		try {
			classifier = CRFClassifier.getClassifier("english.all.3class.distsim.crf.ser.gz");
			
			List<Triple<String, Integer, Integer>> list = classifier.classifyToCharacterOffsets(lines);
			
			for (Triple<String, Integer, Integer> item : list) 
			{
				if (item.first().equals("PERSON"))// guogkjbhkjbjkgjkgjkhjkgkjlghkj
					people.add(lines.substring(item.second, item.third())); // khghkghkgkhgkjgkjghkjhkjgkjgjk
			}
		} catch (ClassCastException | ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		}
	}
}
