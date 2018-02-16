package model;

import java.util.List;
import model.tree.KDTComparable;

public class QuickKDTSort extends QuickSort{

	// quicksort the array
	public static <T extends KDTComparable<T>> void sort(List<T> a, int from, int to, int dimension) {
		if(a == null)			throw new NullPointerException("parameter a was null");
		else if(from < 0)		throw new IllegalArgumentException("parameter from was negative");
		else if(to < 0)			throw new IllegalArgumentException("parameter to was negative");
		else if(dimension < 0)	throw new IllegalArgumentException("parameter dimension was negative");
		
		if(from >= to) return;
		shuffle(a, from, to);
		sortRecursive(a, from, to, dimension);
	}

	// quicksort the subarray from a[lo] to a[hi]
	private static <T extends KDTComparable<T>> void sortRecursive(List<T> a, int lo, int hi, int dimension) { 
		if (hi <= lo) return;
		int j = partition(a, lo, hi, dimension);
		sortRecursive(a, lo, j-1, dimension);
		sortRecursive(a, j+1, hi, dimension);
		assert isSorted(a, lo, hi, dimension);
	}

	// partition the subarray a[lo .. hi] by returning an index j
	// so that a[lo .. j-1] <= a[j] <= a[j+1 .. hi]
	private static <T extends KDTComparable<T>> int partition(List<T> a, int lo, int hi, int dimension) {
		int i = lo;
		int j = hi + 1;
		T v = a.get(lo);

		while (true) { 

			// find item on lo to swap
			while (less(a.get(++i), v, dimension))
				if (i == hi) break;

			// find item on hi to swap
			while (less(v, a.get(--j), dimension))
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

	private static <T extends KDTComparable<T>> boolean less(T v, T w, int dimension) {
		return v.compareInDimension(w, dimension) < 0;
	}
	
	public static <T extends KDTComparable<T>> boolean isSorted(List<T> a, int dimension) {
		if(a == null)			throw new NullPointerException("parameter a was null");
		else if(dimension < 0)	throw new IllegalArgumentException("parameter dimension was negative");
		
		return isSorted(a, 0, a.size() - 1, dimension);
	}

	public static <T extends KDTComparable<T>> boolean isSorted(List<T> a, int lo, int hi, int dimension) {
		if(a == null)			throw new NullPointerException("parameter a was null");
		else if(lo < 0)	throw new IllegalArgumentException("parameter lo was negative");
		else if(hi < 0)	throw new IllegalArgumentException("parameter hi was negative");
		else if(dimension < 0)	throw new IllegalArgumentException("parameter dimension was negative");
		
		for (int i = lo + 1; i <= hi; i++)
			if (less(a.get(i), a.get(i-1), dimension)) return false;
		return true;
	}
}
