package model.trie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;


/**
 * Class that builds a map that travels from postal code to postal code name.
 * Use getPostalMap() to return the HashMap from the object.
 * @author Stahl
 *
 */
public class PostalMap {

	private HashMap<String,String> postalMap;

	/**
	 * The constructor builds the postal map. 
	 * @param location The file where the postal map should be.
	 */
	public PostalMap(File location)
	{
		// Looking for file
		if(location == null)	throw new NullPointerException("parameter location was null");
		
		HashMap<String,String> map = new HashMap();
		BufferedReader in = null;
		try {
			in = new BufferedReader((new InputStreamReader(new FileInputStream(location), "UTF-8")));
			String s = in.readLine();
			while(s != null)
			{
				map.put(s.substring(0,4).trim(),s.substring(5,s.length()).trim());
				s = in.readLine();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Postal map info not found! " + e);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		postalMap = map;
		
		for(int i = 1000; i <= 1499; i++)
		{
			postalMap.put(""+i, "K�benhavn K");
		}
		for(int i = 1500; i <= 1799; i++)
		{
			postalMap.put(""+i, "K�benhavn V");
		}
		for(int i = 1800; i <= 1999; i++)
		{
			postalMap.put(""+i, "Frederiksberg C"); 	
		}
	}

	public HashMap<String,String> getPostalMap()
	{
		return postalMap;
	}

}


