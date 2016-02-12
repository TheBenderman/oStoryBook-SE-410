package storybook.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.apache.commons.io.FileUtils;

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
	}
}
