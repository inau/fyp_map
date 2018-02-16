package test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashMap;

import model.trie.PostalMap;


import org.junit.Test;


public class TestPostalMap {

	@Test
	public void test() {
		
		PostalMap tm = new PostalMap(new File("dat/postalinfo.txt"));
	
		HashMap<String,String> pm = tm.getPostalMap();
		

		assertEquals(true,pm.get("2200").equals("København N"));

		assertEquals(true,pm.get("4000").equals("Roskilde"));
		assertEquals(true,pm.get("2500").equals("Valby"));
		assertEquals(true,pm.get("2620").equals("Albertslund"));
		

		assertEquals(null,pm.get("45345"));

	
		assertEquals(null,pm.get("123456"));
		assertEquals(null,pm.get("1"));
		assertEquals(null,pm.get("00011222"));
		

		assertEquals(false,pm.get("4000").equals("København N"));
		assertEquals(false,pm.get("2200").equals("Roskilde"));
		assertEquals(false,pm.get("2620").equals("Valby"));
		assertEquals(false,pm.get("2500").equals("Måløv"));

		
	}

}
