package test;


import static org.junit.Assert.assertEquals;


import java.util.Map;

import model.data.Edge;
import model.data.KrakDataLoader;
import model.data.Point;
import model.data.RoadType;
import model.tree.KrakTreeKey;

import org.junit.Before;
import org.junit.Test;

public class KrakLoaderTests {
	
	Map<KrakTreeKey, Edge> map;
	
	@Before
	public void setUp() {
		KrakDataLoader kl = new KrakDataLoader("KrakLoaderPointTestInput.txt", "KrakLoaderEdgeTestInput.txt");
		map = kl.getEdgeMap();
	}

	@Test
	public void testDXGreaterThan200() {
		KrakTreeKey k1 = new KrakTreeKey(new Point(10000, 10000), new Point(10200, 10000), RoadType.STI);
		KrakTreeKey k2 = new KrakTreeKey(new Point(10200, 10000), new Point(10400, 10000), RoadType.STI);
		KrakTreeKey k3 = new KrakTreeKey(new Point(10400, 10000), new Point(10600, 10000), RoadType.STI);
		KrakTreeKey k4 = new KrakTreeKey(new Point(10600, 10000), new Point(10800, 10000), RoadType.STI);
		KrakTreeKey k5 = new KrakTreeKey(new Point(10800, 10000), new Point(11000, 10000), RoadType.STI);
		KrakTreeKey k6 = new KrakTreeKey(new Point(11000, 10000), new Point(11200, 10000), RoadType.STI);
		KrakTreeKey k7 = new KrakTreeKey(new Point(11200, 10000), new Point(11400, 10000), RoadType.STI);
		KrakTreeKey k8 = new KrakTreeKey(new Point(11400, 10000), new Point(11600, 10000), RoadType.STI);
		KrakTreeKey k9 = new KrakTreeKey(new Point(11600, 10000), new Point(11800, 10000), RoadType.STI);
		KrakTreeKey k10 = new KrakTreeKey(new Point(11800, 10000), new Point(12000, 10000), RoadType.STI);
		
		assertEquals(map.get(k1).getRoadName(), "DXGreaterThan200");
		assertEquals(map.get(k2).getRoadName(), "DXGreaterThan200");
		assertEquals(map.get(k3).getRoadName(), "DXGreaterThan200");
		assertEquals(map.get(k4).getRoadName(), "DXGreaterThan200");
		assertEquals(map.get(k5).getRoadName(), "DXGreaterThan200");
		assertEquals(map.get(k6).getRoadName(), "DXGreaterThan200");
		assertEquals(map.get(k7).getRoadName(), "DXGreaterThan200");
		assertEquals(map.get(k8).getRoadName(), "DXGreaterThan200");
		assertEquals(map.get(k9).getRoadName(), "DXGreaterThan200");
		assertEquals(map.get(k10).getRoadName(), "DXGreaterThan200");
	}
	
	@Test
	public void testDYGreaterThan200() {
		KrakTreeKey k1 = new KrakTreeKey(new Point(10000, 10000), new Point(10000, 10200), RoadType.STI);
		KrakTreeKey k2 = new KrakTreeKey(new Point(10000, 10200), new Point(10000, 10400), RoadType.STI);
		KrakTreeKey k3 = new KrakTreeKey(new Point(10000, 10400), new Point(10000, 10600), RoadType.STI);
		KrakTreeKey k4 = new KrakTreeKey(new Point(10000, 10600), new Point(10000, 10800), RoadType.STI);
		KrakTreeKey k5 = new KrakTreeKey(new Point(10000, 10800), new Point(10000, 11000), RoadType.STI);
		KrakTreeKey k6 = new KrakTreeKey(new Point(10000, 11000), new Point(10000, 11200), RoadType.STI);
		KrakTreeKey k7 = new KrakTreeKey(new Point(10000, 11200), new Point(10000, 11400), RoadType.STI);
		KrakTreeKey k8 = new KrakTreeKey(new Point(10000, 11400), new Point(10000, 11600), RoadType.STI);
		KrakTreeKey k9 = new KrakTreeKey(new Point(10000, 11600), new Point(10000, 11800), RoadType.STI);
		KrakTreeKey k10 = new KrakTreeKey(new Point(10000, 11800), new Point(10000, 12000), RoadType.STI);
		
		assertEquals(map.get(k1).getRoadName(), "DYGreaterThan200");
		assertEquals(map.get(k2).getRoadName(), "DYGreaterThan200");
		assertEquals(map.get(k3).getRoadName(), "DYGreaterThan200");
		assertEquals(map.get(k4).getRoadName(), "DYGreaterThan200");
		assertEquals(map.get(k5).getRoadName(), "DYGreaterThan200");
		assertEquals(map.get(k6).getRoadName(), "DYGreaterThan200");
		assertEquals(map.get(k7).getRoadName(), "DYGreaterThan200");
		assertEquals(map.get(k8).getRoadName(), "DYGreaterThan200");
		assertEquals(map.get(k9).getRoadName(), "DYGreaterThan200");
		assertEquals(map.get(k10).getRoadName(), "DYGreaterThan200");
	}
	
