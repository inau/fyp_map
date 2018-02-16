
package model.graph;
import java.util.ArrayList;
import java.util.List;

import model.IndexMinPQ;
import model.data.Edge;

/*************************************************************************
 *  Dependencies: IndexMinPQ.java Stack.java
 *
 *  Dijkstra's algorithm. Computes the shortest path tree.
 *  Assumes all weights are nonnegative.
 *
 *  Courtesy of Robert sedgewick and Kevin Wayne - algs4
 *************************************************************************/

public class DijkstraSP {
	private static DiGraph G;
	private double[] distTo;          // distTo[v] = distance  of shortest s->v path
	private Edge[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path
	private IndexMinPQ<Double> pq;    // priority queue of vertices

	/**
	 * Constructs 2 shortest paths - from edge start and end
	 * @param G - directed graph
	 * @param from - edge to search
	 */
	public static Route searchRoutes(Edge from, Edge to) {

		if(G == null) {
			System.err.println("Set the Graph before calling Dijkstra.searchRoutes");
			return null;
		}
		
		if(from == null)	throw new NullPointerException("parameter from was null");
		else if(to == null)	throw new NullPointerException("parameter to was null");

		//generating 2 paths - from start and end point of the edge
		DijkstraSP route1 = new DijkstraSP(G, from.getStart().getId());
		DijkstraSP route2 = new DijkstraSP(G, from.getEnd().getId());

		//result sets
		List<Edge>[] results = new List[4];

		//results based on start-edge start + end-edge start or end
		results[0] = route1.pathTo(from, to.getStart().getId(), to);
		results[1] = route1.pathTo(from, to.getEnd().getId(), to);

		//results based on start-edge end + end-edge start or end
		results[2] = route2.pathTo(from, to.getStart().getId(), to);
		results[3] = route2.pathTo(from, to.getEnd().getId(), to);

		//length comparison array
		double[] a = new double[4];

		//iterates every result for length
		int x = 0;
		for(List<Edge> i : results) {
			if(i.size() > 0) {
				for(Edge e : i) {
					a[x] += e.getDist();
				}
			}
			else{
				a[x] = Double.POSITIVE_INFINITY;
			}
			x++;
		}

		//returns shortest of these paths
		if		(a[0] <= a[1] && a[0] <= a[2] && a[0] <= a[3]) 		return new Route(results[0]);
		else if	(a[1] <= a[0] && a[1] <= a[2] && a[1] <= a[3])		return new Route(results[1]);
		else if	(a[2] <= a[0] && a[2] <= a[1] && a[2] <= a[3])		return new Route(results[2]);
		else return new Route(results[3]);
	}

	public static void setGraph(DiGraph g) {
		if(g == null)	throw new NullPointerException("parameter g was null");
		G = g; //pun intended
	}

	private DijkstraSP(DiGraph G, int source) {
		distTo = new double[G.V()];
		edgeTo = new Edge[G.V()];
		for (int v = 0; v < G.V(); v++)
			distTo[v] = Double.POSITIVE_INFINITY;
		distTo[source] = 0.0;

		// relax vertices in order of distance from source
		pq = new IndexMinPQ<Double>(G.V());
		pq.insert(source, distTo[source]);
		while (!pq.isEmpty()) {
			int v = pq.delMin();
			for (Edge e : G.getAdj(v))
				relax(e);
		}

		//		System.out.println(G.V()); 
		//		System.out.println("DG Check: "+check(G, source));
	}

	// relax edge e and update pq if changed
	private void relax(Edge e) {
		int v = e.getStart().getId(), w = e.getEnd().getId();
		if (distTo[w] > distTo[v] + e.getDist()) {
			distTo[w] = distTo[v] + e.getDist();
			edgeTo[w] = e;
			if (pq.contains(w)) pq.change(w, distTo[w]);
			else                pq.insert(w, distTo[w]);
		}
	}

	// length of shortest path from s to v
	public double distTo(int v) {
		if(v < 0)	throw new IllegalArgumentException("parameter v was negative");
		return distTo[v];
	}

	// is there a path from s to v?
	public boolean hasPathTo(int v) {
		if(v < 0)	throw new IllegalArgumentException("parameter v was negative");
		return distTo[v] < Double.POSITIVE_INFINITY;
	}

	// shortest path from s to v as an Iterable, null if no such path
	public List<Edge> pathTo(Edge start, int v, Edge to) {
		if(start == null)	throw new NullPointerException("parameter start was null");
		else if(to == null)	throw new NullPointerException("parameter to was null");
		else if(v < 0)		throw new IllegalArgumentException("parameter v was negative");
		
		List<Edge> path = new ArrayList<Edge>();
		if (!hasPathTo(v)) return path;

		path.add(to);
		for (Edge e = edgeTo[v]; e != null; e = edgeTo[e.getStart().getId()]) {
			path.add(e);
		}
		path.add(start);

		return path;
	}


	// check optimality conditions:
	// (i) for all edges e:            distTo[e.to()] <= distTo[e.from()] + e.weight()
	// (ii) for all edge e on the SPT: distTo[e.to()] == distTo[e.from()] + e.weight()
	private boolean check(DiGraph G, int source) {

		// check that edge weights are nonnegative
		for (Edge e : G.edges()) {
			if (e.getDist() < 0) {
				System.err.println("negative edge weight detected");
				return false;
			}
		}

		// check that distTo[v] and edgeTo[v] are consistent
		if (distTo[source] != 0.0 || edgeTo[source] != null) {
			System.err.println("distTo[s] and edgeTo[s] inconsistent");
			return false;
		}
		for (int v = 0; v < G.V(); v++) {
			if (v == source) continue;
			if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
				System.err.println("distTo[] and edgeTo[] inconsistent");
				return false;
			}
		}

		// check that all edges e = v->w satisfy distTo[w] <= distTo[v] + e.weight()
		for (int v = 0; v < G.V(); v++) {
			for (Edge e : G.adj(v)) {
				int w = e.getEnd().getId();
				if (distTo[v] + e.getDist() < distTo[w]) {
					System.err.println("edge " + e + " not relaxed");
					return false;
				}
			}
		}

		// check that all edges e = v->w on SPT satisfy distTo[w] == distTo[v] + e.weight()
		for (int w = 0; w < G.V(); w++) {
			if (edgeTo[w] == null) continue;
			Edge e = edgeTo[w];
			int v = e.getStart().getId();
			if (w != e.getEnd().getId()) return false;
			if (distTo[v] + e.getDist() != distTo[w]) {
				System.err.println("edge " + e + " on shortest path not tight");
				return false;
			}
		}
		return true;
	}
}

