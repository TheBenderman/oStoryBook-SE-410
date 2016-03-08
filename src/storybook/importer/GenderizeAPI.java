package storybook.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.*;

import storybook.model.hbn.entity.Person;

public class GenderizeAPI {

	public GenderizeAPI()
	{
		
	}
	
	// get the person's gender for a specific person
	public String getPersonGenderString(Person p)
	{
		JSONObject json = getGenderizeJSONResponseFromURL(p.getFirstname()); // call the genderize api with the person's first name
		try
		{
			String genderString = json.getString("gender"); // get the gender attribute from the returned json string
			
			if (genderString != null) // if the string is null, then the name cant be found in their database
				return genderString;
		}
		catch (JSONException e)
		{
			return "";
		}
		
		return "";
	}
	
	// Call the genderize api through an HTTP request and get the json response
	private JSONObject getGenderizeJSONResponseFromURL(String name)
	{
		try 
		{
			// call the genderize api with the person's name
			URL genderizeURL = new URL("https://api.genderize.io/?name=" + name);
			
			InputStream is = genderizeURL.openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAllCharacters(rd); // read the complete response
			JSONObject json = new JSONObject(jsonText); // create a json object from the response text
			
			is.close();
			
			return json;
		} 
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	// read all of the characters from the reader
	private String readAllCharacters(Reader rd) throws IOException 
	{
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) 
	    {
	      sb.append((char) cp);
	    }
	    
	    return sb.toString();
	}
	
}
