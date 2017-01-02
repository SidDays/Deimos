package deimos.phase2;

import deimos.common.DeimosConfig;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TextFromURL {
	
	final public static String DIR_URLS = DeimosConfig.DIR_OUTPUT + "/urltexts";
	private static MessageDigest md;

	/**
	 * Deletes all the files exist in the URL texts directory.
	 */
	public static void cleanDirectory() throws SecurityException
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
	}
	
	/** 
	 * Generates an MD5 Hash to use as a file name.
	 * This helps to determine whether one URL is visited more than once
	 * @param name The URL to hash
	 * @return A legible filename with a hash.
	 */
	public static String getHashedFileName(String name) {
		
		String filename = "";
		byte[] digest = md.digest(name.getBytes());
		filename = (new HexBinaryAdapter()).marshal(digest);

		return filename;
	}
	
	/** 
	 * Parses the history input file 'export-history.txt';
	 * creates a List containing each parsed URL,
	 * uses a MessageDigest to generate MD5 hashes of URLs to use as filenames,
	 * and uses PageFetcher's fetchHTMLAsFile to save URL texts in URLS_DIR.
	 * 
	 */
	
	public static void fetchTextFromHistoryDump() {
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
			
			// Initialize the MessageDigest object that lets us produce hashes
			try {
				md = MessageDigest.getInstance("MD5");
				
			} catch(NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			
			
			// Parse the history dump to get URLs only
			for (int i = 0; i < urls.size(); i++) {
				String s = urls.get(i);
				// System.out.println(s);
				String subst = s.substring(s.indexOf('|') + 1);
				urls.set(i, subst);
			}
			
			// remove duplicates from urls by using sets - NOT REQUIRED
			/*
			Set<String> hs = new HashSet<>();
			hs.addAll(urls);
			urls.clear();
			urls.addAll(hs);
			*/

			noOfURLs = 50; // any value ranging from 1 to urls.size()

			// Creates empty output directory if it doesn't exist
			new File(DIR_URLS).mkdirs(); 
			
			// cleanDirectory(); // delete previous text files

			System.out.println("Extracting text from fetched URLs...\n");

			try {

				for (int i = 0; (i < noOfURLs && i < urls.size()) ; i++)
				{
					String currentURL = urls.get(i).replace("https://","").
							replace("http://","");

					String truncURL = currentURL.substring(0, 
							Math.min(currentURL.length(), 32));

					String hashValue = getHashedFileName(currentURL);

					System.out.println(i + ": " + truncURL + " " + hashValue);

					File f = new File(DIR_URLS+"/"+hashValue);

					if(!f.isDirectory())
					{
						// PROBLEM?!!?
						if (f.exists() | f.getAbsoluteFile().exists()) {
							System.out.println("Page already fetched.");
						}
						else {
							PageFetcher.fetchHTMLAsFile(DIR_URLS + "/" +
								(hashValue)+ ".txt", urls.get(i));
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

		fetchTextFromHistoryDump();
	}
}
