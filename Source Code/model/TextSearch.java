package model;

import java.util.Map;

/**
 * This interface needs to be implemented by any class representing the 'text' functionality as it is defined in the report.
 * 
 * @param <V> The value contained in the data structure
 */
public interface TextSearch<V>{
	
	/**
	 * 
	 * @return the number of key-value pairs
	 */
	public int size();
	
	/**
	 * @return True if key in the symbol table. False if not
	 */
	public boolean contains(String key);
	
	/**
	 * Find the value V of key if one such exists
	 * @param key
	 * @return V
	 */
	public V get(String key);
	
	/**
	 * Insert string s into the data structure.
	 */
	public void put(String key, V value);
	
	/**
	 * Recursively build the data structure 
	 * @param map containing the key-value pairs to be included in the datastructure
	 */
	public void build(Map<String, V> map);
	
	/**
	 * return an iterable object containing all keys containing prefix
	 * @param prefix
	 * @return  all keys in symbol table
	 */
	public Iterable<String> prefixMatch(String prefix);
}