	@Test
	public void testDXSmallerThan200() {
		KrakTreeKey k1 = new KrakTreeKey(new Point(10000, 10000), new Point(9800, 10000), RoadType.STI);
		KrakTreeKey k2 = new KrakTreeKey(new Point(9800, 10000), new Point(9600, 10000), RoadType.STI);
		KrakTreeKey k3 = new KrakTreeKey(new Point(9600, 10000), new Point(9400, 10000), RoadType.STI);
		KrakTreeKey k4 = new KrakTreeKey(new Point(9400, 10000), new Point(9200, 10000), RoadType.STI);
		KrakTreeKey k5 = new KrakTreeKey(new Point(9200, 10000), new Point(9000, 10000), RoadType.STI);
		KrakTreeKey k6 = new KrakTreeKey(new Point(9000, 10000), new Point(8800, 10000), RoadType.STI);
		KrakTreeKey k7 = new KrakTreeKey(new Point(8800, 10000), new Point(8600, 10000), RoadType.STI);
		KrakTreeKey k8 = new KrakTreeKey(new Point(8600, 10000), new Point(8400, 10000), RoadType.STI);
		KrakTreeKey k9 = new KrakTreeKey(new Point(8400, 10000), new Point(8200, 10000), RoadType.STI);
		KrakTreeKey k10 = new KrakTreeKey(new Point(8200, 10000), new Point(8000, 10000), RoadType.STI);
		
		assertEquals(map.get(k1).getRoadName(), "DXSmallerThan-200");
		assertEquals(map.get(k2).getRoadName(), "DXSmallerThan-200");
		assertEquals(map.get(k3).getRoadName(), "DXSmallerThan-200");
		assertEquals(map.get(k4).getRoadName(), "DXSmallerThan-200");
		assertEquals(map.get(k5).getRoadName(), "DXSmallerThan-200");
		assertEquals(map.get(k6).getRoadName(), "DXSmallerThan-200");
		assertEquals(map.get(k7).getRoadName(), "DXSmallerThan-200");
		assertEquals(map.get(k8).getRoadName(), "DXSmallerThan-200");
		assertEquals(map.get(k9).getRoadName(), "DXSmallerThan-200");
		assertEquals(map.get(k10).getRoadName(), "DXSmallerThan-200");
	}
	
	@Test
	public void testDYSmaller200() {
		KrakTreeKey k1 = new KrakTreeKey(new Point(10000, 10000), new Point(10000, 9800), RoadType.STI);
		KrakTreeKey k2 = new KrakTreeKey(new Point(10000, 9800), new Point(10000, 9600), RoadType.STI);
		KrakTreeKey k3 = new KrakTreeKey(new Point(10000, 9600), new Point(10000, 9400), RoadType.STI);
		KrakTreeKey k4 = new KrakTreeKey(new Point(10000, 9400), new Point(10000, 9200), RoadType.STI);
		KrakTreeKey k5 = new KrakTreeKey(new Point(10000, 9200), new Point(10000, 9000), RoadType.STI);
		KrakTreeKey k6 = new KrakTreeKey(new Point(10000, 9000), new Point(10000, 8800), RoadType.STI);
		KrakTreeKey k7 = new KrakTreeKey(new Point(10000, 8800), new Point(10000, 8600), RoadType.STI);
		KrakTreeKey k8 = new KrakTreeKey(new Point(10000, 8600), new Point(10000, 8400), RoadType.STI);
		KrakTreeKey k9 = new KrakTreeKey(new Point(10000, 8400), new Point(10000, 8200), RoadType.STI);
		KrakTreeKey k10 = new KrakTreeKey(new Point(10000, 8200), new Point(10000, 8000), RoadType.STI);
		
		assertEquals(map.get(k1).getRoadName(), "DYSmallerThan-200");
		assertEquals(map.get(k2).getRoadName(), "DYSmallerThan-200");
		assertEquals(map.get(k3).getRoadName(), "DYSmallerThan-200");
		assertEquals(map.get(k4).getRoadName(), "DYSmallerThan-200");
		assertEquals(map.get(k5).getRoadName(), "DYSmallerThan-200");
		assertEquals(map.get(k6).getRoadName(), "DYSmallerThan-200");
		assertEquals(map.get(k7).getRoadName(), "DYSmallerThan-200");
		assertEquals(map.get(k8).getRoadName(), "DYSmallerThan-200");
		assertEquals(map.get(k9).getRoadName(), "DYSmallerThan-200");
		assertEquals(map.get(k10).getRoadName(), "DYSmallerThan-200");
	}
	
	@Test
	public void testRemainder() {
		KrakTreeKey k = new KrakTreeKey(new Point(12000, 10000), new Point(12100, 10000), RoadType.STI);
		assertEquals(map.get(k).getRoadName(), "DXGreaterThan200");
	}
	

}
