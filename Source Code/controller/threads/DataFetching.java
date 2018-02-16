package controller.threads;

import java.util.concurrent.CountDownLatch;

import model.Search;
import controller.Controller;

public class DataFetching implements Runnable {

	private Controller c;
	private CountDownLatch cdl;
	
	public DataFetching( Controller c , CountDownLatch cdl ) {
	
		this.c = c;
		this.cdl = cdl;
	}
	
	@Override
	public void run() {

		cdl.countDown();
		Search.Data.INSTANCE.initialize();
		
		Search.Text.INSTANCE.initialize();
		
		Search.Graph.INSTANCE.initialize();
		
		c.setViewport();
		
		

	}

}
