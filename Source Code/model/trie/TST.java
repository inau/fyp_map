package model.trie;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.Search;
import model.TextSearch;

/** 
 * Our implementation of a Ternary Search Trie, mostly copied from Algs. 4th edition by Sedgewick & Wayne.
 * 
 * The TST is implemented as a modified binary search tree.
 * The TST is unconditionally thread-safe.
 * @author Morten & Stahl
 *
 * @param <V> The values mapped to the keys of the TST, which mustb e strings.
 */


public class TST<V> implements TextSearch<V>{

	private double triesize = 0;	
	private int N;       // size
	private Node root;   // root of TST

	private final Object stateLock = new Object(), accessorLock = new Object();

	private class Node{
		private char c;                 // character
		private Node left, mid, right;  // left, middle, and right subtries
		private V val;              // value associated with string
	}

	/**
	 * @return The number of key-value pairs
	 */
	public int size() {

		synchronized(accessorLock){
			return N;	
		}        
	}

	/**
	 * @return True if key in the symbol table. False if not
	 */
	public boolean contains(String key) {
		return get(key) != null;
	}

	/**
	 * Find the value V of key if one such exists
	 * @param key
	 * @return V
	 */
	public V get(String key) {
		if (key == null || key.length() == 0) throw new RuntimeException("illegal key");

		synchronized(stateLock){
			Node x = get(root, key, 0);
			if (x == null) return null;
			return (V) x.val;
		}
	}

	// return subtrie corresponding to given key
	private Node get(Node x, String key, int d) {
		if (key == null || key.length() == 0) throw new RuntimeException("illegal key");
		if (x == null) return null;

		synchronized(stateLock){
			char c = key.charAt(d);
			if      (c < x.c)              return get(x.left,  key, d);
			else if (c > x.c)              return get(x.right, key, d);
			else if (d < key.length() - 1) return get(x.mid,   key, d+1);
			else                           return x;
		}
	}


	/**
	 * Insert string s into the symbol table.
	 */
	public void put(String s, V val) {
		if(s == null)	throw new NullPointerException("parameter s was null");
		
		synchronized(stateLock){
			root = put(root, s, val, 0);
		}
	}

	/**
	 * Our private insertion method.
	 * @param x The node we're currently examining
	 * @param s The key we want to insert
	 * @param val The value for insertion
	 * @param d 
	 * @return
	 */
	private Node put(Node x, String s, V val, int d) {

		synchronized(stateLock){
			char c = s.charAt(d);
			if (x == null) {
				x = new Node();
				x.c = c;
			}
			if      (c < x.c)             x.left  = put(x.left,  s, val, d);
			else if (c > x.c)             x.right = put(x.right, s, val, d);
			else if (d < s.length() - 1)  x.mid   = put(x.mid,   s, val, d+1);
			else{                          
				x.val   = val;
				N++;
			}
			return x;
		}
	}

	/**
	 * Find and return longest prefix of s in TST
	 * @param s
	 * @return the longest prefix
	 */
	public String longestPrefixOf(String s) {
		if (s == null || s.length() == 0) return null;
		int length = 0;

		synchronized(stateLock){
			Node x = root;
			int i = 0;
			while (x != null && i < s.length()) {
				char c = s.charAt(i);
				if      (c < x.c) x = x.left;
				else if (c > x.c) x = x.right;
				else {
					i++;
					if (x.val != null) length = i;
					x = x.mid;
				}
			}
			return s.substring(0, length);
		}
	}

	/**
	 * @return  all keys in symbol table
	 */
	public Iterable<String> keys() {
		List<String> list = new LinkedList<String>();
		Node snapshotRoot = null;
		synchronized(stateLock){
			snapshotRoot = root;
		}
		collect(snapshotRoot, "", list);
		return list;
	}

	/**
	 * Find all keys starting with prefix
	 * @param prefix
	 * @return  all keys starting with given prefix
	 */
	public Iterable<String> prefixMatch(String prefix) {
		if(prefix == null)	throw new NullPointerException("parameter prefix was null");
		
		List<String> list = new LinkedList<String>();
		Node x = get(root, prefix, 0);
		if (x == null) return list;
		if (x.val != null) list.add(prefix);
		Node snapshotRoot = null;

		synchronized(stateLock){
			snapshotRoot = x.mid;
		}
		collect(snapshotRoot, prefix, list);
		return list;

	}

	// all keys in subtrie rooted at x with given prefix
	private void collect(Node x, String prefix, List<String> list) {
		if (x == null) return;
		collect(x.left,  prefix,       list);
		if (x.val != null) list.add(prefix + x.c);
		collect(x.mid,   prefix + x.c, list);
		collect(x.right, prefix,       list);
	}


	/**
	 * Find all keys matching given wilcard pattern
	 * @param pattern
	 * @return all keys matching given wilcard pattern
	 */
	public Iterable<String> wildcardMatch(String pattern) {
		if(pattern == null)	throw new NullPointerException("parameter pattern was null");
		
		List<String> list = new LinkedList<String>();
		synchronized(stateLock){
			collect(root, "", 0, pattern, list);		// Lock the entire method, since the method depends on mutable local variables
			return list;
		}		
	}

	/**
	 * Collects keys that match the prefix and the temporary pattern
	 * @param x The node we're currently at
	 * @param prefix THe prefix we're originally searching for
	 * @param i The number of the node we're going to.
	 * @param pat The temporary pattern
	 * @param list A list containing the nodes collected so far.
	 */
	private void collect(Node x, String prefix, int i, String pat, List<String> list) {
		if (x == null) return;

		synchronized(stateLock){
			char c = pat.charAt(i);
			if (c == '.' || c < x.c) collect(x.left, prefix, i, pat, list);
			if (c == '.' || c == x.c) {
				if (i == pat.length() - 1 && x.val != null) list.add(prefix + x.c);
				if (i < pat.length() - 1) collect(x.mid, prefix + x.c, i+1, pat, list);
			}
			if (c == '.' || c > x.c) collect(x.right, prefix, i, pat, list);
		}
	}

	/**
	 * Builds a TST from a Map of Strings and values.
	 * Search.Data interface implementation specific, basically just iterates over the map.
	 */
	public void build(Map<String, V> map){
		if(map == null)	throw new NullPointerException("parameter map was null");
		
		triesize = map.keySet().size();
		for(String name : map.keySet()){
			if(name.length() != 0){
				put(name, map.get(name));
				Search.Text.INSTANCE.setProgress((size()+1) / triesize);
			}
		}
	}
}