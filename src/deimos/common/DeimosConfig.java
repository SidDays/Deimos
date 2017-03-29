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
	
	// ----------------------------------------
	// Filenames and resources
	// ----------------------------------------
	
	// Phase 2
	public static final String FILE_XML_DMOZ_EXAMPLE = "resources/xmlexample.xml";
	public static final String FILE_XML_DMOZ =
			// "E:/Downloads/Padhai/Deimos/Dmoz/content-noExternalPage2.rdf.u8";
			"resources/shopping.rdf.u8";
	
	// Phase 1
	public static final String DIR_OUTPUT = "output";
	public static final String DIR_IMAGES = "resources/images";
	public static final String DIR_CHROME_WIN = System.getenv("LOCALAPPDATA") +
			"/Google/Chrome/User Data/Default";

	/**
	 * Stores the e-mail credentials and passwords.
	 * Remember to make sure .gitignore is set accordingly!
	 */
	public static final String FILE_CREDENTIALS = "resources/credentials.properties";
	
	public static final String FILE_OUTPUT_BOOKMARKS = "export-bookmarks.txt";
	public static final String FILE_OUTPUT_COOKIES = "export-cookies.txt";
	public static final String FILE_OUTPUT_HISTORY = "export-history.csv";
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
	
	public static final String FILE_OUTPUT_CLOUD = "wordCloud.png";
	
	public static final String DIR_STATS = "output/research";

	public static final String DELIM = ">";

	public static final String EMAIL_TEAM_SIDDHESH = "siddhesh.karekar@somaiya.edu";
	public static final String EMAIL_TEAM_AMOGH = "amogh.bhabal@somaiya.edu";
	public static final String EMAIL_TEAM_BHUSHAN = "bhushan.pathak@somaiya.edu";

	public static final String[] EMAILS_TEAM = {
			EMAIL_TEAM_SIDDHESH,
			EMAIL_TEAM_AMOGH,
			EMAIL_TEAM_BHUSHAN
	};
	
	// ----------------------------------------
	// Options
	// ----------------------------------------
	
	/**
	 * If true, delete the individual text files (such as export-IP.txt) after zipping them.
	 */
	public static final boolean OPTION_DELETE_P1_OUTPUT = false;
	
	/**
	 * If true, uses the sample xml instead of the full DMOZ data.
	 */
	public static final boolean OPTION_USE_EXAMPLE_DMOZ = false;

	/**
	 * If true, the texts of URLs are stored by using a hashed filename.
	 * If false, the texts are stored with a more human-looking filename.
	 */
	public static final boolean OPTION_HASH_P1_OUTPUT_FILENAMES = false;
	
	/**
	 * In Phase 2, download not more than these many texts
	 * for web pages per history file/URL list.
	 */
	public static final int LIMIT_URLS_DOWNLOADED = Integer.MAX_VALUE;
	// public static final int LIMIT_URLS_DOWNLOADED = 10;
	
	// ----------------------------------------
	// Credentials
	// ----------------------------------------
	
	public static final String DB_USER = "deimos";
	public static final String DB_PASSWORD = "deimos";
	
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
