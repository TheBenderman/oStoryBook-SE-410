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
			lines = FileUtils.readFileToString(textFile); // store the entire textfile in string
		} 
		catch (IOException e) {
			e.printStackTrace();
			System.out.println(e);
		}
		ArrayList<String> people = new ArrayList<String>(); // arraylist of people names
		ArratList<Person> aperson = new ArrayList<Person>();
		// Make sure this file is in your eclipse root folder
		AbstractSequenceClassifier<CoreLabel> classifier;
			try {
				classifier = CRFClassifier.getClassifier("english.all.3class.distsim.crf.ser.gz");
				List<Triple<String, Integer, Integer>> list = classifier.classifyToCharacterOffsets(lines);
				  for (Triple<String, Integer, Integer> trip : list) {
			          	if(trip.first().equals("PERSON")){
			          		Person p = new Person();
			          		String name = lines.substring(trip.second, trip.third());
			          		name = name.replace("\n", " ").replace("\r", " ").replaceAll("\\s+", " ").trim();
			          		String[] names = name.split(" ");
			          		p.setFirstname(names[0]);
			          		if(names.length>1){
			          			p.setLastname(names[names.length -1 ]);
			          			}
			          		StringBuffer abbreviation = new StringBuffer(p.getFirstname().substring(0, 2));
							if (names.length > 1) {
								abbreviation.append(p.getLastname().substring(0,2));
							}
							p.setAbbreviation(abbreviation.toString());
			          		}
			          	}
				  aperson.add(p);
			} 
			catch (ClassCastException | ClassNotFoundException | IOException e) {
				e.printStackTrace();
				System.out.println(e);
			}
		for(int c = 0; c < people.size(); c++){
			System.out.println(c + ":" + people.get(c));
	     }
	}
}