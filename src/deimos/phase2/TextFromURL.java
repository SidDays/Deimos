package deimos.phase1;

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
	
	final private static String URLS_DIR = DeimosConfig.OUTPUT_DIR + "/urltexts";
	private static MessageDigest md;

	/**
	 * Deletes all the files exist in the URL texts directory.
	 */
	public static void cleanDirectory() throws SecurityException
	{
		File file = new File(URLS_DIR);
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
				String subst = s.substring(s.indexOf('|') + 2);
				urls.set(i, subst);
			}
			
			// remove duplicates from urls by using sets - NOT REQUIRED
			/*
			Set<String> hs = new HashSet<>();
			hs.addAll(urls);
			urls.clear();
			urls.addAll(hs);
			*/

			noOfURLs = 1000; // any value ranging from 1 to urls.size()

			new File(URLS_DIR).mkdirs(); // Creates empty output directory if it doesn't exist

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

					File f = new File(URLS_DIR+"/"+hashValue);

					if(!f.isDirectory())
					{
						// PROBLEM?!!?
						if (f.exists() | f.getAbsoluteFile().exists()) {
							System.out.println("Page already fetched.");
						}
						else {
							PageFetcher.fetchHTMLAsFile(URLS_DIR + "/" +
								(hashValue)+ ".txt", urls.get(i));
						}
					}
				}

				System.out.println("All files are fetched!");


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
