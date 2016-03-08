package storybook.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;

public class StanfordNLPParser {
	private AbstractSequenceClassifier<CoreLabel> classifier;
	
	public StanfordNLPParser()
	{
		// Make sure this file is in your eclipse root folder
		// this file contains the data that the Stanford NLP text processor needs to find names
		try
		{
			classifier = CRFClassifier.getClassifier("english.all.3class.distsim.crf.ser.gz");
		}
		catch (ClassCastException | ClassNotFoundException | IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	// get all of the people from a string containing text
	public List<String> getPeopleFromStanfordNLP(String lines)
	{
		List<Triple<String,Integer,Integer>> objects = classifier.classifyToCharacterOffsets(lines);
		List<String> names = new ArrayList<String>();
		
		// for each of the classifications, find all the people, and parse them
		for (Triple<String, Integer, Integer> trip : objects) 
		{
			// the string in the triple contains the type of object that was classified
			// the stanford nlp processor classifies tags, locations, people.. just to name a few
			// we only want to collect people
          	if(trip.first().equals("PERSON"))
          		names.add(lines.substring(trip.second, trip.third()));
		}
		
		return names;
	}
}
