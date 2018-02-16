package controller;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressParser {

	/**
	 * Address parser using regular expressions. 
	 * @param s
	 * @return String[4] with the different attributes of the address
	 * [0] = Road name
	 * [1] = House number and letter if included
	 * [2] = Postal code
	 * [3] = City(not implemented right now)
	 */
	public static String[] parseAddress(String s)
	{
		String[] parsed = new String[4];
		for(int i = 0; i<parsed.length;i++)
		{
			parsed[i] = "";
		}
		Pattern ar = Pattern.compile("\\b[a-zA-ZøæåÆØÅ]*\\b\\s*[a-zA-ZøæåÆØÅ]*\\s*[a-zA-ZøæåÆØÅ]*"); 		
		// Address name regex
		Matcher addressmatch = ar.matcher(s);
		if(addressmatch.find()) parsed[0] = addressmatch.group().trim();

		Pattern housenumber = Pattern.compile("[0-9]{1,3}[a-zA-Z]?(\b|,)");
		// Postal code regex
		
		Matcher hnmatch = housenumber.matcher(s);
		if(hnmatch.find()) { 
			parsed[1] = hnmatch.group();
			if(parsed[1].contains(",")) { parsed[1] = parsed[1].substring(0,parsed[1].length()-1); }
		}

		Pattern postal  = Pattern.compile("[0-9]{4}");
		Matcher m = postal.matcher(s);
		if(m.find()) parsed[2] = m.group();
		
		return parsed;
	}

}
