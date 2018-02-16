package model.data;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
/**
 * This class represent edges in our system. 
 * This class is fully serializable including a serialization proxy.
 * 
 * The edge is an immutable class.
 * @author Stahl
 *
 */
public class Edge implements Serializable{

	private static final long serialVersionUID = 3634627038496652397L;

	public enum OneWayType {
		NONE, FROMTO, TOFROM, NODRIVING;
	}

	private transient final Point fromP, toP;
	private transient final double dist;
	private transient final String roadName,postalleft, postalright;
	private transient final RoadType roadType;
	private transient final OneWayType onewaytype;
	
	private transient final Object stateLock = new Object();

	public Edge(Point fromP, Point toP, String roadName, RoadType roadType, OneWayType onewaytype, String postalleft, String postalright){
		if(fromP == null)				throw new NullPointerException("parameter fromP was null");
		else if(toP == null)			throw new NullPointerException("parameter toP was null");
		else if(roadName == null)		throw new NullPointerException("parameter roadName was null");
		else if(roadType == null)		throw new NullPointerException("parameter roadType was null");
		else if(onewaytype == null)		throw new NullPointerException("parameter onewaytype was null");
		else if(postalleft == null)		throw new NullPointerException("parameter postalleft was null");
		else if(postalright == null)	throw new NullPointerException("parameter postalright was null");
		
		this.fromP = fromP;
		this.toP = toP;
		this.roadType = roadType;
		this.roadName = roadName;
		this.postalleft = postalleft;
		this.postalright = postalright;
		this.onewaytype = onewaytype;

		dist = calculateDist();
	}

	public Edge(Point fromP, Point toP, String roadName, RoadType roadType){
		this(fromP,toP,roadName,roadType, OneWayType.NONE,"","");
	}

	private OneWayType invertedOneWay(){
		if(onewaytype == OneWayType.FROMTO) return OneWayType.TOFROM;
		else if(onewaytype == OneWayType.TOFROM) return OneWayType.FROMTO;
		else return onewaytype;
	}

	public Edge invertEdge(){
		return new Edge(toP,fromP,roadName,roadType,invertedOneWay(),postalleft,postalright);
	}

	public OneWayType getOneWay(){
		return onewaytype;
	}

	private double calculateDist() {
		double x1 = fromP.getX(); double y1 = fromP.getY();
		double x2 = toP.getX(); double y2 = toP.getY();
		double dx = x1 - x2; double dy = y1 - y2;
		double dxs = Math.pow(dx, 2); double dys = Math.pow(dy, 2);
		return Math.sqrt(dxs + dys);
	}

	public Point getStart(){
		return fromP;
	}

	public Point getEnd(){
		return toP;
	}

	public double getDist(){
		return dist;
	}

	public String getRoadName() {
		return "" + roadName;
	}

	public RoadType getRoadType(){
		return roadType;
	}

	@Override
	public String toString(){	
		return fromP.getId() + " " + toP.getId() + " " + dist;
	}

	public String getPostalLeft(){
		return postalleft;
	}

	public String getPostalRight(){
		return postalright;
	}

	public String toFormatString(){	
		String distString = ((Double) dist).toString();
		int index = distString.indexOf(".");
		if(distString.length() > index+2)	return "From: " + fromP.getX() + "," + fromP.getY() + " To: " + toP.getX() + "," + toP.getY() + " " + distString.substring(0, index+3);
		return toString();
	}	

	/**
	 * Serialization proxy containing all information necessary to build an edge.
	 * @author Sune Debel and Morten Therkildsen
	 * @serial
	 */
	private static class SerializationProxy implements Serializable{

		private static final long serialVersionUID = 4979021344819296606L;

		private final Point fromP, toP;
		private final String roadName,postalleft, postalright;
		private final RoadType roadType;
		private final OneWayType onewaytype;

		/**
		 * Rewrites data in the proxy for an edge, in order to write the proxy to the disk.
		 * @param e The edge to write a proxy for.
		 * @serialData
		 */
		SerializationProxy(Edge e){
			fromP = e.fromP;
			toP = e.toP;
			roadName = e.roadName;
			postalleft = e.postalleft;
			postalright = e.postalright;
			roadType = e.roadType;
			onewaytype = e.onewaytype;
		}

		/**
		 * Called after deserialization. Builds an edge with data read from disk.
		 * @return The edge built with deserialized data.
		 * @serialData
		 */
		private Object readResolve(){			
			return new Edge(fromP, toP, roadName, roadType, onewaytype, postalleft, postalright);
		}
	}


	/**
	 * Called during serialization. Changes data in serialization proxy to contain data about this edge.
	 * @return The SerialisationProxy for this edge.
	 * @serial
	 */
	private Object writeReplace(){
		synchronized(stateLock){
			return new SerializationProxy(this);
		}
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
}
