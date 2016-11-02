package deimos.phase1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class ExportCookies {
	static Connection connection = null;
	static ResultSet resultSet = null;
	static Statement statement = null;
	static PrintStream fileStream;
	static int count = 1;

	static List<String> retrieveCookies(String cookiesLocation) {
		
		List<String> output = new ArrayList<>();
		
		try {
			fileStream = new PrintStream(new File("export-cookies.txt"));
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager
					.getConnection("jdbc:sqlite:"+cookiesLocation);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(
					"SELECT host_key, name FROM  cookies");

			while (resultSet.next()) {
				output.add(resultSet.getString("host_key")
						+ "|" + resultSet.getString("name"));
				count++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}

	public static void main(String[] args) throws FileNotFoundException {
		
		
		
		// The AppData/Local folder - WINDOWS ONLY!
		String dataFolder = System.getenv("LOCALAPPDATA");

		// The default directory where chrome keeps its files
		String cookiesLocation = dataFolder+"/Google/Chrome/User Data/Default/Cookies";

		try {
			List<String> output = retrieveCookies(cookiesLocation);
			System.out.println(count);
			for (int i = 0; i < output.size(); i++) {
				fileStream.println(output.get(i));
			}
			fileStream.println(count);
		}

		catch (Exception e) {
			e.printStackTrace();
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
}
