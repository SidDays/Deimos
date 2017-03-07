package deimos.phase2.collection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import deimos.common.DeimosConfig;
import deimos.common.StringUtils;

/**
 * Old implementation where text files were being downloaded
 * for the URLs into the DIR_URLS.
 * 
 * Parses the history input file;
 * creates a List containing each parsed URL,
 * uses a MessageDigest to generate MD5 hashes of URLs to use as filenames,
 * and uses PageFetcher's fetchHTMLAsFile to save URL texts in URLS_DIR.
 * 
 * @author Bhushan Pathak
 * @author Siddhesh Karekar
 */

public class TextFromURL {
	
	@Deprecated
	final public static String DIR_URLS = DeimosConfig.DIR_OUTPUT + "/urltexts";
	
	/**
	 * Deletes all the files exist in the URL texts directory.
	 */
	/*private static void cleanDirectory() throws SecurityException
	{
		File file = new File(DIR_URLS);
		String[] myFiles;
		if (file.isDirectory()) {
			myFiles = file.list();		
			for (int i = 0; i < myFiles.length; i++) {
				File myFile = new File(file, myFiles[i]);
				myFile.delete();
			}
		}
	}*/
	
	/** 
	 * Parses the history input file;
	 * creates a List containing each parsed URL,
	 * (optionally) uses a MessageDigest to generate MD5 hashes of URLs to use as filenames,
	 * and uses PageFetcher's fetchHTMLAsFile to save URL texts in URLS_DIR.
	 * 
	 * @param filename Usually you need 'export-history.txt'
	 * 
	 */
	@Deprecated
	public static void fetchTextFromHistoryDump(String filename)
	{
		try {
			
			int noOfURLs; // represents number of URLs taken into consideration
			File historyFile = new File("export-history.txt"); 
		
			FileReader fileReader = new FileReader(historyFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			List<String> urls = new ArrayList<String>();
			String line;
			
			while ((line = bufferedReader.readLine()) != null) {
				urls.add(line);
			}
			fileReader.close();
			
			// remove the first line that has the number of URLs
			urls.remove(0);

			/* till now, reading of file is done into an arraylist
			 * following code gets only urls from the above arraylist
			 */
			

			// Parse the history dump to get URLs only
			for (int i = 0; i < urls.size(); i++) {
				String s = urls.get(i);
				// System.out.println(s);
				String subst = s.substring(s.indexOf('|') + 1);
				urls.set(i, subst);
			}
			

			noOfURLs = DeimosConfig.LIMIT_URLS_DOWNLOADED; // any value ranging from 1 to urls.size()

			// Creates empty output directory if it doesn't exist
			new File(DIR_URLS).mkdirs(); 
			
			// cleanDirectory(); // delete previous text files

			System.out.println("Extracting text from fetched URLs...");

			try {

				for (int i = 0; (i < noOfURLs && i < urls.size()) ; i++)
				{
					String currentURL = urls.get(i).replace("https://","").
							replace("http://","");

					String truncURL = currentURL.substring(0, 
							Math.min(currentURL.length(), 32));
					
					String outputfilename;
					if(DeimosConfig.OPTION_HASH_P1_OUTPUT_FILENAMES) {
						outputfilename = StringUtils.hashFilename(currentURL);
					}
					else {
						outputfilename = StringUtils.sanitizeFilename(currentURL);
						outputfilename = outputfilename.substring(0,
								Math.min(currentURL.length(), 48));
					}
					
					System.out.format("%4d", i);
					System.out.println(": " + truncURL + " -> " + outputfilename +".txt");

					File f = new File(DIR_URLS+"/"+outputfilename);

					if(!f.isDirectory())
					{
						// PROBLEM?!!?
						if (f.exists() | f.getAbsoluteFile().exists()) {
							System.out.println("Page already fetched.");
						}
						else {
							PageFetcher.fetchHTMLAsFile(DIR_URLS + "/" +
								(outputfilename)+ ".txt", urls.get(i));
						}
					}
				}

			} catch (SecurityException se) {
				se.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		fetchTextFromHistoryDump(DeimosConfig.FILE_OUTPUT_HISTORY);
	}
}
