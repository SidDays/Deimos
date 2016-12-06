package deimos.phase1;

/* Code from
 * http://stackoverflow.com/questions/2562092/
 * how-to-access-google-chrome-browser-history-programmatically-on-local-machine
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class ExportHistory {
	
	private static Connection connection = null;
	private static ResultSet resultSet = null;
	private static Statement statement = null;
	private static PrintStream fileStream;
	private static int count = 0; // count of entries, not lines

	public static List<String> retrieveHistory(String historyLocation)
	{
		List<String> output = new ArrayList<>();
		
		try {
			
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager
					.getConnection("jdbc:sqlite:"+historyLocation);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(
					"SELECT datetime(last_visit_time/1000000-11644473600,'unixepoch','localtime'),url FROM  urls order by last_visit_time desc");
			
			while (resultSet.next()) {
				output.add(resultSet.getString("datetime(last_visit_time/1000000-11644473600,'unixepoch','localtime')")
						+ " | " + resultSet.getString("url"));
				count++;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}

	public static void retreiveHistoryAsFile(String fileName) {

		// The AppData/Local folder - WINDOWS ONLY!
		String dataFolder = System.getenv("LOCALAPPDATA");

		// The default directory where chrome keeps its files
		String historyLocation = dataFolder+"/Google/Chrome/User Data/Default/History";

		List<String> output = retrieveHistory(historyLocation);

		try {
			fileStream = new PrintStream(new File(fileName));
			fileStream.println(count);

			for (int i = 0; i < output.size(); i++)
			{
				// System.out.println(output.get(i));
				fileStream.println(output.get(i));
			}
			
			System.out.println(count + " history entries exported.");

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			}

			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

		retreiveHistoryAsFile("export-history.txt");
	}
}