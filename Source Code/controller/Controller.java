package controller;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.Search;
import model.Stopwatch;
import model.data.Edge;
import model.data.Point;
import model.graph.Route;
import view.RouteEvent;
import view.View;
import view.loading.LoadingType;
import controller.threads.DataFetching;
import controller.threads.LoadingPercentUpdater;


public class Controller implements CanvasObserver {

	private String[] startSearch, endSearch;
	private View view;

	public static void main(String[] args) {

		// Create the view
		View v = new View();

		// Create the Controller
		Controller c = new Controller(v);

		// Creates an executor service with a thread pool with two threads
		ExecutorService executor = Executors.newCachedThreadPool();
		
		CountDownLatch dfReady = new CountDownLatch(1);
		
		// Sets two runnables in queue and shuts down the executor service, when those are finished
		executor.execute(new DataFetching(c,dfReady));
		executor.execute(new LoadingPercentUpdater(c,dfReady));
		executor.shutdown();
		
	}

	// Set the loadPercent so that a loading bar can be drawn/shown
	public void setLoadPercent(double percent, LoadingType type) {
		view.setLoadPercent(percent, type);
	}

	// Create a viewport
	public void setViewport() {
		view.setViewport();
	}

	public Controller(View v)
	{
		view = v;
		v.addObserver(this);
		startSearch = new String[] { "","","","" };
		endSearch = new String[] { "","","","" };
	}

	/**
	 * This method updates the view with all edges in the data structure.
	 */
	@Override
	public void startMap() {
		view.updateEdges( Search.Data.INSTANCE.getAll() );
	}

	
	/**
	 * This method requests edges in a range from Point from to Point to,
	 * with the zoomlevel passed as parameter. It then updates the edges with
	 * the received edges.
	 */
	@Override
	public void requestEdges(Point from, Point to, int zoomLevel) {
		view.updateEdges( Search.Data.INSTANCE.getRange(from, to,zoomLevel) );
	}

	/**
	 * The controllers main method for planning routes. 
	 * @param e Determines what action should be done. 
	 * 			ROUTESTART updates the start field in the view with suitable addresses from the TST
	 * 			ROUTEEND updates the end field in the view with suitable addresses from the TST
	 * 			ROUTEGO calls the route-planning algorithm and returns the edges marked by the algorithm to the view
	 * 			for it to display.
	 */
	@Override
	public void planRoute(RouteEvent e) {
		String prefix = e.getSearchString();
		String[] tmp = AddressParser.parseAddress(prefix.trim());
		String search = tmp[0]; // Address name from parsing method

		if(search == null || search.length() < 2) return;

		Iterable<String> results = Search.Text.INSTANCE.prefixAndPostal(search); // Get addresses to show in the view

		switch(e.getRouteType()) {
		case ROUTESTART:  
			view.updateStartSearch(results,prefix); // Update start field
			startSearch = tmp; // Save string in field for route planning.
			break;
		case ROUTEEND:
			view.updateEndSearch(results,prefix); 
			endSearch = tmp;
			break;
		case ROUTESEARCH:
			Set<Edge> from = Search.Text.INSTANCE.get(startSearch);
			Set<Edge> to = Search.Text.INSTANCE.get(endSearch);

			// Iterate through all possible edges to find an arbitrary shortest route.
			Edge fromEdge = null;
			Edge toEdge = null;
			for(Edge edgeone : from)
			{
				fromEdge = edgeone;
				for(Edge edgetwo : to)
				{
					toEdge = edgetwo;
					if(fromEdge != null && toEdge != null) { 
							Route r = Search.Graph.INSTANCE.searchRoutes(fromEdge, toEdge); // Search the Graph for a shortest route.
							if(r.geted().size() > 1) {
								view.updateRoute(r.getVisualRoute()); // Update with edges that are to display as route.
								return;
							}
						}
					}

				}
			default: break;	
			}
		}

		/**
		 * This method is used to update the addess used for searching routes without doing anything else.
		 * It's designed so the user can type input without the gui responding actively.
		 * @param e RouteEvent can be ROUTESTART if its the from address or ROUTEEND if the end address sould be updated.	
		 */
		@Override
		public void setPlanField(RouteEvent e) {

			switch(e.getRouteType()) {
			case ROUTESTART:
				startSearch = AddressParser.parseAddress(e.getSearchString().trim());
				break;
			case ROUTEEND:
				endSearch = AddressParser.parseAddress(e.getSearchString());
				break;
			default: break;

			}
		}
	}
