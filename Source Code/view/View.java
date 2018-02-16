package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import model.data.Edge;
import model.data.Point;
import view.RouteEvent.RouteType;
import view.loading.LoadingType;
import controller.CanvasObserver;

/*
 * Main GUI class. Contains whatever class wants to observe and update its data. Also contains all component and extends
 * the JFrame used to build the window. The main class responsible  
 */
public class View extends JFrame implements Searchable {

	LinkedList<CanvasObserver> observers; // The observer must implement the CanvasObserver interface.
	MapCanvas mapCanvas;
	JPanel content;
	SearchBox startRoute;
	SearchBox endRoute;
	private final double zoomPerOut = 0.05;
	private double zoomPerIn;
	private double prevWidth, prevHeight;
	
	public View()
	{

		// Main initialization of components
		initMenu();
		initContent();

		observers = new LinkedList<CanvasObserver>();

		// Some standard settings
		setSize(750,750);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
	}

	public void addObserver(CanvasObserver obs)
	{
		observers.add(obs);
	}

	/**
	 * This method is HUGE, but if you know the basics of Swing, it should be easy to browse.
	 * It goes as follows: First, all the components are initialized and their settings are customized.
	 * Next, the listeners to the buttons are added through a huge chunk of code. Finally, all the components
	 * are added to the master component, the JPanel called content.
	 */
	private final void initContent()
	{
		// Initialize outer components.
		content = new JPanel(new BorderLayout());
		mapCanvas = new MapCanvas();

		JPanel searchMenu = new JPanel(new GridBagLayout());

		JPanel routeMenu = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JPanel zoomMenu = new JPanel(new FlowLayout());

		JButton zoomOut = new JButton("Zoom out");
		JButton zoomIn = new JButton("Zoom in");
		JButton zoomReset = new JButton("Zoom Reset");
		JButton startMap = new JButton("Start map");

		JTextField addressField = new JTextField();
		addressField.setPreferredSize(new Dimension(80,40));

		final String[] initialStart = { "Enter your start destination!" };
		final String[] initialEnd = { "Enter your end destination!" };

		DefaultComboBoxModel dcb = new DefaultComboBoxModel(initialStart);
		startRoute = new SearchBox(dcb,this, RouteType.ROUTESTART);

		dcb = new DefaultComboBoxModel(initialEnd);
		endRoute = new SearchBox(dcb,this, RouteType.ROUTEEND);

		JButton routeGo = new JButton("Find route!");

		// Window listener ( listens to the window to check if it changes and then requests edges )
		this.addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				requestEdges();
			}
		});

		//mapCanvas scroll zoom and drag pan
		mapCanvas.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(e.getWheelRotation() > 0) mapCanvas.zoomPercent(zoomPerOut);
				else						 mapCanvas.zoomPercent(zoomPerIn);
				requestEdges();
			}
		});

		mapCanvas.addMouseMotionListener(new MouseMotionListener() {

			int prevX = 0;
			int prevY = 0;

			@Override
			public void mouseMoved(MouseEvent e) {
				prevX = e.getX();
				prevY = e.getY();				
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if 		(e.getX() != prevX) 	mapCanvas.panHorPix(prevX - e.getX());
				if 		(e.getY() != prevY)		mapCanvas.panVerPix(e.getY() - prevY);
				prevX = e.getX();
				prevY = e.getY();

				requestEdges();

			}
		});

		// Zoom buttons
		zoomOut.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				mapCanvas.zoomPercent(zoomPerOut);
				requestEdges();
			}
		});

		zoomIn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				mapCanvas.zoomPercent(zoomPerIn);
				requestEdges();
			}
		});

		zoomReset.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				mapCanvas.reset();
				requestEdges();
			}
		});

		// Zoom buttons finished -----------------------------------------------
		// Route buttons starting -------------------------------------------------
		routeGo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				search(new RouteEvent(RouteType.ROUTESEARCH));
			}
		});

		// Route buttons finished ---------------------------------------------------------------------------------
		// Other things
		addressField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Something with searching map here
			}
		});

		startMap.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Reset the map, so that we are requesting the full view again
				mapCanvas.reset();
				
				// Calculate zoomPerIn
				Viewport vp = mapCanvas.viewport();
				zoomPerIn = ( vp.distV0V1() - vp.distV0V1() * (1 + zoomPerOut) ) / (vp.distV0V1() * (1 + zoomPerOut) );
				
				requestEdges();
			}
		});

		// Initialize constraints to the gridbagmenu.
		GridBagConstraints c = new GridBagConstraints();

		// Put content together and orderlayouts
		content.add(mapCanvas, BorderLayout.CENTER);
		content.add(routeMenu,BorderLayout.NORTH);
		content.add(zoomMenu, BorderLayout.SOUTH);

		zoomMenu.add(zoomOut);
		zoomMenu.add(zoomIn);
		zoomMenu.add(zoomReset);
		zoomMenu.add(startMap);

		// GridbagConstraint editing.
		c.fill = c.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		searchMenu.add(addressField,c);

		routeMenu.add(startRoute);
		routeMenu.add(endRoute);
		routeMenu.add(routeGo);

		// After much work, we can add it all to the content pane and die happily!
		setContentPane(content);

		startRoute.addCustomListeners();
		endRoute.addCustomListeners();
	}



	public void updateStartSearch(Iterable<String> results, String prefix)
	{
		updateSearchField(results,prefix,startRoute);
	}

	public void updateEndSearch(Iterable<String> results, String prefix)
	{
		updateSearchField(results,prefix,endRoute);
	}

	private void updateSearchField(final Iterable<String> results, final String prefix,final SearchBox box)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{	
				DefaultComboBoxModel dbc = new DefaultComboBoxModel();
				dbc.addElement(prefix);
				for(String s : results) 
					if(s.equals(prefix)) continue;
					else dbc.addElement(s);

				box.setModel(dbc);
				box.setPopupVisible(true);
			}
		});
	}

	public void resetStartSearch()
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				startRoute.removeAllItems();
			}});
	}

	public void resetEndSearch()
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				endRoute.removeAllItems();
			}});
	}



	public void search(RouteEvent evt)
	{
		if(evt == null || observers == null) return;
		for(CanvasObserver obs : observers)
		{
			obs.planRoute(evt);
		}
	}


	/**
	 * Initiates the top menu.
	 * Not much to be done here right now, maybe this method should contain the menu content from the initContent()
	 */
	private void initMenu()
	{
		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem itemquit = new JMenuItem("Quit");
		itemquit.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}			
		});
		file.add(itemquit);
		menubar.add(file);
		setJMenuBar(menubar);
		setTitle("MapTastic BETA");

	}
	
	// Set MapCanvas' loadpercent
	public void setLoadPercent(double percent, LoadingType type) {
		mapCanvas.setLoadPercent(percent, type);
		mapCanvas.repaint();
	}

	// Create the viewport
	public void setViewport() {
		mapCanvas.setViewport();
	}

	// Update the MapCanvas' edges with the given ones
	public void updateEdges(Set<Edge> unCovertedEdges) {
		mapCanvas.updateEdges(unCovertedEdges);
	}
	
	// Update the searched route's edges
	public void updateRoute(Edge[] unConvertedEdges) {
		mapCanvas.setRoute(unConvertedEdges);
	}

	// Requests edges from the controller, which in turn updates the edges without return values
	private void requestEdges() {
		// Get the viewport information
		Viewport vp = mapCanvas.viewport();
		if ( vp != null ) {
			Point v0		= vp.v0();
			Point v1 		= vp.v1();
			int zoomLevel 	= vp.zoomLevel();

			// This fix allows for panning into negative values without giving mistakes.
			double v0XReq;
			double v0YReq;
			if ( v0.getX() < 0 )
				v0XReq = 0;
			else
				v0XReq = v0.getX();
			if ( v0.getY() < 0 )
				v0YReq = 0;
			else
				v0YReq = v0.getY();

			// Request edges on each observer
			for ( CanvasObserver co : observers )
				co.requestEdges(new Point( 0 , v0XReq , v0YReq ) , new Point( 0 , v1.getX() , v1.getY() ), zoomLevel );

		}
	}

	@Override
	public void readOnly(RouteEvent e) {
		for(CanvasObserver obs : observers )
		{
			obs.setPlanField(e);
		}
		
	}

}
