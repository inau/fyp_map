package controller.threads;

public interface Counter {

	/**
	 * Returns the count. The variable holding count isn't part of the interface,
	 * but it part of the implementation
	 * @return count
	 */
	public int count();
	
	/**
	 * Increments the count.
	 * @param count
	 */
	public void incrementCount(int count);
	
}
