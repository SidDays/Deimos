package deimos.phase1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ExportBookmarks
{
	final private static int RECURSION_LIMIT = 5; /* prevent infinite recursion */
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
			output.add(folderName.toUpperCase());
			/*output.add(folder.get("date_added")+" | "+
			folder.get("date_modified")+" | "+
			folder.get("id")+" | "+
			folder.get("name")+" | "+
			folder.get("type"));*/
			
			JSONArray folderchild = (JSONArray) folder.get("children");
			List<String> output_child = retrieveBookmarksFromJSONArray(folderchild, 0);
			output.addAll(output_child);
			
		}
		catch (NullPointerException npe) {
			npe.printStackTrace();
			output.add("EXCEPTION | "+npe.toString());
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
		
		if(recursion <= RECURSION_LIMIT)
		{
			try {
				
				// take the elements of the json array
				Iterator<?> i = folderchild.iterator();
				
				// take each value from the json array separately
				while (i.hasNext()){
					
					JSONObject innerObj = (JSONObject) i.next();
					
					// is it a bookmark?
					if (innerObj.get("url") != null) {
						output.add(innerObj.get("date_added")+" | "+
								innerObj.get("id")+" | "+
								innerObj.get("name")+" | "+
								innerObj.get("url"));
						count++;
					}
					else // it's a new folder
					{
						output.add(innerObj.get("name").toString());
						List<String> output_child = retrieveBookmarksFromJSONArray(
								(JSONArray)innerObj.get("children"),
								recursion+1);
						output.addAll(output_child);
					}
					
				}
			}
			catch (NullPointerException npe) {
				npe.printStackTrace();
				output.add("EXCEPTION | "+npe.toString());
			}
			
			output.add(" ");
		}

		return output;
	}
	
	public static List<String> retreiveBookmarks(String bookmarksLocation)
	{
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
			output.add("EXCEPTION | "+ex.toString());
		} catch (IOException ex) {
			ex.printStackTrace();
			output.add("EXCEPTION | "+ex.toString());
		} catch (ParseException ex) {
			ex.printStackTrace();
			output.add("EXCEPTION | "+ex.toString());
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			output.add("EXCEPTION | "+ex.toString());
		}
		
		return output;
	}
	
	public static void retreiveBookmarksAsFile(String fileName) {
		
		String dataFolder = System.getenv("LOCALAPPDATA");
		String filePath = dataFolder+"/Google/Chrome/User Data/Default/Bookmarks";
		
		List<String> output = retreiveBookmarks(filePath);
		
		try {
			fileStream = new PrintStream(new File(fileName));
			
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
	}
	
	public static void main(String[] args)
	{
		retreiveBookmarksAsFile("export-bookmarks.txt");
	}

}

