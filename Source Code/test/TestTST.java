package test;

import model.trie.TST;
import static org.junit.Assert.*;

import org.junit.Test;


public class TestTST {

	@Test
	public void test() {
		
		TST<String> TestTST = new TST<String>();
		
		
		// Testing insert operation
		TestTST.put("Abevej", "Test#1");
		TestTST.put("Kone", "Test#2");
		TestTST.put("Mand", "Test#3");
		TestTST.put("aabbBBccDDefgH", "Test#4");
		TestTST.put("DDeeffggHHecz", "Test#5");
		TestTST.put("1234TigerMand4321", "Test#6");
		TestTST.put("SkoleTid!", "Test#7");
		TestTST.put("KrakHak", "Test#8");
		TestTST.put("abcdeFghJJikm", "Test#9");
		TestTST.put("A", "Test#10");
		
		
		// Testing for search hit and miss
		assertEquals("Test#1",TestTST.get("Abevej"));
		assertEquals("Test#2",TestTST.get("Kone"));
		assertEquals(null,TestTST.get("SuperKone"));
		assertEquals(null,TestTST.get("sKoLeTid!"));
		
		// Prefix and wildcard testing
		Iterable<String> testSetA = TestTST.prefixMatch("A");
		Iterable<String> testSetB = TestTST.wildcardMatch("DD");
		
		for(String s : testSetA)
		{
			if(s.equals("Abevej") || s.equals("A"))
			{
				assertEquals(true, true);
			}
			else {
				assertEquals(false,true);
			}
		}
		
		for(String s : testSetB)
		{
			if(s.equals("aabbBBccDDefgH") || s.equals("DDeeffggHHecz"))
			{
				assertEquals(true, true);
			}
			else {
				assertEquals(false,true);
			}
		}
		
		// False testing
		assertEquals(false, TestTST.get("1234TigerMand4321").equals("OngoBongo"));
		assertEquals(false, TestTST.get("1234TigerMand4321").equals(""));
		assertEquals(false, TestTST.get("1234TigerMand4321").equals(null));
		assertEquals(false, TestTST.get("KrakHak").equals("test#8"));
		assertEquals(null, TestTST.get("krakHak"));
	}

}
