package deimos.phase1;

import org.sqlite.SQLiteException;

import deimos.common.DeimosConfig;
import deimos.common.StringUtils;

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
	
	private static final String FILE_CHROME_WIN_HISTORY = DeimosConfig.DIR_CHROME_WIN + "/History";
	
	private static final int LIMIT_LENGTH_TITLE = 160;
	
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
		// TODO revise history output format ADD NUMBER OF TIMES VISITED
		
		count = 0;
		
		List<String> output = new ArrayList<>();
		
		try {
			
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager
					.getConnection("jdbc:sqlite:"+historyLocation);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(
					"SELECT datetime(last_visit_time/1000000-11644473600,'unixepoch','localtime'),url, visit_count,"
					+ " title, typed_count FROM  urls order by last_visit_time desc");
			
			while (resultSet.next()) {
				
				String urlTitle = resultSet.getString("title");
				
				if(urlTitle.isEmpty()) {
					urlTitle=" ";
				} else if (urlTitle.length() > LIMIT_LENGTH_TITLE)
					urlTitle = urlTitle.substring(0, LIMIT_LENGTH_TITLE);
				
				// old format
				/*urlTitle = urlTitle.replace(DeimosConfig.DELIM, "_");
				
				output.add(resultSet.getString("datetime(last_visit_time/1000000-11644473600,'unixepoch','localtime')")
						+ DeimosConfig.DELIM + resultSet.getString("url")
						+ DeimosConfig.DELIM + urlTitle
						+ DeimosConfig.DELIM + resultSet.getInt("visit_count")
						+ DeimosConfig.DELIM + resultSet.getInt("typed_count"));*/

				output.add(StringUtils.toCSV(
						resultSet.getString("datetime(last_visit_time/1000000-11644473600,'unixepoch','localtime')"),
						resultSet.getString("url"),
						urlTitle,
						String.valueOf(resultSet.getInt("visit_count")),
						String.valueOf(resultSet.getInt("typed_count"))));
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

	/**
	 * Outputs the Google Chrome history to a file with the 
	 * name specified.
	 * @param fileName
	 * @throws SQLiteException
	 */
	public static void retreiveHistoryAsFile(String fileName) throws SQLiteException {

		List<String> output = retrieveHistory(FILE_CHROME_WIN_HISTORY);

		try {
			fileStream = new PrintStream(new File(fileName));
			// fileStream.println(count);

			for (int i = 0; i < output.size(); i++)
			{
				// System.out.println(output.get(i));
				fileStream.println(output.get(i));
			}
			
			System.out.println(count + " history entries exported to "+fileName+ ".");

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			
			if(fileStream != null)
				fileStream.close();
			
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
			retreiveHistoryAsFile(DeimosConfig.FILE_OUTPUT_HISTORY);
		}
		catch (SQLiteException sle) {
			System.out.println("Is Chrome Running?");
			sle.printStackTrace();
		}
	}
}