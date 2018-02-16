package controller.threads;

public class CounterThread implements Runnable {

	private Counter c;
	private boolean run = true;

	public CounterThread(Counter c) {
		this.c = c;
	}

	/**
	 * This run method increments a variable implemented, because of the Counter interface
	 */
	@Override
	public void run() {
		while ( run ) {
			c.incrementCount(c.count());
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				System.err.println("INTERRUPTED!");
			}
		}
	}
	
	/**
	 * Makes the thread stop safely
	 */
	public synchronized void stopRun() {
		run = false;
	}

}
