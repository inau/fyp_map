package controller.threads;

import java.util.concurrent.CountDownLatch;

import model.Search;
import controller.Controller;
import static view.loading.LoadingType.*;

public class LoadingPercentUpdater implements Runnable {

	private double loadingPercent = 0;
	private Controller c;
	private CountDownLatch cdl;

	public LoadingPercentUpdater( Controller c , CountDownLatch cdl ) {
		this.c = c;
		this.cdl = cdl;
	}

	/**
	 * This run method makes the thread get the progress from the different loading instances
	 * It sets the loading progress in the view accordingly
	 */
	@Override
	public void run() {

		try {
			cdl.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		double kdtree = 0, tst = 0, graph = 0;

		while ( loadingPercent < 1 ) {

			if(tst == 0){
				kdtree = Search.Data.INSTANCE.getProgress();
				c.setLoadPercent(kdtree, KDTREE);
			}
			if(kdtree >= 1 && graph == 0){
				tst = Search.Text.INSTANCE.getProgress();
				c.setLoadPercent(tst, TST);
			}
			if(tst >= 1){
				graph = Search.Graph.INSTANCE.getProgress();
				c.setLoadPercent(graph, GRAPH);
			}

			// The average of all loadings. Is 1 if all are fully loaded.
			loadingPercent = ( kdtree + tst + graph) / 3;

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.err.println("INTERUPTED!");
			}
		}
	}
}
