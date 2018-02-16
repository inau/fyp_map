package model;

import java.util.Map;
import java.util.Set;

/**
 * This interface needs to be implemented by any class representing the 'data' functionality as it is defined in the report.
 * 
 * @param <K> The key of the data search structure
 * @param <V> The value associated with K 
 */
public interface DataSearch<K, V>{
	
	/**
	 * 
	 * @return the number of key-value pairs
	 */
	public int size();
	
	/**
	 * Adds key to the data structure with the associated value.
	 * @param searchkey
	 * @param val	Can be null.
	 */
	public void put(K key, V value);
	
	/**
	 * Return all values in the range key1 - key2
	 * @param key1
	 * @param key2
	 * @return Set containing all values in the range key1 - key2
	 */
	public Set<V> getRange(K key1, K key2);
	
	/**
	 * Recursively build the data structure 
	 * @param map containing the key-value pairs to be included in the datastructure
	 */
	public void build(Map<K, V> map);
	
}
