package model.data;

import java.awt.Color;
import java.io.Serializable;

/**
 * This class keeps track of the different roadtypes from the krak data
 * 
 * @author ejer
 *
 */
public enum RoadType implements Serializable{
	MOTORVEJ					(1, "Motorvej", Color.RED, 2, 											 0 , 1),
	PROJMOTORVEJ				(21, "Proj. motorvej", Color.red, 2, 									 0 , 1),
	MOTORVEJSTUNNEL				(41, "Motorvejstunnel", Color.red, 2, 									 0 , 1),
	MOTORVEJAFKØRSEL			(31, "Motorvej", Color.red, 2, 											 0 , 1),
	PRIMÆRRUTESTRSEKSM			(3, "Primærrute > 6 meter", new Color(128,255,128), new Float(1.3),		 0 , 3),
	PROJPRIMÆRVEJ				(23, "Proj. primærvej", new Color(128,255,128), new Float(1.3), 		 0 , 3),
	PRIMÆRVEJAFKØRSEL			(33, "Primærrute > 6 meter", new Color(128,255,128), new Float(1.3),	 0 , 3),
	PRIMÆRVEJTUNNEL				(43, "Primærrute > 6 meter", new Color(128,255,128), new Float(1.3),	 0 , 3),
	
	SEKUNDÆRRUTESTRSEKSM		(4, "Sekundærrute > 6 meter", new Color(128,255,128), new Float(1.3),	 5 , 12),
	PROJSEKUNDÆRVEJ				(24, "Proj. sekundærvej", new Color(128,255,128), new Float(1.3), 		 5 , 12),
	SEKUNDÆRVEJAFKØRSEL			(34, "Sekundærrute > 6 meter", new Color(128,255,128), new Float(1.3),	 5 , 12),
	SEKUNDÆRVEJTUNNEL			(44, "Sekundærrute > 6 meter", new Color(128,255,128), new Float(1.3),	 5 , 12),
	
	MOTORTRAFIKVEJ				(2, "Motortrafikvej", Color.orange, 2, 									 0 , 1),
	PROJMOTORTRAFIKVEJ			(22, "Proj. motortrafikvej", Color.orange, 2, 							 0 , 1),
	MOTORTRAFIKVEJAFKØRSEL		(32, "Motortrafikvej", Color.orange, 1, 								 0 , 1),
	MOTORTRAFIKVEJSTUNNEL		(42, "Motortrafikvejstunnel", Color.orange, 2, 							 0 , 1),	
	
	VEJTRETILSEKSM				(5, "Vej 3-6 meter", new Color(128,255,128), 1, 						28 , 21),
	PROJVEJTRETILSEKSM			(25, "Proj. vej 3-6 meter", new Color(128,255,128), 1, 					28 , 21),
	
	ANDENVEJ					(6, "Anden vej", new Color(128,255,128), new Float(1.3), 				28 , 21),
	ANDENVEJAFKØRSEL			(35, "Anden vej", new Color(128,255,128), new Float(1.3),				28 , 21),
	ANDENVEJTUNNEL				(45, "Anden vej", new Color(128,255,128), new Float(1.3),				28 , 21),
	
	STI							(8, "Sti", new Color(205,133,63), new Float(0.75),						35 , 23),
	PROJSTI						(28, "Proj. sti", new Color(205,133,63), new Float(0.75),				35 , 23),
	STITUNNEL					(48, "Stitunnel", new Color(205,133,63), new Float(0.75),				35 , 23),
	
	MARKVEJ						(10, "Markvej", new Color(128,255,128), 1, 								28 , 21),
	
	GÅGADE						(11, "Gågade", new Color(128,255,128), 1, 								28 , 21),
	
	PROJVEJMINDRESEKSM			(26, "Proj. vej > 6 meter", new Color(128,255,128), 1, 					22 , 21),
	MINDREVEJTUNNEL				(46, "Mindre vejtunnel", new Color(128,255,128), 1, 					28 , 23),
	
	FÆRGEFORBINDELSE			(80, "Færgeforbindelse", Color.white, 1, 								 4 , 1),
	STEDNAVN					(99, "Stednavn-eksakt beliggenhed ukendt", Color.white, 1, 				20 , 20);
	
	
	private final int typeID;
	private final String typeName;
	private final Color colour;
	private final float lineWidth;
	private final int zoomlevel;
	private final int roadNameLevel;
	
	private static int maxZoom;
	
	RoadType(int typeID, String typeName, Color colour, float lineWidth, int zoomlevel, int roadNameLevel) {
		if(typeID < 0)				throw new IllegalArgumentException("parameter typeID was negative");
		else if(typeName == null)	throw new NullPointerException("parameter typeName was null");
		else if(colour == null)		throw new NullPointerException("parameter colour was null");
		else if(lineWidth < 0)		throw new IllegalArgumentException("parameter lineWidth was negative");
		
		this.typeID = typeID;
		this.typeName = typeName;
		this.colour = colour;
		this.lineWidth = lineWidth;
		this.zoomlevel = zoomlevel;
		this.roadNameLevel = roadNameLevel;

		checkMaxZoom(zoomlevel);
	}
	
	/**
	 * @return The type id of this road. As specified by krak
	 */
	public int getTypeID() 			{ return typeID; }
	
	/**
	 * @return The name of the type of this road. As specified by krak
	 */
	public String getTypeName() 	{ return typeName; }
	
	/**
	 * @return The color of this road type
	 */
	public Color getColour()		{ return colour; }
	
	/**
	 * @return	The width of the line used to represent this road type
	 */
	public float getLineWidth()		{ return lineWidth; }
	
	/**
	 * @return	The zoomlevel of this road type
	 */
	public double getZoomLevel()	{ return zoomlevel; }
	
	/**
	 * @return The roadName level of this road type
	 */
	public int getRoadNameLevel() 	{ return roadNameLevel; }

	/**
	 * @return The maximum zoom level of all enums contained in this enum class
	 */
	public static int getMaxZoom(){
		return maxZoom;
	}
	
	private static void checkMaxZoom(int zoom){	if(maxZoom < zoom)	maxZoom = zoom;	}
	
	/**
	 * @param typeID
	 * @return RoadType object matching typeID. Null if nothing matches
	 */
	public static RoadType getRoadType(int typeID) {		
		for (RoadType rt : RoadType.values())
			if(rt.getTypeID() == typeID)
				return rt;
		
		return null;
	}
	
	/**
	 * Finds the closest RoadType match with respect to zoom
	 * @param zoom 
	 * @return RoadType
	 */
	public static RoadType getTypeByZoom(int zoom){
		if(zoom > maxZoom)	zoom = maxZoom;
		if(zoom < 0) 		zoom = 0;
		
		RoadType rt0 = RoadType.MOTORVEJ;
		for (RoadType rt1 : RoadType.values()){
			if(rt1.getZoomLevel() <= zoom && rt1.getZoomLevel() > rt0.getZoomLevel())
				rt0 = rt1;
		}
		
		return rt0;
	}
}
