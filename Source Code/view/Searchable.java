package view;

/**
 * Any top-level container that implements the SearchBox.java use a class that implements this interface 
 * to handle user input registered in the SearchBox. The container should also implement methods that update
 * the SearchBox with new ComboBoxModels containing the retrieved info.
 * @author Stahl
 *
 */
public interface Searchable {

	/**
	 * This method is called every time the SearchBox receives input from the user.
	 * The search method should request new items to display in the drop down part of the SearchBox.
	 * @param e a RouteEvent containing search information and the SearchBoxs type (start field or end field).
	 */
	public void search(RouteEvent e);

	/**
	 * This method is called every time the SearchBox receives input from the user, but only 
	 * when it's not appropriate to update the items displayed. 
	 * @param e RouteEvent containing the searchbox's current state and the SearchBox's type.
	 */
	public void readOnly(RouteEvent e);
	
}
