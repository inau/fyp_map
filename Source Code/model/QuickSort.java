package model;

import java.util.List;
import java.util.Random;

/**
 * Quick sort class. Imported from Algs 4th. edition. 
 * And modified a little.
 * Contains recursive quicksort methods. 
 * @author Stahl
 *
 */
public class QuickSort{

	// quicksort the array
	public static <T extends Comparable<T>> void sort(List<T> a, int from, int to) {
		if(a == null)			throw new NullPointerException("parameter a was null");
		else if(from < 0)		throw new IllegalArgumentException("parameter from was negative");
		else if(to < 0)			throw new IllegalArgumentException("parameter to was negative");
		
		shuffle(a, from, to);
		sortRecursive(a, from, to);
	}

	// quicksort the subarray from a[lo] to a[hi]
	private static <T extends Comparable<T>> void sortRecursive(List<T> a, int lo, int hi) { 
		if (hi <= lo) return;
		int j = partition(a, lo, hi);
		sortRecursive(a, lo, j-1);
		sortRecursive(a, j+1, hi);
		assert isSorted(a, lo, hi);
	}

	// partition the subarray a[lo .. hi] by returning an index j
	// so that a[lo .. j-1] <= a[j] <= a[j+1 .. hi]
	private static <T extends Comparable<T>> int partition(List<T> a, int lo, int hi) {
		int i = lo;
		int j = hi + 1;
		T v = a.get(lo);

		while (true) { 

			// find item on lo to swap
			while (less(a.get(++i), v))
				if (i == hi) break;

			// find item on hi to swap
			while (less(v, a.get(--j)))
				if (j == lo) break;      // redundant since a[lo] acts as sentinel

			// check if pointers cross
			if (i >= j) break;

			exch(a, i, j);
		}


		// put v = a[j] into position
		exch(a, lo, j);

		// with a[lo .. j-1] <= a[j] <= a[j+1 .. hi]
		return j;
	}


	/***********************************************************************
	 *  Helper sorting functions
	 ***********************************************************************/

	// is v < w ?
	private static <T extends Comparable<T>> boolean less(T v, T w) {
		return v.compareTo(w) < 0;
	}

	// exchange a[i] and a[j]
	protected static void exch(List<?> a, int i, int j) {
		if(a == null)		throw new NullPointerException("parameter a was null");
		else if(i < 0)		throw new IllegalArgumentException("parameter i was negative");
		else if(j < 0)		throw new IllegalArgumentException("parameter j was negative");
		
		swapHelper(a, i, j);
	}

	protected static <T> void swapHelper(List<T> a, int i, int j){
		if(a == null)		throw new NullPointerException("parameter a was null");
		else if(i < 0)		throw new IllegalArgumentException("parameter i was negative");
		else if(j < 0)		throw new IllegalArgumentException("parameter j was negative");
		
		T objectAtI = a.set(i, a.get(j));
		T objectAtJ = a.set(j, objectAtI);
		assert objectAtI.equals(a.get(j));
		assert objectAtJ.equals(a.get(i));
	}

	/***********************************************************************
	 *  Check if array is sorted - useful for debugging
	 ***********************************************************************/
	public static <T extends Comparable<T>> boolean isSorted(List<T> a) {
		if(a == null)		throw new NullPointerException("parameter a was null");
		
		return isSorted(a, 0, a.size() - 1);
	}

	private static <T extends Comparable<T>> boolean isSorted(List<T> a, int lo, int hi) {
		if(a == null)		throw new NullPointerException("parameter a was null");
		else if(lo < 0)		throw new IllegalArgumentException("parameter lo was negative");
		else if(hi < 0)		throw new IllegalArgumentException("parameter hi was negative");
		
		for (int i = lo + 1; i <= hi; i++)
			if (less(a.get(i),a.get(i-1))) return false;
		return true;
	}
	
	/**
     * Rearrange the elements of the subarray a[lo..hi] in random order.
     */
    public static <T extends Object> void shuffle(List<T> a, int lo, int hi) {
    	if(a == null)		throw new NullPointerException("parameter a was null");
		else if(lo < 0)		throw new IllegalArgumentException("parameter lo was negative");
		else if(hi < 0)		throw new IllegalArgumentException("parameter hi was negative");
  
    	if (lo > hi || hi >= a.size())	throw new RuntimeException("Illegal subarray range");
    	
    	Random random = new Random();
    	for (int i = lo; i <= hi; i++) {
            int r = i + random.nextInt(hi-i+1);     // between i and hi
            exch(a, i, r);
        }
    }
}