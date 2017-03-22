package deimos.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * For the CSV outputs that we use in our paper.
 * 
 * @author Siddhesh Karekar
 */
public class StatisticsUtilsCSV
{
	
	/** Required only for inspecting CSV contents in-program. */
	/*private InputStreamReader inputStreamReader;
	private BufferedReader bufferedReader;*/
	
	/** Required for opening the file. */
	private FileWriter fileWriter;
	private PrintWriter writer;
	
	/** Specify whether it should be
	 * appended to (true) or overwritten (false).
	 * Not used afterwards */
	private boolean appendMode;	
	
	static {
		ProcessFileUtils.createDirectoryIfNotExists(DeimosConfig.DIR_STATS);
	}
	
	/**
	 * Create/use a file in the DIR_STATS, usually
	 * a CSV file.
	 * 
	 * @param filename The name (include the extension!)
	 * @param appendMode Specify whether it should be
	 * appended to (true) or overwritten (false). Default append mode is true
	 * @throws FileNotFoundException Usually if the output file can't be opened.
	 */
	public StatisticsUtilsCSV(String filename, boolean appendMode) throws
		FileNotFoundException, IOException
	{
		this.appendMode = appendMode;
		
		String fullPath = DeimosConfig.DIR_STATS + "/" + StringUtils.sanitizeFilename(filename);

		fileWriter = new FileWriter(
				new File(fullPath),
				this.appendMode);
		writer = new PrintWriter(fileWriter);

	}
	
	/**
	 * Create/use a file in the DIR_STATS, usually
	 * a CSV file, opening it in append mode.
	 * 
	 * @param filename The name (include the extension!)
	 * @throws FileNotFoundException Usually if the output file can't be opened.
	 */
	public StatisticsUtilsCSV(String filename) throws FileNotFoundException, IOException
	{
		this(filename, true);
	}
	
	/**
	 * Closes all required resources after output,
	 * and allows the output to appear in the file.
	 * MUST BE EXPLICITLY CALLED! Can't depend upon garbage
	 * collection.
	 */
	public void closeOutputStream()
	{
		try {
			if(fileWriter != null)
				fileWriter.close();
			if(writer != null) {
				writer.flush();
				writer.close();
			}
			// System.out.println("Closed!");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/** Write a new line to it. */
	public void println(String newLine)
	{
		writer.println(newLine);
	}
	
	/**
	 * Prints a CSV row string containing each of the input
	 * parameter strings. Escapes double quotes with underscores.
	 * @param strings an array of Strings e.g. (Apple, Ball, "Poop")
	 * is saved in the CSV as "Apple", "Ball", "_Poop_"
	 */
	public void printAsCSV(String ... strings)
	{
		String lineCSV = StringUtils.toCSV(strings);
		writer.println(lineCSV);
		
		// System.out.println(lineCSV);
	}
	
	
	/** Doesn't seem to be called like it should, automatically. */
	/*@Override
	protected void finalize()
	{
		closeOutputStream();
		
	}*/
	
	/** Testing */
	public static void main(String args[])
	{
		StatisticsUtilsCSV csv;
		try {
			csv = new StatisticsUtilsCSV("test.csv", false);
			csv.printAsCSV("Hello", "World");
			csv.closeOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
