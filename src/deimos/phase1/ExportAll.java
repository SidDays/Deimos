package deimos.phase1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.sqlite.SQLiteException;

import deimos.common.DeimosConfig;

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
	
	/**
	 * Zips all the output files defined in DeimosConfig
	 * into a single ZIP file.
	 */
	public static void zipOutputFiles() {
		
		/** If any errors were found. */
		// boolean errors = false;
		
		/** The number of files zipped. */
		int count_files = 0;
		
		/** The number of ZIP files created. */
		int count = 0;
		
		byte[] buffer = new byte[1024];

		try{

			FileOutputStream fos = new FileOutputStream(DeimosConfig.FILE_OUTPUT_ALL_ZIP);
			ZipOutputStream zos = new ZipOutputStream(fos);
			count++;

			// System.out.println("Output to ZIP: " + DeimosConfig.FILE_OUTPUT_ALL_ZIP);

			for(String file : DeimosConfig.FILES_OUTPUT_ALL){

				// System.out.println("File Added: " + file);
				ZipEntry ze= new ZipEntry(file);
				zos.putNextEntry(ze);

				FileInputStream in =
						new FileInputStream(file);

				int len;
				
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();
				count_files++;
			}

			zos.closeEntry();
			
			// Remember to close it.
			zos.close();
			
		}
		catch(IOException ex) {
			ex.printStackTrace();
			// errors = true;
		}
		finally {
			System.out.println(count+ " ZIP file(s) created at " + DeimosConfig.FILE_OUTPUT_ALL_ZIP + ".");
			System.out.println(count_files+ " file(s) zipped into " + DeimosConfig.FILE_OUTPUT_ALL_ZIP + ".");
		}
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
		
		zipOutputFiles();
		
		deleteOutputFiles();
	}

}
