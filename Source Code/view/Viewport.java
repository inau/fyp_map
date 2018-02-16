package view;

import java.util.HashSet;
import java.util.Set;

import model.data.Edge;
import model.data.Point;

/**
 * Viewport class. Creates a "window" determining what should be viewed in the MapCanvas.
 * @author Ppho
 *
 */

public class Viewport {

	private MapCanvas c;
	private Point v0S, v1S, v0, v1, m0, m1;
	private int zoomLevel = 0;
	
	public Viewport(MapCanvas c, Point m0, Point m1, Point v0, Point v1) {
		this.c = c;
		this.m0 = m0;
		this.m1 = m1;
		this.v0S = this.v0 = v0;
		this.v1S = this.v1 = v1;
	}

	/**
	 * Gets a set of edges for conversion to edges with correct coordinates and returns them
	 * @param unConvertedEdges The set of edges to convert
	 * @return convertedEdges The set of converted edges
	 */
	public Set<Edge> getVisibleEdges(Set<Edge> unConvertedEdges) {

		Set<Edge> set = new HashSet<Edge>();

		for ( Edge e : unConvertedEdges ) {
			double sX = ( e.getStart().getX() - v0.getX() ) * c.getWidth() / ( width() );
			double sY = ( e.getStart().getY() - v0.getY()) * c.getHeight() / ( height() );
			double eX = ( e.getEnd().getX() - v0.getX() ) * c.getWidth() / ( width() );
			double eY = ( e.getEnd().getY() - v0.getY()) * c.getHeight() / ( height() );

			double sFY = flipYCoord(sY);
			double eFY = flipYCoord(eY);

			if ( v0.getX() < 0 ) {
				sX = sX + Math.abs( trueToMapCanHor(v0.getX()));
				eX = eX + Math.abs( trueToMapCanHor(v0.getX()));
			}
			if ( v0.getY() < 0 ) {
				sY = sY + Math.abs( trueToMapCanVer(v0.getY()));
				eY = eY + Math.abs( trueToMapCanVer(v0.getY()));
			}

			Edge newE = new Edge( new Point(0 , sX , sFY ) , new Point(0 , eX , eFY ) , e.getRoadName() , e.getRoadType() );
			set.add( newE );
		}

		return set;
	}

	/**
	 * Converts an array of edges to edges with correct coordinates
	 * @param unConvertedEdges The array of edges to convert
	 * @return convertedEdges An array of edges with correct coordinates
	 */
	public Edge[] getVisibleEdges(Edge[] unConvertedEdges) {

		Edge[] list = new Edge[unConvertedEdges.length];
		int count = 0;

		for ( Edge e : unConvertedEdges ) {
			double sX = ( e.getStart().getX() - v0.getX() ) * c.getWidth() / ( width() );
			double sY = ( e.getStart().getY() - v0.getY()) * c.getHeight() / ( height() );
			double eX = ( e.getEnd().getX() - v0.getX() ) * c.getWidth() / ( width() );
			double eY = ( e.getEnd().getY() - v0.getY()) * c.getHeight() / ( height() );

			double sFY = flipYCoord(sY);
			double eFY = flipYCoord(eY);

			if ( v0.getX() < 0 ) {
				sX = sX + Math.abs( trueToMapCanHor(v0.getX()));
				eX = eX + Math.abs( trueToMapCanHor(v0.getX()));
			}
			if ( v0.getY() < 0 ) {
				sY = sY + Math.abs( trueToMapCanVer(v0.getY()));
				eY = eY + Math.abs( trueToMapCanVer(v0.getY()));
			}

			Edge newE = new Edge( new Point(0 , sX , sFY ) , new Point(0 , eX , eFY ) , e.getRoadName() , e.getRoadType() );
			list[count++] = newE;
		}

		return list;
	}

	// Flips the Y coordinate according to MapCanvas coordinates
	private double flipYCoord(double coord) {
		return (coord * -1) + c.getHeight();
	}

	/**
	 * Zoom
	 * Zooming in uses a negative percentage
	 * @param percent The percentage to zoom
	 */
	public void zoom(double percent) {
		if ( atMaxZoomLvl(percent) ) {
			double ratio = height() / width();
			v0 = new Point( 0 , (v0.getX() - percent * distV0V1()) , (v0.getY() - percent * (distV0V1() * ratio) ) );
			v1 = new Point( 0 , (v1.getX() + percent * distV0V1()) , (v1.getY() + percent * (distV0V1() * ratio) ) );

			c.repaint();


			if ( percent > 0 )
				zoomLevel--;
			if ( percent < 0 )
				zoomLevel++;


		} else { System.err.println("Could not zoom"); }

	}

	private boolean atMaxZoomLvl(double percent) {
		return v1.getX() + percent * distV0V1() > v0.getX() && v1.getY() + percent * distV0V1() > v0.getY();
	}

