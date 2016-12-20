package deimos.phase1;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import deimos.common.DeimosConfig;

/**
 * 
 * Exports Bookmarks from Google Chrome.
 * 
 * The current output format is as follows:
 * 
 * <br>
 * <br>
 * count
 * <br>
 * {GROUP}
 * <br>
 * [Folder]
 * <br>
 * date added DELIM id DELIM url DELIM name
 * 
 * @author Amogh Bhabal
 * @author Siddhesh Karekar
 *
 */
public class ExportBookmarks
{
	/**
	 * Prevents infinite recursion.
	 */
	private static final int LIMIT_RECURSION = 5;
	private static final String FILE_CHROME_WIN_BOOKMARKS = DeimosConfig.DIR_CHROME_WIN + "/Bookmarks";
	private static JSONParser jsonParser = new JSONParser();
	private static JSONObject jsonObject;
	private static PrintStream fileStream;
	private static int count = 0;
	
	/**
	 * Google Chrome's top-level bookmarks are divided into three groups,
	 * bookmark_bar, others and synced.
	 * @param structure The root level JSONObject
	 * @param folderName The group name, used as a key
	 * @return a List containing information about bookmarks retreived.
	 */
	public static List<String> retrieveBookmarksFromGroup(JSONObject structure,
			String folderName)
	{
		
		List<String> output = new ArrayList<String>();

		try {

			JSONObject folder = (JSONObject) structure.get(folderName);
			output.add("{" + folderName.toUpperCase() + "}");
			
			/*output.add(folder.get("date_added")+DeimosConfig.DELIM+
			folder.get("date_modified")+DeimosConfig.DELIM+
			folder.get("id")+DeimosConfig.DELIM+
			folder.get("name")+DeimosConfig.DELIM+
			folder.get("type"));*/
			
			JSONArray folderchild = (JSONArray) folder.get("children");
			List<String> output_child = retrieveBookmarksFromJSONArray(folderchild, 0);
			output.addAll(output_child);
			
		}
		catch (NullPointerException npe) {
			npe.printStackTrace();
			output.add("EXCEPTION"+ DeimosConfig.DELIM + npe.toString());
		}

		output.add(" ");
		// System.out.println();
		
		return output;

	}
	
	/**
	 * The main 'groups' contain JSONArrays containing bookmarks and
	 * bookmark folders (which are also JSONArrays)
	 * @param folderchild The JSONArray, value of the "children" key
	 * @param folderName This is used to print a folder name
	 * @param recursion The max. depth to which it can be explored
	 * @return a List containing information about bookmarks retreived.
	 */
	
	public static List<String> retrieveBookmarksFromJSONArray(JSONArray folderchild,
			int recursion)
	{
		
		List<String> output = new ArrayList<String>();
		
		if(recursion <= LIMIT_RECURSION)
		{
			try {
				
				// take the elements of the json array
				Iterator<?> i = folderchild.iterator();
				
				// take each value from the json array separately
				while (i.hasNext()){
					
					JSONObject innerObj = (JSONObject) i.next();
					
					// is it a bookmark?
					if (innerObj.get("url") != null) {
						output.add(innerObj.get("date_added")+DeimosConfig.DELIM+
								innerObj.get("id")+DeimosConfig.DELIM+
								innerObj.get("url")+DeimosConfig.DELIM+
								innerObj.get("name"));
						count++;
					}
					else // it's a new folder
					{
						output.add("[" + innerObj.get("name").toString() + "]");
						List<String> output_child = retrieveBookmarksFromJSONArray(
								(JSONArray)innerObj.get("children"),
								recursion+1);
						output.addAll(output_child);
					}
					
				}
			}
			catch (NullPointerException npe) {
				npe.printStackTrace();
				output.add("EXCEPTION"+DeimosConfig.DELIM+npe.toString());
			}
			
			output.add(" ");
		}

		return output;
	}
	
	public static List<String> retreiveBookmarks(String bookmarksLocation)
	{
		count = 0;
		
		List<String> output = new ArrayList<String>();
		
		try {
			// read the json file
			FileReader reader = new FileReader(bookmarksLocation);

			jsonObject = (JSONObject) jsonParser.parse(reader);
			

			JSONObject structure = (JSONObject) jsonObject.get("roots");
			
			output.addAll(retrieveBookmarksFromGroup(structure, "bookmark_bar"));
			output.addAll(retrieveBookmarksFromGroup(structure, "other"));
			output.addAll(retrieveBookmarksFromGroup(structure, "synced"));
			

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			output.add("EXCEPTION"+ DeimosConfig.DELIM+ex.toString());
		} catch (IOException ex) {
			ex.printStackTrace();
			output.add("EXCEPTION"+ DeimosConfig.DELIM+ex.toString());
		} catch (ParseException ex) {
			ex.printStackTrace();
			output.add("EXCEPTION"+ DeimosConfig.DELIM+ex.toString());
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			output.add("EXCEPTION"+ DeimosConfig.DELIM+ex.toString());
		}
		
		return output;
	}
	
	/**
	 * Retrieves the bookmarks from Google Chrome, then stores
	 * the output as a text file in the defined output directory.
	 * Currently no output directory used.
	 * @param fileName
	 */
	public static void retreiveBookmarksAsFile(String fileName) {
		
		List<String> output = retreiveBookmarks(FILE_CHROME_WIN_BOOKMARKS);
		
		// new File(DeimosConfig.OUTPUT_DIR).mkdirs();
		
		try {
			// fileStream = new PrintStream(new File(DeimosConfig.OUTPUT_DIR + File.pathSeparator + fileName));
			fileStream = new PrintStream(fileName);
			
			fileStream.println(count);
			
			for (int i = 0; i < output.size(); i++)
			{
				// System.out.println(output.get(i));
				fileStream.println(output.get(i));
			}
			
			System.out.println(count + " bookmark(s) exported to "+fileName+ ".");
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			if(fileStream != null)
				fileStream.close();
		}
	}
	
	public static void main(String[] args)
	{
		retreiveBookmarksAsFile(DeimosConfig.FILE_OUTPUT_BOOKMARKS);
	}

}

