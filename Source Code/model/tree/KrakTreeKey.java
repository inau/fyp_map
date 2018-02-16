package model.tree;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import model.data.Point;
import model.data.RoadType;


/**
 * This class is used as key in the KDTree
 * 
 * @author Morten Fredrik Therkildsen (mfrt@itu.dk), Sune Debel (sdeb@itu.dk)
 *
 */
public class KrakTreeKey implements KDTComparable<KrakTreeKey>, Serializable{

	private static final long serialVersionUID = 4525951466836388989L;

	private transient final int DIMENSIONS = 5;

	public transient final double startx;
	public transient final double starty;
	public transient final double endx;
	public transient final double endy;
	public transient final RoadType roadType;

	/**
	 * Creates an instance of this class
	 * @param start The point where this key starts
	 * @param end The point where this key ends
	 * @param roadType The roadtype this key represents
	 */
	public KrakTreeKey(Point start, Point end, RoadType roadType){
		if(start == null)			throw new NullPointerException("parameter start was null");
		else if(end == null)		throw new NullPointerException("parameter end was null");
		else if(roadType == null)	throw new NullPointerException("parameter roadType was null");
		
		this.startx = start.getX();
		this.endx = end.getX();
		this.starty = start.getY();
		this.endy = end.getY();
		this.roadType = roadType;
	}

	public KrakTreeKey(){
		this.startx = 0;
		this.endx = 0;
		this.starty = 0;
		this.endy = 0;
		this.roadType = null;
	}

	@Override
	public double getDimensionValue(int dimension){
		if(dimension < 0)	throw new IllegalArgumentException("parameter dimension was negative");

		switch(dimension){
		case 0: return startx;
		case 1:	return starty;
		case 2: return endx;
		case 3:	return endy;
		case 4: return roadType.getZoomLevel();
		default: throw new RuntimeException("Invalid dimension: " + dimension);
		}
	}

	@Override
	public int compareInDimension(KrakTreeKey that, int dimension) {
		if(that == null)		throw new NullPointerException("parameter that was null");
		else if(dimension < 0)	throw new IllegalArgumentException("parameter dimension was null");

		switch(dimension){
		case 0: return compareValue(startx, that.startx);
		case 1:	return compareValue(starty, that.starty);
		case 2: return compareValue(endx, that.endx);
		case 3:	return compareValue(endy, that.endy);
		case 4: return compareRoadType(roadType, that.roadType);
		default: throw new RuntimeException("Invalid dimension: " + dimension);
		}
	}

	private int compareValue(double thisval, double thatval){
		if(Math.abs(thisval - thatval) < Math.pow(10, -9))	return 0;
		if(thisval < thatval)								return -1;
		return 1;
	}

	private int compareRoadType(RoadType thistype, RoadType thattype){
		if(thistype.getZoomLevel() > thattype.getZoomLevel())		return 1;	
		else if(thistype.getZoomLevel() < thattype.getZoomLevel())	return -1;
		else														return 0;
	}	

	@Override
	public int getDimensions(){
		return DIMENSIONS;
	}

	/**
	 * Serialization proxy containing all information necessary to build a KrakTreeKey.
	 * @author Sune Debel and Morten Therkildsen
	 * @serial
	 */
	private static class SerializationProxy implements Serializable{

		private static final long serialVersionUID = 8124326481158241806L;

		final double startx;
		final double starty;
		final double endx;
		final double endy;
		final RoadType roadType;

		/**
		 * Rewrites data in the proxy for a KrakTreeKey, in order to write the proxy to the disk.
		 * @param e The edge to write a proxy for.
		 * @serialData
		 */
		SerializationProxy(KrakTreeKey key){
			this.startx = key.startx;
			this.starty = key.starty;
			this.endx = key.endx;
			this.endy = key.endy;
			this.roadType = key.roadType;
		}

		/**
		 * Called after deserialization. Builds a KrakTreeKey with data read from disk.
		 * @return The edge built with deserialized data.
		 * @serialData
		 */
		private Object readResolve(){
			return new KrakTreeKey(new Point(startx, starty), new Point(endx, endy), roadType);
		}
	}

	/**
	 * Called during serialization. Changes data in serialization proxy to contain data about this KrakTreeKey.
	 * @return The SerialisationProxy for this edge.
	 * @serial
	 */
	private Object writeReplace(){
		return new SerializationProxy(this);
	}

	/**
	 * Only called when trying to instantiate an Edge from serialized data.
	 * @serialData
	 * @param in The input stream reading from the disk.
	 * @throws InvalidObjectException
	 *
	 */
	private void readObject(ObjectInputStream in) throws InvalidObjectException{
		throw new InvalidObjectException("Proxy required");
	}
	
	@Override
	public int hashCode() {
		Double d1 = new Double(startx);
		Double d2 = new Double(starty);
		return (d1.hashCode() * 17 ) ^ d2.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof KrakTreeKey)) return false;
		KrakTreeKey k = (KrakTreeKey) o;
		if (startx == k.startx && 
			starty == k.starty &&
			endx == k.endx &&
			endy == k.endy &&
			roadType == k.roadType) return true;
		else return false;
	}
}
