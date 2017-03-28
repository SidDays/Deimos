package deimos.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
	
	/**
	 * Reads all lines from a textual file, whose path is specified,
	 * and returns a List containing all those lines.
	 * 
	 * @param filePath A path to a textual file.
	 * @return
	 */
	public static List<String> readFileIntoList(String filePath) throws FileNotFoundException, IOException
	{
		File inputFile = new File(filePath); 
		return readFileIntoList(inputFile);
	}
	
	/**
	 * Reads all lines from a textual file
	 * and returns a List containing all those lines.
	 * 
	 * @param inputFile A file object containg a textual file.
	 * @return
	 */
	public static List<String> readFileIntoList(File inputFile) throws FileNotFoundException, IOException
	{
		List<String> lines = null;

		FileReader fileReader = new FileReader(inputFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;

		lines = new ArrayList<>();

		while ((line = bufferedReader.readLine()) != null) {
			lines.add(line);
		}
		fileReader.close();
		// System.out.format("Parsed file '%s' with %d line(s) into List.\n", inputFile.toString(), lines.size());

		return lines;
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
