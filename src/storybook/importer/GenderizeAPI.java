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
	
	public String getPersonGenderString(Person p)
	{
		JSONObject json = getGenderizeJSONResponseFromURL(p.getFirstname());
		try
		{
			String genderString = json.getString("gender");
			
			if (genderString != null)
				return genderString;
		}
		catch (JSONException e)
		{
			return "";
		}
		
		return "";
	}
	
	private JSONObject getGenderizeJSONResponseFromURL(String queryString)
	{
		try 
		{
			URL genderizeURL = new URL("https://api.genderize.io/?name=" + queryString);
			
			InputStream is = genderizeURL.openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAllCharacters(rd);
			JSONObject json = new JSONObject(jsonText);
			
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
