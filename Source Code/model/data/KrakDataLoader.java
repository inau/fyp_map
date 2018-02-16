package model.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.data.Edge.OneWayType;
import model.tree.KrakTreeKey;

/**
 * This class is unconditionally thread-safe
 * @author ejer
 *
 */
public class KrakDataLoader {

	public static final int DELTA = 200;
	private static Set<Edge> edgeset;
	private static Map<String, Set<Edge>> edgesByName;
	private static Map<Integer, Point> nodemap;

	private final Object accessorLock = new Object();

	private static double maxPx = 0, maxPy = 0;
	private static double minPx = Double.POSITIVE_INFINITY, minPy = Double.POSITIVE_INFINITY;

	/**
	 * Constructs a KrakLoader object that will read from the default input files
	 */
	public KrakDataLoader(){
		edgesByName = new HashMap<String, Set<Edge>>();
		MapParser.loadDefaultFiles();
		loadNodeMap(); 
		loadEdgesFromTXT();
	}

	/**
	 * Constructs a KrakLoader object that will read from the parameter files
	 * @param pointFile	containing node information
	 * @param edgeFile	containing edge information
	 */
	public KrakDataLoader(String pointFile, String edgeFile){
		if(pointFile == null)			throw new NullPointerException("parameter pointFile was null");
		else if(edgeFile == null)		throw new NullPointerException("parameter edgeFile was null");
		else if(pointFile.equals(""))	throw new IllegalArgumentException("parameter pointFile was emptystring");
		else if(edgeFile.equals(""))	throw new IllegalArgumentException("parameter edgeFile was emptystring");
		edgesByName = new HashMap<String, Set<Edge>>();
		MapParser.loadSpecFiles(pointFile, edgeFile);
		loadNodeMap(); 
		loadEdgesFromTXT();
		
	}

	/**
	 * Creates all edges and puts them in edgeMap
	 */
	public Map<KrakTreeKey, Edge> getEdgeMap(){

		Map<KrakTreeKey, Edge> edgemap = new HashMap<KrakTreeKey, Edge>();
		List<KrakTreeKey> keys = new LinkedList<KrakTreeKey>();

		synchronized(accessorLock){
			for(Edge e : edgeset){
				keys = generateKeys(e, keys);
				for(KrakTreeKey key : keys){
					edgemap.put(key, e);
				}
				keys.clear();
			}
			return edgemap;
		}
	}

	public Map<Integer,Point> getNodeMap()  {
		return nodemap;
	}

	/**
	 * Creates all nodes and puts them in nodeMap
	 */
	private void loadNodeMap(){
		nodemap = new HashMap<Integer, Point>();
		String[] idXY = MapParser.nextNodeInfo();
		while(idXY != null){
			Point node = new Point(Integer.parseInt(idXY[0])-1, Double.parseDouble(idXY[1]), Double.parseDouble(idXY[2]));

			if(node.getX() > maxPx)	maxPx = node.getX();		// Dynamically determine the largest value of x
			if(node.getY() > maxPy)	maxPy = node.getY();		// Dynamically determine the largest value of y

			if(node.getX() < minPx)	minPx = node.getX();		// Dynamically determine the lowest value of x
			if(node.getY() < minPy)	minPy = node.getY();		// Dynamically determine the lowest value of y

			nodemap.put(node.getId(), node);
			idXY = MapParser.nextNodeInfo();
		}
	}

	/**
	 * Creates all edges and puts them in edgeMap
	 */
	private void loadEdgesFromTXT(){
		edgeset = new HashSet<Edge>();
		String[] fromto = MapParser.nextEdgeInfo();
		while(fromto != null){	
			if(Integer.parseInt(fromto[0]) == Integer.parseInt(fromto[1])){ 
				fromto = MapParser.nextEdgeInfo(); 
				continue;
			}
			Edge e = createEdge(nodemap, fromto);
			edgeset.add(e);
			fromto = MapParser.nextEdgeInfo();
		}
		loadEdgeMap();		
	}


	private void loadEdgeMap(){
		for(Edge e : edgeset) {
			String name = e.getRoadName();
			if(edgesByName.get(name) == null) 
				edgesByName.put(name, new HashSet<Edge>());
			edgesByName.get(name).add(e);
		}
	}

