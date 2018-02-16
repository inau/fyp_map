package view;

/**
 * The EventType enum is a custom defined event type for use in the View class.
 * When an observer wishes to subscribe to the View, one should consult this
 * Enum to see what actions could be performed in the View. Implementing the CanvasObserver
 * makes sure the API is correct for the observer; but paying attention to the EventType
 * of the ObserverEvent is the only way to secure a correct implementation.
 * @author Stahl
 *
 */

public enum EventType {
	
	
	// The route planning events is defined here. The view supports 2 entries - start and end.
	// And the ROUTESEARCH to indicate the user is done with the input and wants the result.
	ROUTESTART, ROUTEEND, ROUTESEARCH,
		
	// This is a generic request designed to update all states of the controller/model.
	UPDATE,
	// This is a default EventType to avoid null types. This will be the default enum type.
	NONE;
	
}
