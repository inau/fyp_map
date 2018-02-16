
	package controller;

	import model.data.Point;
import view.RouteEvent;

	/**
	 * Interface that any observer to the view must implement. The number of that require implementation
	 * have been narrowed down significantly, allowing the user to choose the level of action based on the events thrown.
	 * As of now, the methods return edges directly from the model as part of a MVP pattern, but this may still change.
	 * @author Stahl
	 *
	 */
	public interface CanvasObserver {
		
		/**
		 * A request to get all edges contained in whatever map's displayed.
		 * Also this should initialize whatever needs initialization.  
		 */
		void startMap();
		
		/**
		 * This method requests subparts of the map, ranging from upperleft coordinate to lower right.
		 * @param from The upperleft corner, also called northwest. 
		 * @param to The lowerleft corner, also called southeast.
		 * @param zoomLevel Integer from 0-6 where 0 is the lowest, filtering roads by priority.
		 */
		void requestEdges(Point from, Point to, int zoomLevel);
		
		/**
		 * A very generic method, this informs the observer that something with route planning should be executed.
		 * The details regarding the action is contained within the RouteEvent object. 
		 * @param e The specific event that should take place. For more information regarding the event, 
		 * please review the RouteEvent class.
		 */
		void planRoute(RouteEvent e);
		
		/**
		 * A method to request updating the static fields in the controller that contains route planning route information.
		 * This method shouldn't update the searchbox, however. This is an implementation specific method.
		 * @param e RouteEvent can be either ROUTESTART or ROUTEEND, depending on which box was updated.
		 */
		void setPlanField(RouteEvent e);

	
	}
	
