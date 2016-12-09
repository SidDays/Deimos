package deimos.phase1;

import org.sqlite.SQLiteException;

/**
 * Combines all 4 export functions.
 * 
 * @author Bhushan Pathak
 * @author Amogh Bhabal
 * @author Siddhesh Karekar
 *
 */

public class ExportAll {

	public static void main(String[] args) {
		
		ExportBookmarks.retreiveBookmarksAsFile("export-bookmarks.txt");
		
		ExportCookies.retreiveCookiesAsFile("export-cookies.txt");
		
		try {
			ExportHistory.retreiveHistoryAsFile("export-history.txt");
		}
		catch (SQLiteException sle) {
			
			sle.printStackTrace();
		}
		
		ExportIP.retrievePublicIPAsFile("export-publicIP.txt");

	}

}
