package deimos.phase1;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ExportBookmarks {
	
	
	//private static final String filePath = "C:/Users/Amogh/Desktop/New folder (2)/Bookmarks";
	
	public static void main(String[] args) {

		String dataFolder = System.getenv("LOCALAPPDATA");
		String filePath = dataFolder+"/Google/Chrome/User Data/Default/Bookmarks";
		try {
			// read the json file
			FileReader reader = new FileReader(filePath);

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

			// get a String from the JSON object
			//String checksum = (String) jsonObject.get("checksum");
			//System.out.println(checksum);

			// get a number from the JSON object
			//long id =  (long) jsonObject.get("id");
			//System.out.println("The id is: " + id);

			JSONObject structure = (JSONObject) jsonObject.get("roots");
			
			///////////////////////////// 						bookmark_bar BOOKMARK 						/////////////////////////////
			System.out.println("BOOKMARK BAR");
			JSONObject bookmar = (JSONObject) structure.get("bookmark_bar");
			JSONArray bookchild= (JSONArray) bookmar.get("children");
			
			// take the elements of the json array
			Iterator i = bookchild.iterator();
			// take each value from the json array separately
			while (i.hasNext()) {
				JSONObject innerObj = (JSONObject) i.next();
				//System.out.println("date_added ="+ innerObj.get("date_added")+" | id ="+ innerObj.get("id")+" | name ="+ innerObj.get("name")+" | type ="+ innerObj.get("type")+" | url ="+ innerObj.get("url"));
				System.out.println(innerObj.get("date_added")+" | "+ innerObj.get("id")+" | "+ innerObj.get("name")+" | "+ innerObj.get("url"));
			}
			//System.out.println("date_added ="+ bookmar.get("date_added")+" | date_modified ="+ bookmar.get("date_modified")+" | id ="+ bookmar.get("id")+" | name ="+ bookmar.get("name")+" | type ="+ bookmar.get("type"));
			System.out.println(bookmar.get("date_added")+" | "+ bookmar.get("date_modified")+" | "+ bookmar.get("id")+" | "+ bookmar.get("name")+" | "+ bookmar.get("type"));
			
			///////////////////////////// 						other OTHER 						/////////////////////////////
			System.out.println("OTHER");
			JSONObject other = (JSONObject) structure.get("other");
			JSONArray otherchild= (JSONArray) other.get("children");
			
			// take the elements of the json array
			Iterator j = otherchild.iterator();

			// take each value from the json array separately
			while (j.hasNext()) {
				JSONObject innerObj = (JSONObject) j.next();
				System.out.println(innerObj.get("date_added")+" | "+ innerObj.get("id")+" | "+ innerObj.get("name")+" | "+ innerObj.get("url"));
			}
			//System.out.println("date_added ="+ other.get("date_added")+" | date_modified ="+ other.get("date_modified")+" | id ="+ other.get("id")+" | name ="+ other.get("name")+" | type ="+ other.get("type"));
			System.out.println(other.get("date_added")+" | "+ other.get("date_modified")+" | "+ other.get("id")+" | "+ other.get("name")+" | "+ other.get("type"));
			
			///////////////////////////// 						synced SYNCED 						/////////////////////////////
			System.out.println("SYNCED");
			JSONObject synced = (JSONObject) structure.get("synced");
			JSONArray syncchild= (JSONArray) synced.get("children");
			
			// take the elements of the json array
			Iterator k = syncchild.iterator();

			// take each value from the json array separately
			while (k.hasNext()) {
				JSONObject innerObj = (JSONObject) k.next();
				System.out.println(innerObj.get("date_added")+" | "+ innerObj.get("id")+" | "+ innerObj.get("name")+" | "+ innerObj.get("url"));
			}
			//System.out.println("date_added ="+ synced.get("date_added")+" | date_modified ="+ synced.get("date_modified")+" | id ="+ synced.get("id")+" | name ="+ synced.get("name")+" | type ="+ synced.get("type"));
			System.out.println(synced.get("date_added")+" | "+ synced.get("date_modified")+" | "+ synced.get("id")+" | "+ synced.get("name")+" | "+ synced.get("type"));

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ParseException ex) {
			ex.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}

	}

}

