package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import view.loading.LoadingType;
import controller.threads.Counter;
import controller.threads.CounterThread;
import model.Search;
import model.data.Edge;
import model.data.Point;


@SuppressWarnings("serial")
public class MapCanvas extends Component implements Counter {

	private Set<Edge> setEdges = new HashSet<Edge>();
	private Edge[] originalRouteEdges = new Edge[0];
	private Edge[] routeEdges = new Edge[0];
	Viewport vp;
	private double loadTotal = 0; // For making a loading bar
	private double loadKDTree = 0, loadTST = 0, loadGraph = 0;
	private int count = 0;
	private CounterThread counter = new CounterThread(this);
	private ExecutorService executor = Executors.newSingleThreadExecutor();

	public MapCanvas()
	{
		super();

		// A mouse listener, which listens for mouse clicks on the map
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if ( vp != null ) {
					System.out.println("MapCanvas coord: (" + e.getX() + "," + e.getY() + ")");
					System.out.println("True coord:      (" + vp.mapCanToTrueHor(e.getX()) + "," + vp.mapCanToTrueVer(e.getY()) + ")");
				}
			}
		});

		// Execute the counter runnable and shut down the pool, when finished
		executor.execute(counter);
		executor.shutdown();
		
	}

	/**
	 * Sets the loading percent done of a certain loading type
	 * @param percent The percent of loading done
	 * @param type The loading type
	 */
	public void setLoadPercent(double percent, LoadingType type) {
		switch ( type ) {
		case KDTREE: 	loadKDTree 	= percent; break;
		case TST: 		loadTST 	= percent; break;
		case GRAPH: 	loadGraph 	= percent; break;
		}

		loadTotal = (loadKDTree + loadTST + loadGraph) / LoadingType.values().length;

		repaint();
	}

	/**
	 * Creates a ViewPort if none exists
	 */
	public void setViewport() {
		if ( vp == null ) {
			Point m0 = Search.Coordinate.INSTANCE.getMinPoint();
			Point m1 = Search.Coordinate.INSTANCE.getMaxPoint();
			vp = new Viewport(this, m0, m1, m0, m1);
		}
		repaint();
	}

	@Override
	public void paint(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// If there is no viewport, we need to show the loading bar
		if ( vp == null ) {

			int width = 50;
			int height = 50;

			// Draw circle loading background
			g2d.setColor(Color.GRAY);
			g2d.fillArc(getWidth() / 2 - (width / 2), getHeight() / 2 - (height / 2), width, height, 0, (int) (360) );

			// Draw the amount of loading done
			g2d.setColor(new Color(79,216,0)); // GREEN
			g2d.fillArc(getWidth() / 2 - (width / 2), getHeight() / 2 - (height / 2), width, height, 0, (int) ((360 / LoadingType.values().length) * loadKDTree));
			if(loadKDTree >= 1){
				g2d.setColor(Color.RED);
				g2d.fillArc(getWidth() / 2 - (width / 2), getHeight() / 2 - (height / 2), width, height, (int) ((360 / LoadingType.values().length) * loadKDTree), (int) ((360 / LoadingType.values().length) * loadTST));
				if(loadTST >= 1){
					g2d.setColor(Color.YELLOW);
					g2d.fillArc(getWidth() / 2 - (width / 2), getHeight() / 2 - (height / 2), width, height, (int) ((360 / LoadingType.values().length) * loadKDTree) + (int) ((360 / LoadingType.values().length) * loadTST), (int) ((360 / LoadingType.values().length) * loadGraph));
				}
			}

			// Draw the black arc around it all
			g2d.setColor(Color.BLACK);
			g2d.drawArc(getWidth() / 2 - (width / 2), getHeight() / 2 - (height / 2), width, height, 0, (int) (360) );

			// Draw the smaller inner arc turning around
			g2d.drawArc(getWidth() / 2 - ((width / 2) / 2), getHeight() / 2 - ((height / 2) / 2), width / 2, height / 2, count + 120 , 45);
			g2d.drawArc(getWidth() / 2 - ((width / 2) / 2), getHeight() / 2 - ((height / 2) / 2), width / 2, height / 2, count + 240 , 45);
			g2d.drawArc(getWidth() / 2 - ((width / 2) / 2), getHeight() / 2 - ((height / 2) / 2), width / 2, height / 2, count		 , 45);

		} else {

			counter.stopRun();

			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());

			for(Edge e : setEdges) {
				// All the drawing goes here.
				double linewidthMod = 0.85 + (vp.zoomLevel() - e.getRoadType().getZoomLevel())*0.05; if ( linewidthMod > 2.5 ) linewidthMod = 2.5;
				g2d.setColor(e.getRoadType().getColour());
				g2d.drawLine((int) e.getStart().getX() , (int) e.getStart().getY(), (int) e.getEnd().getX() , (int) e.getEnd().getY() );

			}

			// Draw the route
			for(Edge e : routeEdges) {

				double linewidthMod = 0.85 + (vp.zoomLevel() - e.getRoadType().getZoomLevel())*0.05; if ( linewidthMod > 2.5 ) linewidthMod = 2.5;
				g2d.setColor(Color.BLUE);
				g2d.drawLine((int) e.getStart().getX() , (int) e.getStart().getY(), (int) e.getEnd().getX() , (int) e.getEnd().getY() );

			}

		}

	}

	// Returns the angle needed to draw the text angled
	private double getAngle(Point p1, Point p2) {
		if ( p1.getX() < p2.getX() ) {
			if ( p1.getY() < p2.getY() ) {
				return Math.atan( ( p2.getY() - p1.getY() ) / ( p2.getX() - p1.getX() ) );
			} else {
				return Math.atan( (p1.getY() - p2.getY()) / ( p2.getX() - p1.getX() ) );
			}

		} else {
			if ( p1.getY() < p2.getY() ) {
				return Math.atan( ( p2.getY() - p1.getY() ) / ( p1.getX() - p2.getX() ) );
			} else {
				return Math.atan( ( p1.getY() - p2.getY() ) / ( p1.getX() - p2.getX() ) );
			}
		}
	}

	/**
	 * Zoom with a certain percent
	 * When zooming in the percent should be negative
	 * @param percent The percent to zoom
	 */
	public void zoomPercent(double percent) {
		if ( vp != null )
			vp.zoom(percent);
	}

	/**
	 * Resets the map
	 */
	public void reset() {
		if ( vp != null )
			vp.reset();
	}

	/**
	 * Pan horizontal by percent
	 * @param percent The percent to pan horizontally
	 */
	public void panHor(double percent) {
		if ( vp != null )
			vp.panHor(percent);
	}

	/**
	 * Pan horizontal by pixel
	 * @param pixel The number of pixels to pan horizontally
	 */
	public void panHorPix(int pixel) {
		if ( vp != null )
			vp.panHorPix(pixel);
	}

	/**
	 * Pan vertically by percent
	 * @param percent The percent to pan vertically
	 */
	public void panVer(double percent) {
		if ( vp != null )
			vp.panVer(percent);
	}

	/**
	 * Pan vertical by pixel
	 * @param pixel The number of pixels to pan vertically
	 */
	public void panVerPix(int pixel) {
		if ( vp != null )
			vp.panVerPix(pixel);
	}

	/**
	 * Returns the ViewPort
	 * @return viewport The MapCanvas' viewport
	 */
	public Viewport viewport() {
		if ( vp != null )
			return vp;
		else
			return null;
	}

	// Update the MapCanvas' edges with the given ones after conversion
	/**
	 * Sends the edges needed to be drawn for conversion in the viewport
	 * @param unConvertedEdges The set of edges to be converted with the correct coordinates
	 */
	public void updateEdges(Set<Edge> unConvertedEdges)
	{	
		if ( vp != null ) {
			setEdges = vp.getVisibleEdges(unConvertedEdges);
			updateRoute();
		}
	}

	/**
	 * Sends the edges needed to draw the current route for conversion in the viewport
	 * @param unConvertedEdges The array of edges to be converted with the correct coordinates
	 */
	public void setRoute(Edge[] unConvertedEdges) {
		if ( vp != null ) {
			originalRouteEdges = unConvertedEdges;
			routeEdges = vp.getVisibleEdges(originalRouteEdges);
		}
	}

	/**
	 * Updates the route
	 */
	public void updateRoute() {
		if ( vp != null && routeEdges.length > 0 )
			routeEdges = vp.getVisibleEdges(originalRouteEdges);
	}

	/**
	 * See the Count interface
	 */
	@Override
	public synchronized int count() {
		return count;
	}

	/**
	 * See the Count interface
	 */
	@Override
	public synchronized void incrementCount(int count) {
		this.count = count + 1;

	}

}
