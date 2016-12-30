package deimos.phase2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import deimos.common.DeimosConfig;

public class StopWordsRemoval {
	
	final public static String DIR_SWFREE = DeimosConfig.DIR_OUTPUT + "/swfreetexts";
	final public static String FILE_STOPWORDS = "resources/stopwords.txt";
	
	// TODO make this a resource
	
	/**
	 * Initializes a Set, stopWords it gets from a file SW_FILE.
	 * Goes through each file in URLS_DIR, and creates stopword-free output
	 * which is stored into SWFREE_DIR
	 */
	public static void removeStopWordsFromURLTexts() {
		
		File folder;
		File[] listOfFiles;
		Set<String> stopWords; // using a Set prevents duplicates
		String[] lineWords;
		String[] allFiles;
		List<String> fileWords;
		List<String> union;
		List<String> intersection;
		
		try {
			
			folder = new File(deimos.phase2.TextFromURL.DIR_URLS);
			listOfFiles = folder.listFiles();
			stopWords = new LinkedHashSet<>();
			fileWords = new ArrayList<>();
			
			allFiles = new String[listOfFiles.length];
			for (int i = 0; i < listOfFiles.length; i++) {
				allFiles[i] = listOfFiles[i].getName().toString();
			}
			
			// Contains list of all stopwords to remove
			File stopWordFile = new File(FILE_STOPWORDS);
			FileReader fileReader = new FileReader(stopWordFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			// add stopwords from external file to set
			String line;
			while ((line = bufferedReader.readLine()) != null) {

				line = line.trim();
				if(line.length() > 0) {
					stopWords.add(line);
				}
			}
			fileReader.close();
			
			// Create empty output directory if it doesn't exist
			new File(DIR_SWFREE).mkdirs();
			
			// Initialize output 'engine' (idk)
			File inputFile;
			File outputFile;
			BufferedReader reader = null;
			BufferedWriter writer = null;
			
			// Go through all the files
			String filename;
			
			System.out.println("Removing stop words from "+listOfFiles.length+" URL text(s).");
			for (int i = 0; i < listOfFiles.length; i++)
			{
				if (listOfFiles[i].isFile())
				{
					filename = listOfFiles[i].getName();
					System.out.println(i + 1 + "\t" + filename);
					
					if(filename.endsWith(".txt"))
					{
						inputFile = new File(deimos.phase2.TextFromURL.DIR_URLS +
								"/"+filename);
						outputFile = new File(DIR_SWFREE + "/"+ filename);
						reader = new BufferedReader(new FileReader(inputFile));
						writer = new BufferedWriter(new FileWriter(outputFile));
						
						fileWords.clear();
						String currentLine;
						while((currentLine = reader.readLine()) != null)
						{
						    // trim newline 
						    String trimmedLine = currentLine.trim();
						    lineWords = trimmedLine.toLowerCase().split(" ");
						    for(int j = 0; j < lineWords.length; j++)
						    	fileWords.add(lineWords[j]);
						}
						
						// System.out.println(fileWords);
						union = new ArrayList<String>(stopWords);
						union.addAll(fileWords);
						
						intersection = new ArrayList<String>(stopWords);
						intersection.retainAll(fileWords);
						
						union.removeAll(intersection);
						intersection = new ArrayList<String>(stopWords);
						
						intersection.retainAll(union);
						union.removeAll(intersection);
						
						for(String s: union) {
							writer.write(s+" ");
						}
						
						writer.close();
						reader.close();
					}
					
				}
				else if (listOfFiles[i].isDirectory()) {
					System.out.println("DIR\t" + listOfFiles[i].getName());
				}
			}
			System.out.println("All stop words have been removed!");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		removeStopWordsFromURLTexts();
	}
}
