package test;

import controller.AddressParser;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test for address parsing. This will probably fail a few, but it's nothing that's too important in the program.
 * Room for regex improvement!
 * @author Stahl
 *
 */
public class TestAddressParser {

	@Test
	public void test() {

		{
			String[] parsed = AddressParser.parseAddress("Rued Langgaards Vej 7A, 2300 KBH S");
			assertEquals(true,parsed[2].equals("2300"));
			System.out.println(parsed[1]);
			assertEquals(true,parsed[1].equals("7A"));
			assertEquals(true,parsed[0].equals("Rued Langgaards Vej"));
			
			assertEquals(false,parsed[2].equals("2300 S"));
			assertEquals(false,parsed[1].equals("7A,"));
			assertEquals(false,parsed[0].equals("RuedLanggaardsVej"));
			
			String[] parsedA = AddressParser.parseAddress("Tagensvej 9999");
			assertEquals(true,parsedA[2].equals("9999"));
			assertEquals(true,parsedA[1].equals(""));
			System.out.println(parsedA[0].equals("Tagensvej"));
			assertEquals(true,parsedA[0].equals("Tagensvej"));
			
			assertEquals(false,parsedA[2].equals("99999"));
			assertEquals(false,parsedA[1].equals("??"));
			assertEquals(false,parsedA[0].equals("Tagensvej "));
			
			String[] parsedB = AddressParser.parseAddress("Tagensvej");
			assertEquals(parsedB[2].equals(""),true);
			assertEquals(parsedB[1].equals(""),true);
			assertEquals(parsedB[0].equals("Tagensvej"),true);
			
			assertEquals(parsedB[2].equals("-"),false);
			assertEquals(parsedB[1].equals("  "),false);
			assertEquals(parsedB[0].equals("Tagensvej "),false);
			
			String[] parsedC = AddressParser.parseAddress("##Tagensvej #9999 #1%&%#");
			assertEquals(parsedC[2].equals("9999"),true);
			assertEquals(parsedC[1].equals(""),true);
			assertEquals(parsedC[0].equals("Tagensvej"),true);
			
			
			
			
		}
	}

}
