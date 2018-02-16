package test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import model.Serializer;
import model.data.Edge;
import model.data.Point;
import model.data.RoadType;
import static org.junit.Assert.*;

import org.junit.Test;

public class TestSerializer {

	
	// Basic outputstream test
	@Test
	public void test() {

		File listTestFile = new File("test/listtest.ser");
		ArrayList<Integer> listTest = new ArrayList<Integer>();
		for(int i = 0; i<10000; i++)
		{
			listTest.add(i);
		}
		
		Serializer.write(listTest, listTestFile);
		ArrayList<Integer> resultList = Serializer.read(listTestFile);
		
		for(int i = 0; i<10000; i++)
		{
			assertEquals(true,listTest.get(i).equals(resultList.get(i)));
		}
		
		File testStringFile = new File("test/stringtest.ser");
		String testString = "Testing";
		Serializer.write(testString, testStringFile);
		assertEquals(true,Serializer.read(testStringFile).equals(testString));
		
		
		}
	}