	private Edge createEdge(Map<Integer, Point> map, String[] fromto){
		int fromId = Integer.parseInt(fromto[0]) -1;
		int toId = Integer.parseInt(fromto[1]) -1;
		String roadName = fromto[2].substring(1, fromto[2].length()-1).trim();
		RoadType roadType = RoadType.getRoadType(Integer.parseInt(fromto[3]));
		String oneway = fromto[4].substring(1, fromto[4].length()-1).trim();
		OneWayType onewaytype = null;
		if(oneway.equals("ft")) { onewaytype = OneWayType.FROMTO; }
		else if(oneway.equals("tf")) { onewaytype = OneWayType.TOFROM; }
		else if(oneway.equals("n")) { onewaytype = OneWayType.NODRIVING; }
		else { onewaytype = OneWayType.NONE; }
		String postleft = fromto[5].trim();
		String postright = fromto[6].trim();

		if(roadType == null)	roadType = RoadType.STEDNAVN;					// Da roadType af en eller anden grund bliver null, sætter vi den til ANDENVEJ..

		return new Edge(map.get(fromId), map.get(toId), roadName, roadType, onewaytype, postleft, postright);
	}

	private List<KrakTreeKey> generateKeys(Edge e, List<KrakTreeKey> list){

		double dx = e.getEnd().getX() - e.getStart().getX();
		double dy = e.getEnd().getY() - e.getStart().getY();	

		list = addKeys(list, e, dx, dy);		

		return list;
	}

	private List<KrakTreeKey> addKeys(List<KrakTreeKey> list, Edge e, double dx, double dy){
		double maxdelta = maxAbs(dx, dy);
		double modifier = (int) maxdelta / Math.abs(maxdelta);
		int amount = (int) Math.abs(maxdelta)/DELTA;
		double startx = e.getStart().getX();			double starty = e.getStart().getY();
		double endx = e.getEnd().getX();				double endy = e.getEnd().getY();
		double a = dy/dx;		

		if(dx == 0)		a = 0;
		if(dy == 0)		a = 1;

		double b = (starty - (a * startx));

		while(amount > 0){
			double startxnew = startx;	
			double startynew = starty;

			if(Math.abs(Math.abs(maxdelta) - Math.abs(dx)) < Math.pow(10, -9)){		// if maxdelta = dx
				startxnew = startx + DELTA * modifier;		// original value plus our delta.
				if(dy != 0)			
					startynew = a * startxnew + b;

			}else{
				startynew = starty + DELTA * modifier;	// original value plus our delta.
				if(a != 0)	
					startxnew = (startynew - b) / a;
			}
			//			System.out.println("dx: " + dx + ", dy: " + dy + ", maxdelta: " + maxdelta + ", startx = " + startx + ", startxnew = " + startxnew + ", starty = " + starty + ", startynew = " + startynew + ", a = " + a + ", b = " + b);
			list.add(new KrakTreeKey(new Point(0, startx, starty), new Point(0, startxnew, startynew), e.getRoadType()));

			startx = startxnew;
			starty = startynew;
			amount--;
		}

		if(!(startx == endx) && !(starty == endy))	
			list.add(new KrakTreeKey(new Point(0, startx, starty), new Point(0, endx, endy), e.getRoadType()));

		return list;

	}

	private double maxAbs(double x1, double x2){
		if(Math.abs(Math.abs(x1) - Math.abs(x2)) < Math.pow(10, -9))		return x1;
		else if(Math.abs(x1) > Math.abs(x2))								return x1;
		else																return x2;
	}

	public Map<String, Set<Edge>> getEdgesByName(){
		return edgesByName;
	}

	/**
	 * Return all edges as a set
	 * @return all edges
	 */
	public Set<Edge> getEdgeSet(){
		return edgeset;
	}

	/**
	 * Note: This point does not represent an actual point in the data
	 * @return A point consisting of the largest x-, and y- value. (Not necessary from the same point).
	 */
	public Point getMaxPoint(){
		return new Point(0, maxPx, maxPy);
	}

	/**
	 * Note: This point does not represent an actual point in the data
	 * @return A point consisting of the lowest x-, and y- value. (Not necessary from the same point).
	 */
	public Point getMinPoint(){
		return new Point(0, minPx, minPy);
	}
}
