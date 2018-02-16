package model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Generic Object Serializing class. First written to support a faster load time of the BST used in our KrakProject,
 * it's now generic and supports a number of data types.
 * @author Stahl
 *
 */
public class Serializer{
	
	/**
	 * In case anyone wants to modify the code, this EOFObject can be instantiated to check for end of file instead of 
	 * running into an exception. The EOFObject must then be checked for in the reading.
	 * @author Stahl
	 *
	 */
	private static final class EOFObject implements Serializable {
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Generic set-writing method. Writes method to a target File destination.
	 * @param output The set to be written
	 * @param destination The file destination. Can be disk, URL etc.
	 */
	public static <E extends Serializable> void write(E output, File destination)
	{
		if(output == null)				throw new NullPointerException("parameter output was null");
		else if(destination == null)	throw new NullPointerException("parameter destination was null");
		
		ObjectOutputStream out = null;
		try
		{
			out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(destination)));
			out.writeObject(output);
		}
		catch(Exception e)
		{
			System.out.println("Serialize error " + e);
			e.printStackTrace();
		}
		finally
		{
			try {
				out.close();
			} catch (IOException e) {
				System.out.println("IOException while closing writing stream : " + e);
			}
		}

	}

	/**
	 * Generic set reading method. Reads a set from a target File destination. 
	 * SuppressWarnings("unchecked") used when downcasting the read object to its runtime type
	 * @param location the location of the serialized set.
	 * @return The set read from the file.
	 */
	public static <E extends Serializable> E read(File location) {
		if(location == null)	throw new NullPointerException("parameter location was null");
		
		E result = null;
		ObjectInputStream in = null; // We have to initialize outside try block
		try
		{
			in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(location)));
			
			@SuppressWarnings("unchecked")
			E tmp = (E) in.readObject(); // Safe cast as it's read as object. 
			result = tmp;
		} catch (FileNotFoundException e) {
			System.out.println("File not found!  " + e);
		} catch (IOException e) {
			System.out.println("IOException while reading Set from stream : " + e);
		} catch (ClassNotFoundException e) {
			System.out.println("Something is wrong with your class loading!, " + e);
		} 
		finally // Flush and close buf. stream.
		{
			try {
				in.close();
				return result;
			} catch (IOException e) {
				System.out.println("IOException while closing In-stream : " + e);
			}
		}
		return result;

	}

	/**
	 * Helper method to write any serializable data. Basically just recall
	 * @param data
	 * @param destination
	 */
	public static <E extends Serializable> void writeImplementer(E data, File destination)
	{
		write(data,destination);
	}

	/**
	 * Returns a DataSearch kontaining K,V. Or at least the compiler will check for K,V at runtime.
	 * @param location
	 * @return
	 */
	public static <K, V> DataSearch<K, V> readImplementer(File location)
	{
		return read(location);
	}
}
