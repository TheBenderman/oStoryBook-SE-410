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
		String[] prefixes = {"dr.", "dr", "doctor", "mr.", "mr", "mister", "ms.", "ms", "miss", "mrs.","mrs", "mistress", 
				"hn.", "hn", "honorable", "the", "honorable", "his", "her", "honor", "fr", "fr.",
			     "frau", "hr", "herr", "rv.", "rv", "rev.", "rev", "reverend", "reverend", "madam", "lord", "lady",
			     "sir", "senior", "bishop", "rabbi", "holiness", "rebbe", "deacon", "eminence", "majesty", "consul",
			     "vice", "president", "ambassador", "secretary", "undersecretary", "deputy", "inspector", "ins.",
			     "detective", "det", "det.", "constable", "private", "pvt.", "pvt", "petty", "p.o.", "po", "first",
			     "class", "p.f.c.", "pfc", "lcp.", "lcp", "corporal", "cpl.", "cpl", "colonel", "col", "col.",
			     "capitain", "cpt.", "cpt", "ensign", "ens.", "ens", "lieutenant", "lt.", "lt", "ltc.", "ltc",
			     "commander", "cmd.", "cmd", "cmdr", "rear", "radm", "r.adm.", "admiral", "adm.", "adm", "commodore",
			     "cmd.", "cmd", "general", "gen", "gen.", "ltgen", "lt.gen.", "maj.gen.", "majgen.", "major", "maj.",
			     "mjr", "maj", "seargent", "sgt.", "sgt", "chief", "cf.", "cf", "petty", "officer", "c.p.o.", "cpo",
			     "master", "cmcpo", "fltmc", "formc", "mcpo", "mcpocg", "command", "fleet", "force", "miss."};
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
		int num = 0;
		String[] aline = lines.split(System.getProperty("line.separator")); // separate the string by lines
		ArrayList<String> people = new ArrayList<String>(); // arraylist of people names
		// Make sure this file is in your eclipse root folder
		AbstractSequenceClassifier<CoreLabel> classifier;
		for(String l: aline){ // read each line of file
			try {
				classifier = CRFClassifier.getClassifier("english.all.3class.distsim.crf.ser.gz");
				List<Triple<String, Integer, Integer>> list = classifier.classifyToCharacterOffsets(l);
				for(Triple<String, Integer, Integer> item : list){
					if (item.first().equals("PERSON")){ // look at tag for person
						for(String pre: prefixes){ // iterate through common prefixes
		          			if (l.toLowerCase().indexOf(pre.toLowerCase()) != -1 ) { // find index of prefix in sentence
		          				num = l.toLowerCase().indexOf(pre.toLowerCase()); // prefix found save index
		          				people.add(l.substring(num, trip.third())); // add name to array with prefix at new index
		          				break; // break for loop
		          				} 
		          			} // exit prefix for loop
		          		if(num==0){ // check if prefix not found 
		          			people.add(l.substring(trip.second, trip.third())); // add name with no prefix
		          		}
					}
				}
			} 
			catch (ClassCastException | ClassNotFoundException | IOException e) {
				e.printStackTrace();
				System.out.println(e);
			}
		}
		for(int c=0; c<people.size(); c++){
			System.out.println(c+":"+people.get(c));
	     }
	}
}