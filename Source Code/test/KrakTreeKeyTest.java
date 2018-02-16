package test;

import model.data.Point;
import model.data.RoadType;
import model.tree.KrakTreeKey;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class KrakTreeKeyTest {
	
	KrakTreeKey kGreat;
	KrakTreeKey kSmall;
	KrakTreeKey kAlsoSmall;
	
	@Before
	public void setup() {
		Point sGreat = new Point(10, 10);
		Point eGreat = new Point(10, 10);
		Point sSmall = new Point(1, 1);
		Point eSmall = new Point(1, 1);
		kGreat = new KrakTreeKey(sGreat, eGreat, RoadType.STI);
		kSmall = new KrakTreeKey(sSmall, eSmall, RoadType.MOTORVEJ);
		kAlsoSmall = new KrakTreeKey(sSmall, sSmall, RoadType.MOTORVEJ);
	}

	@Test
	public void testNull() {
		try {
			new KrakTreeKey(null,null,null);
			fail("Exception not thrown");
		} catch (NullPointerException e) {
			//Succes!
		}
	}
	
	@Test
	public void testStartXGreater() {
		int cmp = kGreat.compareInDimension(kSmall, 0);
		assertTrue(cmp == 1);
	}
	
	@Test
	public void testStartYGreater() {
		int cmp = kGreat.compareInDimension(kSmall, 1);
		assertTrue(cmp == 1);
	}
	
	@Test
	public void testEndXGreater() {
		int cmp = kGreat.compareInDimension(kSmall, 2);
		assertTrue(cmp == 1);
	}
	
	@Test
	public void testEndYGreater() {
		int cmp = kGreat.compareInDimension(kSmall, 3);
		assertTrue(cmp == 1);
	}
	
	@Test
	public void testRoadTypeGreater() {
		int cmp = kGreat.compareInDimension(kSmall, 4);
		assertTrue(cmp == 1);
	}
	
	@Test
	public void testStartXSmaller() {
		int cmp = kSmall.compareInDimension(kGreat, 0);
		assertTrue(cmp == -1);
	}
	
	@Test
	public void testStartYSmaller() {
		int cmp = kSmall.compareInDimension(kGreat, 1);
		assertTrue(cmp == -1);
	}
	
	@Test
	public void testEndXSmaller() {
		int cmp = kSmall.compareInDimension(kGreat, 2);
		assertTrue(cmp == -1);
	}
	
	@Test
	public void testEndYSmaller() {
		int cmp = kSmall.compareInDimension(kGreat, 3);
		assertTrue(cmp == -1);
	}
	
	@Test
	public void testRoadTypeSmaller() {
		int cmp = kSmall.compareInDimension(kGreat, 4);
		assertTrue(cmp == -1);
	}
	
	@Test
	public void testStartXEqual() {
		int cmp = kSmall.compareInDimension(kAlsoSmall, 0);
		assertTrue(cmp == 0);
	}
	
	@Test
	public void testStartYEqual() {
		int cmp = kSmall.compareInDimension(kAlsoSmall, 1);
		assertTrue(cmp == 0);
	}
	
	@Test
	public void testEndXEqual() {
		int cmp = kSmall.compareInDimension(kAlsoSmall, 2);
		assertTrue(cmp == 0);
	}
	
	@Test
	public void testEndYEqual() {
		int cmp = kSmall.compareInDimension(kAlsoSmall, 3);
		assertTrue(cmp == 0);
	}
	
	@Test
	public void testRoadTypeEqual() {
		int cmp = kSmall.compareInDimension(kAlsoSmall, 4);
		assertTrue(cmp == 0);
	}

}
