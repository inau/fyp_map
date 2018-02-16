package model;

import java.util.Map;
import java.util.Set;

import model.data.Edge;
import model.data.KrakDataLoader;
import model.data.Point;
import model.tree.KrakTreeKey;

public enum KrakLoader {

	LOADER;
	
	private KrakDataLoader loaderInstance;
	public final static int DELTA = KrakDataLoader.DELTA;
	
	/**
	 * Constructs a KrakLoader object that will read from the default input files
	 */
	KrakLoader(){
		loaderInstance = new KrakDataLoader();
	}

	/**
	 * Creates all edges and puts them in edgeMap
	 */
	public Map<KrakTreeKey, Edge> getEdgeMap(){
		return loaderInstance.getEdgeMap();
	}
	
	public Map<Integer, Point> getNodeMap() {
		return loaderInstance.getNodeMap();
	}
	
	public Map<String, Set<Edge>> getEdgesByName(){
		return loaderInstance.getEdgesByName();
	}
	
	public Set<Edge> getAllEdges(){
		return loaderInstance.getEdgeSet();
	}
	
	/**
	 * Note: This point does not represent an actual point in the data
	 * @return A point consisting of the largest x-, and y- value. (Not necessary from the same point).
	 */
	public Point getMaxPoint(){
		return loaderInstance.getMaxPoint();
	}
	
	/**
	 * Note: This point does not represent an actual point in the data
	 * @return A point consisting of the lowest x-, and y- value. (Not necessary from the same point).
	 */
	public Point getMinPoint(){
		return loaderInstance.getMinPoint();
	}
}
