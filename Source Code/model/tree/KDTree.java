package model.tree;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.DataSearch;
import model.QuickKDTSort;
import model.Search;


/**
 *  
 * This class is unconditionally thread-safe
 *  
 * @author Morten Fredrik Therkildsen (mfrt@itu.dk), Sune Debel (sdeb@itu.dk)
 *
 * @param <K> This class is required to implement the interface KDTComparable<K>. 
 * @param <V> 
 */
public class KDTree<K extends KDTComparable<K>, V> implements DataSearch<K, V>, Serializable{

	private static final long serialVersionUID = 2530395579737497198L;

	private transient final int DIMENSIONS;

	private transient List<K> keys;
	private transient List<V> values;

	private transient double treesize = 0;	// Used for the recursive build method. To return the progress at a given point during building
	private transient Node root;
	public transient int maxdepth = 0;		// Auxiliary field

	private transient final Object stateLock = new Object();
	private transient final Object accessorLock = new Object();

	public KDTree(K k){
		if(k == null)	throw new NullPointerException("parameter k was null");
		
		keys = new ArrayList<K>();
		values = new ArrayList<V>();
		DIMENSIONS = k.getDimensions();
	}

	private class Node{		
		K key;				// key
		V val;				// associated data
		Node left, right;	// subtrees
		int n;				// # nodes in this subtree

		public Node(K key, V val, int n){
			this.key = key;
			this.val = val;
			this.n = n;
		}
	}

	/**
	 * Searches for a node equal to searchkey. If found, adds searchkey as the right leaf of that node. 
	 * If not found, searchkey is added as a node with value val.
	 * @param searchkey
	 * @param val	Can be null.
	 */
	public void put(K searchkey, V val){
		if(searchkey == null)	throw new NullPointerException("parameter searchkey was null");
		synchronized(stateLock){
			root = put(root, searchkey, val, 0);
		}
	}

	private Node put(Node h, K searchkey, V val, int depth){
		synchronized(stateLock){
			if(h == null){
				if(maxdepth < depth)	maxdepth = depth;	// Dynamically determine the maximum depth of the tree
				keys.add(searchkey);
				values.add(val);
				return new Node(searchkey, val, 1);			// Standard insert
			}

			int dim = depth % DIMENSIONS;
			int cmp = searchkey.compareInDimension(h.key, dim);

			if		(cmp < 0)		h.left = put(h.left, searchkey, val, depth+1);
			else if	(cmp > 0)		h.right = put(h.right, searchkey, val, depth+1);
			else					h.right = put(h.right, searchkey, val, depth+1);

			h.n = size(h.left) + size(h.right) + 1;

			return h;
		}
	}

	/**
	 * 
	 * @return the size of the tree
	 */
	public int size(){
		synchronized(accessorLock){
			return size(root);	
		}
	}

	private int size(Node h){
		synchronized(accessorLock){
			if(h == null)	return 0;
			else			return h.n;
		}
	}

	/**
	 * 
	 * @param searchkey
	 * @return values associated with key. Null if key doesn't exist
	 */
	public V get(K searchkey){
		synchronized(stateLock){
			return get(root, searchkey, 0);
		}
	}

	private V get(Node h, K searchkey, int depth){
		if(h == null)	return null;

		synchronized(stateLock){
			int dim = depth % DIMENSIONS;
			int cmp = searchkey.compareInDimension(h.key, dim);			// TODO This method is broken! Refactor or delete!

			if		(cmp < 0)	return get(h.left, searchkey, depth+1);
			else if	(cmp > 0)	return get(h.right, searchkey, depth+1);
			else				return h.val;
		}
	}

	/**
	 * Return all values in the range
	 * @param keyfrom	the key to start from
	 * @param keyto		the key to end with
	 * @return			a set containing all V within the bounds of start and end
	 */
	public Set<V> getRange(K keyfrom, K keyto){
		if(keyfrom == null)		throw new NullPointerException("parameter keyfrom was null");
		else if(keyto == null)	throw new NullPointerException("parameter keyto was null");
		
		Set<V> valueset = new HashSet<V>();
		synchronized(stateLock){
			getRange(root, keyfrom, keyto, valueset, 0);
			//System.out.println("Time for getRange(): " + st.elapsedTime());
			return valueset;
		}
	}

