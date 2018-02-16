package model;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import model.data.Edge;
import model.data.Point;
import model.data.RoadType;
import model.graph.DiGraph;
import model.graph.DijkstraSP;
import model.graph.Route;
import model.tree.KDTree;
import model.tree.KrakTreeKey;
import model.trie.PostalMap;
import model.trie.TST;

/**
 * Access point for all classes communicating with the model.
 * @author ejer
 *
 */
public enum Search{
	;
	/**
	 * This enum is used to contain a single instance of DataSearch (Singleton pattern). It is unconditionally thread-safe. Relevant methods are forwarded to DataSearch. 
	 */
	public enum Data implements Progress{
		INSTANCE;

		DataSearch<KrakTreeKey, Edge> dataInstance = new KDTree<KrakTreeKey, Edge>(new KrakTreeKey());
		final int buffer = 210;		// Amount to expand range search (As dataInstance only returns values where all dimensions are in range)
		final File datdir = new File("dat");
		final File kdfile = new File(datdir + File.separator + "kdtree.ser");
		final File nodefile = new File(datdir + File.separator + "nodesize.ser");
		private int nodeSize = 0;
		private double progress = 0;
		private final Object progressLock = new Object();

		// return the number of nodes in the data set used by the DataSearch instance.
		private int nodeSize()
		{
			if(nodefile.exists()) {
				return Serializer.read(nodefile);
			}
			else {
				int size = KrakLoader.LOADER.getNodeMap().size();
				Serializer.write(size, nodefile);
				return size;
			}
		}

		/**
		 * 
		 * @return the number of points contained in the DataSearch object
		 */
		public int getNodeSize(){		
			return nodeSize;
		}

		/**
		 * Used to initialize the DataSearch object contained in this singleton together with the nodeSize field
		 */
		public void initialize(){

			nodeSize = nodeSize();

			KDTree<KrakTreeKey, Edge> kt = null;

			if(kdfile.exists()) {
				dataInstance = Serializer.readImplementer(kdfile);
			}else {
				dataInstance.build(KrakLoader.LOADER.getEdgeMap());
				kt = (KDTree<KrakTreeKey, Edge>) dataInstance;
				if(!datdir.exists()) { datdir.mkdir(); } 
				Serializer.writeImplementer(kt, new File("dat/kdtree.ser"));
			}
		}

		/** 
		 * @return the size of the data structure (number of keys)
		 */
		public int size(){
			return dataInstance.size();
		}

		/**
		 * Return all values in the range
		 * @param keyfrom	the key to start from
		 * @param keyto		the key to end with
		 * @return			a set containing all V within the bounds of start and end
		 */
		public Set<Edge> getRange(Point from, Point to, int zoom){
			RoadType roadType = RoadType.getTypeByZoom(zoom);
			from = new Point(0, from.getX()-buffer, from.getY()-buffer);
			to = new Point(0, to.getX()+buffer, to.getY()+buffer);
			KrakTreeKey key1 = new KrakTreeKey(from, from, RoadType.MOTORVEJ);
			KrakTreeKey key2 = new KrakTreeKey(to, to, roadType);
			Set<Edge> resultset = dataInstance.getRange(key1, key2);
			return resultset;				
		}

		/**
		 * @return All values.
		 */
		public Set<Edge> getAll(){
			return getRange(Search.Coordinate.INSTANCE.getMinPoint(), Search.Coordinate.INSTANCE.getMaxPoint(), RoadType.getMaxZoom());
		}

		@Override
		public double getProgress(){
			synchronized(progressLock){
				return progress;			
			}			
		}

		@Override
		public void setProgress(double percent) {
			synchronized(progressLock){
				progress = percent;
			}
		}
	}


	/**
	 * This class is used to contain a single instance of StringSearch (Singleton pattern). Relevant methods are forwarded to StringSearch.
	 */
	public enum Text implements Progress{
		INSTANCE;

		final TextSearch<Set<Edge>> textInstance = new TST<Set<Edge>>();
		final File postalFile = new File("dat" + File.separator + "postalinfo.txt");
		Map<String,String> postalMap = new PostalMap(postalFile).getPostalMap();
		private double progress = 0;
		private final Object progressLock = new Object();

		/**
		 * Used to initialize the TextSearch object contained in this singleton.
		 */
		public void initialize(){
			textInstance.build(getEdgesByName());
		}

		private Map<String, Set<Edge>> getEdgesByName(){
			Map<String, Set<Edge>> edgesByName = new HashMap<String, Set<Edge>>();
			for(Edge e : Search.Data.INSTANCE.getAll()) {
				String name = e.getRoadName();
				if(edgesByName.get(name) == null) 
					edgesByName.put(name, new HashSet<Edge>());
				edgesByName.get(name).add(e);
			}

			return edgesByName;
		}