	/**
	 * Panning horizontally by percent
	 * Panning left uses a negative percentage
	 * @param percent The percent to pan
	 */
	public void panHor(double percent) {
		v0 = new Point( 0 , (v0.getX() + percent * distV0V1()) , v0.getY() );
		v1 = new Point( 0 , (v1.getX() + percent * distV0V1()) , v1.getY() );

		c.repaint();
	}

	/**
	 * Panning horizontally by pixel
	 * Panning left uses a negative number of pixel
	 * @param pixel The number of pixels to pan
	 */
	public void panHorPix(int pixel) {
		double trueCord = mapCanToTrueHorPixel(pixel);
		v0 = new Point( 0 , (v0.getX() + trueCord) , v0.getY() );
		v1 = new Point( 0 , (v1.getX() + trueCord) , v1.getY() );

		c.repaint();
	}

	/**
	 * Panning vertically by percent
	 * Panning up uses a negative percentage
	 * @param percent The percentage to pan
	 */
	public void panVer(double percent) {
		v0 = new Point( 0 , v0.getX() , (v0.getY() + (percent * distV0V1())) );
		v1 = new Point( 0 , v1.getX() , (v1.getY() + (percent * distV0V1())) );

		c.repaint();
	}

	/**
	 * Panning vertically by pixels
	 * Panning up uses a negative number of pixels
	 * @param pixel The number of pixels to pan
	 */
	public void panVerPix(int pixel) {
		double trueCord = mapCanToTrueVerPixel(pixel);
		v0 = new Point( 0 , v0.getX() , (v0.getY() + trueCord) );
		v1 = new Point( 0 , v1.getX() , (v1.getY() + trueCord) );

		c.repaint();
	}

	/**
	 * Resets the map to its original state
	 */
	public void reset() {
		v0 = v0S;
		v1 = v1S;
		zoomLevel = 0;

		c.repaint();
	}

	/**
	 * 
	 * @return v0
	 */
	public Point v0() {
		return v0;
	}

	/**
	 * 
	 * @return v1
	 */
	public Point v1() {
		return v1;
	}
	
	/**
	 * 
	 * @return m0
	 */
	public Point m0() {
		return m0;
	}

	/**
	 * 
	 * @return m1
	 */
	public Point m1() {
		return m1;
	}

	/**
	 * 
	 * @return zoomLevel
	 */
	public int zoomLevel() {
		return zoomLevel;
	}

	/**
	 * Returns the width of the viewport
	 * @return viewportW Width of the viewport
	 */
	public double width() {
		return v1.getX() - v0.getX();
	}

	/**
	 * Returns the height of the viewport
	 * @return viewportH Height of the viewport
	 */
	public double height() {
		return v1.getY() - v0.getY();
	}

	/**
	 * Returns the direct distance between v0 and v1
	 * @return distv0v1
	 */
	public double distV0V1() {
		return Math.sqrt( Math.pow( width() , 2 ) + Math.pow( height() , 2 ) );
	}

	/**
	 * Converts a MapCanvas coordinate to a true coordinate in the horizontal direction
	 * @param coordinate The coordinate to convert
	 * @return trueCoord A true coordinate
	 */
	public double mapCanToTrueHor(double coordinate) {
		double scale = width()/c.getWidth();
		return coordinate * scale + v0().getX();
	}

	/**
	 * Converts a pixel length to a true length in the horizontal direction
	 * @param pixel The pixel length to convert
	 * @return trueLength A true length
	 */
	public double mapCanToTrueHorPixel(int pixel) {
		double scale = width()/c.getWidth();
		return pixel * scale;
	}

	/**
	 * Converts a MapCanvas coordinate to a true coordinate in the vertical direction
	 * @param coordinate The coordinate to convert
	 * @return trueCoord A true coordinate
	 */
	public double mapCanToTrueVer(double coordinate) {
		double scale = height()/c.getHeight();
		return coordinate * scale + v0().getY();
	}

	/**
	 * Converts a pixel length to a true length in the vertical direction
	 * @param pixel The pixel length to convert
	 * @return trueLength A true length
	 */
	public double mapCanToTrueVerPixel(int pixel) {
		double scale = height()/c.getHeight();
		return pixel * scale;
	}

	/**
	 * Converts a true coordinate to a MapCanvas coordinate in horizontal direction
	 * @param coordinate The coordinate to convert
	 * @return mapCanvasCoord A MapCanvas coordinate
	 */
	public double trueToMapCanHor(double coordinate) {
		double scale = c.getWidth()/width();
		return coordinate * scale - v0().getX();
	}

	/**
	 * Converts a true coordinate to a MapCanvas coordinate in vertical direction
	 * @param coordinate The coordinate to convert
	 * @return mapCanvasCoord A MapCanvas coordinate
	 */
	public double trueToMapCanVer(double coordinate) {
		double scale = c.getHeight()/height();
		return coordinate * scale - v0().getY();
	}

	/**
	 * Returns a string with a representation of this instance
	 */
	@Override
	public String toString() {
		return "v0: (" + v0.getX() + "," + v0.getY() + ")\nv1: (" + v1.getX() + "," + v1.getY() + ")";
	}

}