	private void getRange(Node thisNode, K minKey, K maxKey, Set<V> valuelist, int depth){
		if (thisNode == null)			return;

		int dim = depth % DIMENSIONS;
		int[] inRange = dimensionsInRange(minKey, maxKey, thisNode.key);

		synchronized(stateLock){
			if		(inRange[dim] > 0)		getRange(thisNode.left, minKey, maxKey, valuelist, depth+1);
			else if	(inRange[dim] < 0)		getRange(thisNode.right, minKey, maxKey, valuelist, depth+1);
			else{
				boolean allDimsInRange = true;			
				for(int i = 0; i < DIMENSIONS; i++){
					if(inRange[i] != 0)		allDimsInRange = false;
				}

				if	(allDimsInRange){		// Only add key the the set if all dimensions are within bounds
					valuelist.add(thisNode.val);
					getRange(thisNode.left, minKey, maxKey, valuelist, depth+1);
					getRange(thisNode.right, minKey, maxKey, valuelist, depth+1);
				}else{
					getRange(thisNode.left, minKey, maxKey, valuelist, depth+1);
					getRange(thisNode.right, minKey, maxKey, valuelist, depth+1);
				}
			}
		}
	}

	// Helper method to determine what dimensions of compareKey is within the bounds of minKey and maxKey
	private int[] dimensionsInRange(K minKey, K maxKey, K compareKey){		
		int[] dims = new int[DIMENSIONS];
		for(int i = 0; i < DIMENSIONS; i++){
			if	   (compareKey.getDimensionValue(i) < minKey.getDimensionValue(i))	dims[i] = -1;
			else if(compareKey.getDimensionValue(i) > maxKey.getDimensionValue(i))	dims[i] = 1;
			else	dims[i] = 0;
		}
		return dims;
	}

	private void setProgress(double percent){
		Search.Data.INSTANCE.setProgress(percent);
	}

	/**
	 * Builds a balanced kd-tree with keys K and values V
	 */
	public void build(Map<K, V> map){
		if(map == null)	throw new NullPointerException("parameter map was null");
		
		synchronized(stateLock){
			treesize = map.size();
			List<K> keylist = new ArrayList<K>(map.keySet().size());
			keylist.addAll(map.keySet());

			QuickKDTSort.sort(keylist, 0, keylist.size()-1, 0);
			int mean = findMeanIndex(keylist, 0, keylist.size()-1, 0);
			K key = keylist.get(mean);
			put(key, map.get(key));			
			setProgress(size() / treesize);

			buildTree(map, keylist, 0, mean-1, 0);
			buildTree(map, keylist, mean+1, keylist.size()-1, 0);
		}
	}

	private void buildTree(Map<K, V> map, List<K> keylist, int fromIndex, int toIndex, int depth){

		if(fromIndex > toIndex)		return;
		depth++;

		int dimension = depth % DIMENSIONS;

		synchronized(stateLock){
			QuickKDTSort.sort(keylist, fromIndex, toIndex, dimension);
			int mean = findMeanIndex(keylist, fromIndex, toIndex, dimension);

			if(mean != fromIndex &&  mean - 1 >= 0) assert keylist.get(mean).getDimensionValue(dimension) > keylist.get(mean-1).getDimensionValue(dimension) : ".get(mean): " + keylist.get(mean).getDimensionValue(dimension) + ", .get(mean-1): " + keylist.get(mean-1).getDimensionValue(dimension) + ", fromIndex: " + fromIndex + ", toIndex: " + toIndex + ", depth: " + depth;

			K key = keylist.get(mean);
			put(key, map.get(key));

			setProgress(size() / treesize);

			buildTree(map, keylist, fromIndex, mean-1, depth);
			buildTree(map, keylist, mean+1, toIndex, depth);
		}
	}

