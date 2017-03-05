package deimos.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A collection of several utility functions for working with processes and files.
 * 
 * @author Siddhesh Karekar
 * @author Various others (credited)
 */
public class ProcessFileUtils {

	private static final String TASKLIST = "tasklist";
	private static final String KILL = "taskkill /F /IM ";
	
	/**
	 * Sourced from:
	 * stackoverflow.com/questions/81902/how-to-find-and-kill-running-win-processes-from-within-java
	 * @author Kara Rawson
	 */
	public static boolean isProcessRunning(String serviceName) throws IOException
	{
		Process p = Runtime.getRuntime().exec(TASKLIST);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null)
		{
			// System.out.println(line);
			if (line.contains(serviceName))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Sourced from:
	 * stackoverflow.com/questions/81902/how-to-find-and-kill-running-win-processes-from-within-java
	 * @author Kara Rawson
	 */
	public static void killProcess(String serviceName) throws IOException {

		Runtime.getRuntime().exec(KILL + serviceName);

	}
	
	/** Replace every character which is not a letter, number,
	 * underscore or dot with an underscore, using regex.
	 * 
	 * Sourced from:
	 * stackoverflow.com/questions/1184176/
	 * how-can-i-safely-encode-a-string-in-java-to-use-as-a-filename
	 * @author JonasCz
	 */
	public static String sanitizeFilename(String inputName) {
	    return inputName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
	}
	
	/**
	 * Create a directory in the specified location if it doesn't already exist
	 * @param dirPath
	 * @return true if and only if the directory was created,
	 * along with all necessary parent directories; false otherwise 
	 */
	public static boolean createDirectoryIfNotExists(String dirPath) {
		return new File(dirPath).mkdirs(); 
	}

}