		/**
		 * Find all edges associated with parameter
		 * @param key
		 * @return a set. 
		 */
		public Set<Edge> get(String[] key)
		{
			Set<Edge> sorted = new HashSet<Edge>();
			if(key[0].trim() == null || key[0].trim().length() == 0) return sorted;
			Set<Edge> results = textInstance.get(key[0].trim());
			if(results == null) return sorted;
			for(Edge e : results)
			{
				if(e.getPostalLeft().equals(key[2]) || e.getPostalRight().equals(key[2]))
				{
					sorted.add(e);
				}
			}
			return sorted;
		}

		/**
		 * Find all keys starting with given prefix. Not case sensitive.
		 * @param prefix
		 * @return all keys starting with prefix
		 */
		public Iterable<String> prefixMatch(String prefix){
			// Create a new string, with first letter of every word as upper case
			String[] tmp = prefix.split(" ");
			String tmp1 = "";
			for(int i = 0 ; i < tmp.length ; i++){
				tmp1 += Character.toUpperCase(tmp[i].charAt(0)) + tmp[i].substring(1) + " "; 
			}
			tmp1 = tmp1.substring(0, tmp1.length()-1);

			return textInstance.prefixMatch(tmp1);
		}

		/**
		 * Method to that returns a list of addresses with appended postal code and postal name.
		 * If the postal info document contained in the program does not contain information for the
		 * specified postal code, the address will only contain the postal code and no name
		 * @param address the prefix to match addresses against
		 * @return a list of strings with appended postal codes and names
		 */
		public Iterable<String> prefixAndPostal(String address)
		{

			Iterable<String> list = prefixMatch(address); // Retrieve list just with prefix
			HashSet<String> done = new HashSet<String>();
			if(list == null) return done;
			for(String s : list)
			{
				Iterable<Edge> edges = textInstance.get(s); // Get suitable edges from TST
				if(edges == null) continue;
				for(Edge e : edges)
				{
					if(e == null) continue;
					String left = s + ", " + e.getPostalLeft(); // Get postal information from edges
					String right = s + ", " + e.getPostalRight();
					if(postalMap.get(e.getPostalLeft().trim()) != null) { left += ", " + postalMap.get(e.getPostalLeft().trim()); }
					if(postalMap.get(e.getPostalRight().trim()) != null) { right += ", " + postalMap.get(e.getPostalRight().trim()); }

					done.add(left);
					done.add(right); // Add postal codes to the returned list.
				}
			}
			return done;

		}

		@Override
		public double getProgress(){
			synchronized(progressLock){
				return progress;
			}
		}

		@Override
		public void setProgress(double percent) {
			synchronized(progressLock){
				progress = percent;
			}
		}
	}

	/**
	 * This class is used to contain two instances of Point. Representing the two points consisting of the two lowest and two highest x and y values.
	 */
	public enum Coordinate{
		INSTANCE;

		private final File maxPointFile = new File("dat/maxpoint.ser");
		private final File minPointFile = new File("dat/minpoint.ser");

		private final Point maxPoint;
		private final Point minPoint;

		/**
		 * Initializes the two points
		 */
		Coordinate(){
			if(maxPointFile.exists()){
				maxPoint = Serializer.read(maxPointFile);
			}else{
				maxPoint = KrakLoader.LOADER.getMaxPoint();
				Serializer.write(maxPoint, maxPointFile);
			}

			if(minPointFile.exists()){
				minPoint = Serializer.read(minPointFile);
			}else{
				minPoint = KrakLoader.LOADER.getMinPoint();
				Serializer.write(minPoint, minPointFile);
			}
		}

		/**
		 * Note: This point does not represent an actual point in the data
		 * @return A point consisting of the largest x-, and y- value. (Not necessary from the same point).
		 */
		public Point getMaxPoint(){
			return maxPoint;
		}

		/**
		 * Note: This point does not represent an actual point in the data
		 * @return A point consisting of the lowest x-, and y- value. (Not necessary from the same point).
		 */
		public Point getMinPoint(){
			return minPoint;
		}
	}

	/**
	 * This class is used to contain a single Graph instance(Singleton pattern). Relevant methods are forwarded to the Graph object.
	 */
	public enum Graph implements Progress{
		INSTANCE;

		private double progress = 0;
		private Object progressLock = new Object();

		/**
		 * Initialize the graph object contained in this singleton.
		 */
		public void initialize() {
			DiGraph dg = new DiGraph();
			DijkstraSP.setGraph(dg);
		}

		/**
		 * Constructs 2 shortest paths - from edge start and end
		 * @param to - edge to search to
		 * @param from - edge to search from
		 * @return the shortest of the two constructed Routes
		 */
		public Route searchRoutes(Edge fromEdge, Edge toEdge){
			return DijkstraSP.searchRoutes(fromEdge,toEdge);
		}

		@Override
		public double getProgress() {
			synchronized(progressLock){
				return progress;
			}
		}

		@Override
		public void setProgress(double percent) {
			synchronized(progressLock){
				progress = percent;
			}
		}
	}
}
