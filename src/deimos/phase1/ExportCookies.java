package deimos.phase1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.io.*;

public class ExportCookies {
	static Connection connection = null;
	static ResultSet resultSet = null;
	static Statement statement = null;
	static ArrayList<String> output = new ArrayList<String>();
	static PrintStream fileStream;
	static int count = 1;

	static ArrayList<String> retrieveHistory() {
		try {
			fileStream = new PrintStream(new File("export-cookies.txt"));
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager
					.getConnection("jdbc:sqlite:C:/users/Amogh/appdata/local/google/chrome/user data/Default/Cookies");
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

		try {
			retrieveHistory();
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
