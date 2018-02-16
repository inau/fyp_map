
package model.graph;

import model.Bag;
import model.Search;
import model.Stopwatch;
import model.data.Edge;
import model.data.Edge.OneWayType;

/**
 * Directed Graph class from algs4 @ Princeton - altered
 */
public class DiGraph {

	Search.Data k;
	protected int V;
	protected int E;
	protected int EInSet = 0;
	protected static double progress = 0;
	protected static Bag<Edge>[] adj;

	/**
	 * Create an empty edge-weighted digraph based on the KrakData.
	 */
	public DiGraph() {
		k = Search.Data.INSTANCE;
		if (k.size() < 0) throw new RuntimeException("Number of vertices must be nonnegative");
		V = 0;
		E = 0;
		int maxsize = k.getNodeSize(); //gets the max size from our dataset to generate the array
		adj = (Bag<Edge>[]) new Bag[maxsize];
		for(int i = 0; i < maxsize ; i++ ) {
			adj[i] = new Bag<Edge>(); //generating empty bags
		}
		generateGraph();
		V = k.getNodeSize();
	}

	/**
	 * Create an empty edge-weighted digraph based on edge collection.
	 */
//		public DiGraph(Collection<Edge> c) {
//			if (c.size() < 0) throw new RuntimeException("Number of vertices must be nonnegative");
//			V = 0;
//			E = 0;
//			int maxsize = c.size(); //gets the max size from our dataset to generate the array
//			adj = (Bag<Edge>[]) new Bag[maxsize];
//			for(int i = 0; i < maxsize ; i++ ) {
//				adj[i] = new Bag<Edge>(); //generating empty bags
//			}
//			generateGraph(c);
//		}

	/**
	 * Generate the edges based on collection
	 */
	//	protected void generateGraph(Collection<Edge> c) {
	//		int key;
	//		for(Edge e : c) {
	//			if(e.getDirection() != null && e.getDirection().equals("ft")) {
	//				key = e.getStart().getId(); // reference key (edge start-point id)
	//				//check whether there are prior references for vertex count
	//				if(adj[key].size() == 0) V++;
	//				addEdge(e);
	//			}
	//			else if(e.getDirection() != null && e.getDirection().equals("tf")) {
	//				key = e.getEnd().getId();
	//				if(adj[key].size() == 0) V++;
	//				addInvertedEdge(e);
	//			}
	//			else {
	//				//check whether there are prior references for vertex count
	//				if(adj[e.getStart().getId()].size() == 0) { V++;}
	//				addEdge(e);
	//				if(adj[e.getEnd().getId()].size() == 0) { V++;}
	//				addInvertedEdge(e);
	//			}
	//			EInSet++; //keeps track of edges in set
	//			// this is written for keeping track of progress
	//			Search.Graph.INSTANCE.setProgress( (double)EInSet/(double)c.size() );
	//		}
	//	}

	/**
	 * Generate the edges based on input
	 */
	protected void generateGraph() {
		double ksize = k.getAll().size();
		new Stopwatch();
		for(Edge e : k.getAll()) 
		{
			EInSet++;
			 //keeps track of edges in set
			// this is written for keeping track of progress.
			Search.Graph.INSTANCE.setProgress( (double)EInSet/ksize );
			OneWayType owt = e.getOneWay();
			if(owt == OneWayType.FROMTO)
			{
				addEdge(e); continue;
			}
			else if(owt == OneWayType.TOFROM)
			{
				addInvertedEdge(e); continue;
			}
			else if(owt == OneWayType.NONE)
			{
				addEdge(e);
				addInvertedEdge(e); continue;
			}
		}
	}

	/**
	 * Return the number of vertices(points) in this digraph.
	 */
	public int V() {
		return V;
	}

	public Bag<Edge> getAdj(int v) {
		if(v < 0)	throw new IllegalArgumentException("parameter v wal negative");
		
		return adj[v];
	}

	/**
	 * Return the number of edges in this digraph.
	 */
	public int E() {
		return E;
	}

	/**
	 * Add the edge e to this digraph.
	 */
	public void addEdge(Edge e) {
		if(e == null)	throw new NullPointerException("parameter e was null");
		
		adj[e.getStart().getId()].add(e); //add to current references
		E++;
	}

	/**
	 * Add the inverted edge of e to this digraph.
	 */
	public void addInvertedEdge(Edge e) {
		if(e == null)	throw new NullPointerException("parameter e was null");
		
		Edge tmp = e.invertEdge();
		adj[tmp.getStart().getId()].add(tmp); //add to current refs
		E++;
	}

	/**
	 * Return the edges leaving Point p as an Iterable.
	 * To iterate over the end points leaving vertex v, use foreach notation:
	 * <tt>for (Point p : graph.adj.get(v))</tt>.
	 */
	public Iterable<Edge> adj(int p) {
		if(p < 0)	throw new IllegalArgumentException("parameter p was negative");
		
		return adj[p];
	}

	/**
	 * Return all points in this graph as an Iterable.
	 * To iterate over the edges, use foreach notation:
	 * <tt>for (Edge e : graph.edges())</tt>.
	 */
	public Iterable<Edge> edges() {
		Bag<Edge> list = new Bag<Edge>();
		for (int v = 0; v < V; v++) {
			for (Edge p : adj[v]) {
				list.add(p);
			}
		}
		return list;
	} 

	/**
	 * Return number of edges leaving v.
	 */
	public int outdegree(int p) {
		if(p < 0)	throw new IllegalArgumentException("parameter p was negative");
		
		return adj[p].size();
	}

	/**
	 * test method to print all bags for each entry - used mainly for debugging
	 */
	public void testOutput() {
		System.out.println("Vertices: "+V());
		for(int P = 0; P < adj.length; P++) {
			System.out.println("From: "+P);
			for(Edge e : adj[P])
				System.out.println("\t"+e.getEnd().getId());
		}
	}
}