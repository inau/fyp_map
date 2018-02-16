package model.tree;

import java.io.Serializable;

/**
 * This interface was developed for keys used in KDTree.
 * 
 * @author ejer
 *
 * @param <E>
 */
public interface KDTComparable<E> extends Serializable{
	
	/**
	 * The value corresponding to dimension
	 * @param dimension
	 * @return	a value
	 */
	public double getDimensionValue(int dimension);
	
	/**
	 * Compares that object with this object with respect to the same dimension value. 
	 * This method is similar to the compareTo(E o) method of the interface Comparable<E>
	 * 
	 * @param o that object
	 * @param dimension 
	 * @return 1 if this object's value is larger than that object's value in dimension. 
	 * 0 if value is equal, 
	 * -1 if this object's value is lower than that object's value in dimension
	 */
	public int compareInDimension(E o, int dimension);
	
	/**
	 * 
	 * @return The number of dimensions
	 */
	public int getDimensions();
}