	private int findMeanIndex(List<K> list, int fromIndex, int toIndex, int dimension){
		if(fromIndex > toIndex)				return -1;
		if(fromIndex == toIndex)			return fromIndex;

		double mean = findMean(list, fromIndex, toIndex, dimension);		// Find the mean value of the dimension

		if(mean == -1)		return -1;

		double nearest = list.get(toIndex).getDimensionValue(dimension) - list.get(fromIndex).getDimensionValue(dimension);
		int nearestIndex = fromIndex;
		for(int i = fromIndex ; i <= toIndex ; i++){
			double dimensionValue = list.get(i).getDimensionValue(dimension);
			if( Math.abs(dimensionValue - mean) < Math.pow(10, -8) ){

				return i;		// If the mean matches a value, return index of that value
			}else{
				if(Math.abs(dimensionValue - mean) < nearest){
					nearest = Math.abs(dimensionValue - mean);
					nearestIndex = i;
				}
			}
		}

		return nearestIndex;	// Return the index of the value closest to mean
	}

	private double findMean(List<K> list, int fromIndex, int toIndex, int dimension){
		if(toIndex == fromIndex)						return list.get(toIndex).getDimensionValue(dimension);		// If difference between to and from is zero

		// Below is a method to accurately calculate the sum of a set of numbers
		double sum = 0;
		double carry = 0;          				//A running compensation for lost low-order bits.
		for(int i = fromIndex ; i <= toIndex ; i++){
			double value = list.get(i).getDimensionValue(dimension) - carry;    
			double total = sum + value;         //Alas, sum is big, y small, so low-order digits of y are lost.
			carry = (total - sum) - value;   	//(t - sum) recovers the high-order part of y; subtracting y recovers -(low part of y)
			sum = total;
		}

		return sum / ( (toIndex - fromIndex) + 1);		// The smaller the sub-array, the greater the loss of precision
	}

	/**
	 * This class stores all information necessary to create a faithful replica of this KDTree instance during deserialization.
	 * 
	 * @author Morten Fredrik Therkildsen (mfrt@itu.dk), Sune Debel (sdeb@itu.dk)
	 *
	 * @param <K>
	 * @param <V>
	 */
	private static class SerializationProxy<K extends KDTComparable<K>, V> implements Serializable{

		private static final long serialVersionUID = 2867552696252679684L;

		List<K> keys;
		List<V> values;
		static final transient Object stateLock = new Object();

		SerializationProxy(KDTree<K, V> tree){
			this.keys = tree.keys;
			this.values = tree.values;
		}

		/**
		 * Creates a new instance of KDTree containing the exact information it had at serialization time
		 * @return a faithful replica of the serialized instance
		 */
		private Object readResolve(){
			KDTree<K, V> tree = new KDTree<K, V>(keys.get(0));
			double treesize = keys.size();
			
			synchronized(stateLock){
				for(int i = 0 ; i < keys.size() ; i++){
					tree.put(keys.get(i), values.get(i));
					tree.setProgress(tree.size() / treesize);
				}
				return tree;
			}
		}
	}

	/**
	 * Overwrites the fields in SerializationProxy with the necessary fields in this KDTree instance
	 * @return A reference to a proxy containing information to reliably recreate this KDTree instance during deserialization
	 */
	private Object writeReplace(){
		synchronized(stateLock){
			return new SerializationProxy<K, V>(this);
		}
	}

	/**
	 * If anyone attempts to modify the state of the deserialized object, an exception will be thrown
	 * @param in
	 * @throws InvalidObjectException
	 */
	private void readObject(ObjectInputStream in) throws InvalidObjectException{
		throw new InvalidObjectException("Proxy required");
	}
	
	@Override
	public String toString() {
		String[] a = new String[size()];
		toString(root, a, 0);
		String returnString = "";
		for (String s : a) {
			returnString += s;
		}
		return returnString;
		
	}

	private void toString(Node h, String[] returnString, int i) {
		if (h == null) return;
		returnString[i] = h.val.toString() + " - ";
		toString(h.left, returnString, ++i);
		toString(h.right, returnString, ++i);
		
	}
}
