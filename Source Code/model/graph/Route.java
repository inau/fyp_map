package model.graph;

import java.util.List;

import model.data.Edge;

public class Route {

	Edge[] edges;
	String[] route;
	List<Edge> ed;

	/**
	 * Constructs the Route model based on a list of edges
	 * @param ed list of edges
	 */
	public Route(List<Edge> ed) {
		if(ed == null)	throw new NullPointerException("parameter ed was null");
		
		this.ed = ed;
		edges = new Edge[ed.size()];
		route = new String[ed.size()];
		int z = ed.size()-1;
		for(Edge e : ed) { //reverses the order since our Dijkstra returned in reverse order
			edges[z--] = e;
		}

		//runs through every edge and determines name and direction arrays.
		for(int x = 0 ; x < edges.length-1  ; x++) {
			if(x == edges.length-1) {
				route[x+1] = edges[x+1].getRoadName();
			}
			if(edges[x].getRoadName().equals(edges[x+1].getRoadName())) { //if 2 IDENTIC names occur the name is replaced with a line
				route[x] = " | ";
			}
			else {
				route[x] = edges[x].getRoadName();
			}
			
		}
	}

	/**
	 * returns the original edge list - used for debugging mainly
	 * @return list of edges
	 */
	public List<Edge> geted() {
		return ed;
	}

	/**
	 * returns a list of all edges that we pass
	 * @return
	 */
	public String[] getWrittenRoute() {
		return route;
	}
	
	/**
	 * returns a edge array to be represent the route graphical
	 * @return edge array
	 */
	public Edge[] getVisualRoute() {
		return edges;
	}	
}
