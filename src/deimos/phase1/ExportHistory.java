package deimos.phase1;

import org.sqlite.SQLiteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Exports all URLs in browser history, optionally to a text file.
 * Used code from response by Sonny,
 * http://stackoverflow.com/questions/2562092/
 * how-to-access-google-chrome-browser-history-programmatically-on-local-machine
 * @author Bhushan Pathak
 * @author Siddhesh Karekar
 * @author Sonny
 */

public class ExportHistory {
	
	final public static String DELIM = "|";
	private static Connection connection = null;
	private static ResultSet resultSet = null;
	private static Statement statement = null;
	private static PrintStream fileStream;
	private static int count = 0; // count of entries, not lines
	
	/**
	 * Accesses Google Chrome's history file and puts output in a List.
	 * @param historyLocation the path where Chrome's 'history' files is located.
	 * @return A List containing history entries.
	 * @throws SQLiteException if Chrome's database is locked -
	 * probably indicates that Chrome is running.
	 */
	
	public static List<String> retrieveHistory(String historyLocation) throws SQLiteException
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
						+ DELIM + resultSet.getString("url"));
				count++;
			}
			
		} catch (ClassNotFoundException ce) {
			ce.printStackTrace();
		} catch (SQLiteException sle) {
			throw sle; // Let it be handled externally!
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return output;
	}

	public static void retreiveHistoryAsFile(String fileName) throws SQLiteException {

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
				if(resultSet != null)
					resultSet.close();
				if(statement != null)
					statement.close();
				if(connection != null)
					connection.close();
			}

			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		
		try {
			retreiveHistoryAsFile("export-history.txt");
		}
		catch (SQLiteException sle) {
			sle.printStackTrace();
		}
	}
}