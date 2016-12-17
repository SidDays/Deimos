package deimos.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Utility functions to check if
 * a browser can be used to collect user data.
 * 
 * @author Siddhesh Karekar
 *
 */
public class BrowserCheck {

	final private static String CHROME_DIR_WINDOWS = System.getenv("LOCALAPPDATA") +
			"/Google/Chrome/User Data/Default";
	
	/**
	 * Confirms if Google Chrome data is available to
	 * perform export functions.
	 * To do this, 'open' each of Chrome's data files and
	 * check if any Exception is thrown.
	 * @return true if yes.
	 */
	public static boolean isChromeAvailable() {
		boolean result = false;
		
		String current = null;
		FileInputStream in;
		
		try {
			try {
				current = "History";
				File chromeHistoryDB = new File(CHROME_DIR_WINDOWS + "/History");
				in = new FileInputStream(chromeHistoryDB);
				in.close();
				
				current = "Bookmarks";
				File chromeBookmarks = new File(CHROME_DIR_WINDOWS + "/Bookmarks");
				in = new FileInputStream(chromeBookmarks);
				in.close();
				
				current = "Cookies";
				File chromeCookiesDB = new File(CHROME_DIR_WINDOWS + "/Cookies");
				in = new FileInputStream(chromeCookiesDB);
				in.close();

				result = true;

			}
			catch (IOException ine) {
				ine.printStackTrace();
			}
			
		} catch (Exception e) {
			
			// e.printStackTrace();
			System.err.println("Google Chrome's "+current+" resource is unavailable.");
		}
		
		return result;
	}
	
	public static void main(String args[]) {
		System.out.println("Is Google Chrome available? " + isChromeAvailable());
	}

}
