package model.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class MapParser {

	private static final String POINTFILE = "kdv_node_unload.txt";
	private static final String EDGEFILE = "kdv_unload.txt";	
	private static final String ENCODING = "ISO-8859-1";

	private static BufferedReader edges, points;

	private static final Object IO_POINTLOCK = new Object();
	private static final Object IO_EDGELOCK = new Object();

	/**
	 * Initialize the BufferedReader objects tied to the default input files
	 */	
	public static void loadDefaultFiles(){
		points = reader(POINTFILE, ENCODING);
		edges = reader(EDGEFILE, ENCODING);
	}

	/**
	 * Initialize the BufferedReader objects tied to the parameter files
	 * @param pointFile	containing node information
	 * @param edgeFile	containing edge information
	 */
	public static void loadSpecFiles(String pointFile, String edgeFile) {
		if(pointFile == null)			throw new NullPointerException("parameter pointFile was null");
		else if(edgeFile == null)		throw new NullPointerException("parameter edgeFile was null");
		else if(pointFile.equals(""))	throw new IllegalArgumentException("parameter pointFile was emptystring");
		else if(edgeFile.equals(""))	throw new IllegalArgumentException("parameter edgeFile was emptystring");
		
		points = reader(pointFile, ENCODING);
		edges = reader(edgeFile, ENCODING);
	}

	/**
	 * Return the id, x, and y coordinates from the next line in the file
	 * @return	String[] id at [0], x-cord at [1] and y-cord at [2]. Null if reaching end of file
	 */
	public static String[] nextNodeInfo(){
		try {
			String[] pointInfo = new String[3];
			synchronized(IO_POINTLOCK){
				String s = points.readLine();
				if(s == null) return null;
				String[] splitString = s.split(",");
				pointInfo[0] = splitString[splitString.length-3];
				pointInfo[1] = splitString[splitString.length-2];
				pointInfo[2] = splitString[splitString.length-1];
				return pointInfo;
			}
		} catch (IOException e) {
		}
		return null;
	}

	/**
	 * Return an array containing edge information taken from the next line of file
	 * @return	String[] [0] = fromId, [1] = toId, [2] = roadName, [3] = roadType, [4] = speedLimit. Null if reaching end of file
	 */	
	public static String[] nextEdgeInfo(){
		try {
			String[] edgeInfo = new String[7];
			synchronized(IO_EDGELOCK){
				String s = edges.readLine();
				if(s == null) return null;
				String[] fromtoSplit = s.split(",");
				edgeInfo[0] = fromtoSplit[0]; //From node ID
				edgeInfo[1] = fromtoSplit[1]; //To node ID
				edgeInfo[2] = fromtoSplit[6]; //Road name
				edgeInfo[3] = fromtoSplit[5]; //Road type
				edgeInfo[4] = fromtoSplit[27];//One way
				edgeInfo[5] = fromtoSplit[17];// Left side of road postal code
				edgeInfo[6] = fromtoSplit[18];// Right side of road postal code
				return edgeInfo;
			}
		} catch (IOException e) {
		}
		return null;
	}

	/**
	 * Return a BufferedReader object skipping the first line of the file
	 * @param filename of the file to read from
	 * @param encoding of the file's content
	 * @return BufferedReader object
	 */
	private static BufferedReader reader(String filename, String encoding){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), encoding));
			br.readLine();
		} catch (UnsupportedEncodingException e) {
		} catch (IOException e) {
		}
		return br;
	}

}
