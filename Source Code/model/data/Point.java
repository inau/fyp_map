package model.data;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;


/**
 * This class represents a point in two dimensions. It is unconditionally thread-safe
 * 
 * @author Morten Fredrik Therkildsen (mfrt@itu.dk)
 *
 */
public class Point implements Comparable<Point>, Serializable{

	private static final long serialVersionUID = 3494437703489383667L;

	private transient final double x;
	private transient final double y;
	private transient int id;

	private transient final Object stateLock = new Object();

	/**
	 * Create a point with a specific id value
	 * @param id
	 * @param x
	 * @param y
	 */
	public Point(int id, double x, double y){		
		if(id < 0) 		throw new IllegalArgumentException("parameter id was negative");
		
		this.x = x;
		this.y = y;
		this.id = id;
	}

	/**
	 * Create a point with default id value (0)
	 * @param x
	 * @param y
	 */
	public Point(double x, double y)
	{
		this(0, x, y);
	}

	/**
	 * @return The value of y
	 */
	public double getY(){
		return y;
	}

	/**
	 * 
	 * @return The value of x
	 */
	public double getX(){
		return x;
	}

	/**
	 * 
	 * @return The value of id
	 */
	public int getId(){
		synchronized(stateLock){
			return id;
		}
	}

	/**
	 * Modify this point's id
	 * @param newId
	 */
	public void setId(int newId){
		synchronized(stateLock){
			id = newId;
		}
	}

	@Override
	public int compareTo(Point that) {
		if(x < that.getX())	return -1;
		if(x > that.getX())	return 1;
		return 0;
	}

	@Override
	public String toString(){
		return "("+x+", "+y+")";
	}

	/**
	 * This class stores all information necessary to create a faithful replica of this Point instance during deserialization.
	 * 
	 * @author Morten Fredrik Therkildsen (mfrt@itu.dk), Sune Debel (sdeb@itu.dk)
	 */
	private static class SerializationProxy implements Serializable{

		private static final long serialVersionUID = 8764450962105231109L;

		final double x;
		final double y;		
		int id;

		SerializationProxy(Point point){			
			this.x = point.x;
			this.y = point.y;
			this.id = point.id;
		}

		/**
		 * Creates a new Point instance containing the exact information it had at serialization time
		 * @return a faithful replica of the serialized instance
		 */
		private Object readResolve(){
				return new Point(id, x, y);
		}
	}

	/**
	 * Serialize a proxy containing enough info to create a faithful replica of this {@code Point} instance during readResolve
	 * @return a reference to a proxy containing info about this Point instance
	 */
	private Object writeReplace(){
		synchronized(stateLock){
			return new SerializationProxy(this);
		}
	}

	/**
	 * If anyone attempts to modify the state of the deserialized object, an exception will be thrown
	 * @param in
	 * @throws InvalidObjectException
	 */
	private void readObject(ObjectInputStream in) throws InvalidObjectException{
		throw new InvalidObjectException("Proxy required");
	}
}