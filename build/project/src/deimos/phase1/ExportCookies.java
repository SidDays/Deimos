package deimos.phase1;

import org.sqlite.SQLiteException;

import deimos.common.DeimosConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class ExportCookies {
	
	private static final String FILE_CHROME_WIN_COOKIES = DeimosConfig.DIR_CHROME_WIN + "/Cookies";
	
	private static Connection connection = null;
	private static ResultSet resultSet = null;
	private static Statement statement = null;
	private static PrintStream fileStream;
	
	/** Count of entries, not lines */
	private static int count = 0;
	
	// TODO go over cookie output formats
	
	public static List<String> retrieveCookies(String cookiesLocation) throws SQLiteException {
		
		count = 0;
		
		List<String> output = new ArrayList<>();
		
		try {

			Class.forName("org.sqlite.JDBC");
			connection = DriverManager
					.getConnection("jdbc:sqlite:"+cookiesLocation);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(
					"SELECT host_key, name FROM  cookies");

			while (resultSet.next()) {
				output.add(resultSet.getString("host_key")
						+ DeimosConfig.DELIM + resultSet.getString("name"));
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
	
	public static void retreiveCookiesAsFile(String fileName) throws SQLiteException {

		List<String> output = retrieveCookies(FILE_CHROME_WIN_COOKIES);
		
		try {
			fileStream = new PrintStream(new File(fileName));
			fileStream.println(count);
			
			for (int i = 0; i < output.size(); i++)
			{
				// System.out.println(output.get(i));
				fileStream.println(output.get(i));
			}
			
			System.out.println(count + " cookie(s) exported to "+fileName+ ".");
			
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

			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		
		try {
			retreiveCookiesAsFile(DeimosConfig.FILE_OUTPUT_COOKIES);
		} catch (SQLiteException e) {
			
			e.printStackTrace();
		}
	}
}
