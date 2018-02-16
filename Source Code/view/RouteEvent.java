package view;

/**
 * This class is designed to be a custom event passable from the View to the Controller.
 * The objects created contains information telling the container what to do.
 * The view part can use either the enums declared in the class RouteType or a String that tells the 
 * Controller what address the user wants to search for.
 * @author Stahl
 *
 */
public class RouteEvent {

	private final RouteType eventType;
	private final String searchString;
	
	/**
	 * Enums explaining action in the View. The names of the enums describes what action the user 
	 * have requested.
	 * @author Stahl
	 *
	 */
	public enum RouteType {
		
		// Route type enum
		ROUTESTART, ROUTEEND, ROUTESEARCH,
		
		// Implementation specific enum, used when the user has specified this is his final path
		
		
		// And a few to define which transport type the user wants to get shortest path to.
		BICYCLEONLY, VEHICLEONLY,
		// This is a default RouteType to avoid null types. This will be the default enum type.
		NONE;
		}
		
	
	public RouteEvent()
	{
		this("NOSEARCHSTRINGATTACHED", RouteType.NONE);
	}
	

	public RouteEvent(RouteType e)
	{
		this("NOSEARCHSTRINGATTACHED", e);
	}
	
	public RouteEvent(String s,RouteType e)
	{
		eventType = e;
		searchString = s;
	}
	
	public RouteEvent(String s)
	{
		this(s,RouteType.NONE);
	}
	
	public RouteType getRouteType()
	{
		return eventType;
	}
	
	public String getSearchString()
	{
		return searchString;
	}
}
