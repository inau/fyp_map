package test;


import java.util.Set;

import model.tree.KDTComparable;
import model.tree.KDTree;
import org.junit.Test;
import junit.framework.TestCase;

public class KDTreeTests extends TestCase {
	//Data-set A
	KDTTestPoint A = new KDTTestPoint(1, 1);
	
	//Data-set B
	KDTTestPoint[] B = { new KDTTestPoint(1,1), new  KDTTestPoint(2,2), new KDTTestPoint(0,0) };
	
	//Data-set C
	KDTTestPoint C = new KDTTestPoint(10, 10);
	
	//Data-set D
	KDTTestPoint D = null;
	
	//Data-set E
	KDTTestPoint[] E = { new KDTTestPoint(10,10), new  KDTTestPoint(1,1), new KDTTestPoint(19,19) };
	
	/**
	 * Test class used for whitebox testing the KDTree.
	 * @author Sune Debel
	 *
	 */
	@SuppressWarnings("serial")
	private class KDTTestPoint implements KDTComparable<KDTTestPoint> {
		
		
		final int x;
		final int y;
		
		public KDTTestPoint() {
			x = 0;
			y = 0;
		}
		
		public KDTTestPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public double getDimensionValue(int dimension) {
			switch(dimension) {
			case 0: return x;
			default: return y;
			}
		}

		@Override
		public int compareInDimension(KDTTestPoint point, int dimension) {
			switch(dimension) {
			case 0: return compareX(point);
			default: return compareY(point);
			}
		}

		private int compareY(KDTTestPoint point) {
			if (point.y > y) return -1;
			else if (point.y < y) return 1;
			else return 0;
		}

		private int compareX(KDTTestPoint point) {
			if (point.x > x) return -1;
			else if (point.x < x) return 1;
			else return 0;
		}

		@Override
		public int getDimensions() {
			return 2;
		}
		
		@Override
		public String toString() {
			return "" + x + "." + y;
		}
		
	}
	
	@Test
	public void testPut() {
		KDTree<KDTTestPoint, String> tree = new KDTree<KDTTestPoint, String>(new KDTTestPoint());
		
		//Choice 1: searchkey == null
		try {
			tree.put(D, "null");
			fail("Exception not thrown");
		} catch (NullPointerException e) {
			//Succes!
		}
		
		//choice 1: searchkey != null and choice 2: root == null 
		//this will always be the case on first put()
		tree.put(A, A.toString());
		assertEquals("1.1 - ", tree.toString());
		tree = new KDTree<KDTTestPoint, String>(new KDTTestPoint());
		
		
		
		//choice 2: root != null and choice 3: searchkey < root in dim and choice 3: searchkey > root
		//As this is the second put, root should now be key and value "1.1", and not "2.2"
		//furthermore, the point "2.2" should be the right child, and "0.0" should be the left
		for (KDTTestPoint p : B) {
			tree.put(p, p.toString());
		}
		assertEquals("1.1 - 0.0 - 2.2 - ", tree.toString());
	}
	
	@Test
	public void testGetRange() {
		//choice 1: root is null
		KDTree<KDTTestPoint, String> tree = new KDTree<KDTTestPoint, String>(new KDTTestPoint());
		Set<String> result = tree.getRange(new KDTTestPoint(0, 0), new KDTTestPoint(10, 10));
		assertTrue(result.isEmpty());
		result.clear();
		tree = new KDTree<KDTTestPoint, String>(new KDTTestPoint());
		
		//choice 1: root != null
		tree.put(C, C.toString());
		result = tree.getRange(new KDTTestPoint(0, 0), new KDTTestPoint(100, 100));
		assertTrue(result.contains("10.10") && result.size() == 1);
		result.clear();
		tree = new KDTree<KDTTestPoint, String>(new KDTTestPoint());
		
		//choice 2: searchkey < root in dim
		for (KDTTestPoint p : E) {
			tree.put(p, p.toString());
		}
		result = tree.getRange(new KDTTestPoint(0, 0), new KDTTestPoint(2, 2));
		assertTrue(result.contains("1.1") && result.size() == 1);
		result.clear();
		
		//choice 2: searchkey > root in dim
		result = tree.getRange(new KDTTestPoint(18, 18), new KDTTestPoint(20, 20));
		assertTrue(result.contains("19.19") && result.size() == 1);
		
		//choice 4: the for loop in getrange() will always be executed exactly 4 times, and is
		//thus covered by the other tests
		
		//choice 5: root outside range. Also covered by test of choice 2 and choice 1.
		
		//choice 6: root in range covered by choice 1.
	}

}
