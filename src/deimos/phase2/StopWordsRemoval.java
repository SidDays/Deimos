package deimos.phase2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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
	
	static File stopWordFile;
	static FileReader fileReader;
	static BufferedReader bufferedReader;
	static Set<String> stopWords; // using a Set prevents duplicates
	
	// Initialize list of stopwords
	static
	{
		stopWords = new LinkedHashSet<>();
		
		// Contains list of all stopwords to remove
		stopWordFile = new File(FILE_STOPWORDS);
		try
		{
			fileReader = new FileReader(stopWordFile);
			bufferedReader = new BufferedReader(fileReader);

			// add stopwords from external file to set
			String line;
			while ((line = bufferedReader.readLine()) != null) {

				line = line.trim();
				if(line.length() > 0) {
					stopWords.add(line);
				}
			}
			fileReader.close();
			
			System.out.print("(Stopwords loaded.) ");

		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Returns a string without any stopwords in it.
	 * @param inputText
	 * @return
	 */
	public static String removeStopWordsFromString(String inputText) {
		
		StringBuilder sb = new StringBuilder();
		
		String[] words = inputText.trim().toLowerCase().split(" ");
		for(String word : words)
		{
			word = Phase2.getAlphabeticalString(word.trim());
			if(word.length() > 0 && !stopWords.contains(word))
			{
				sb.append(word);
				sb.append(" ");
			}
		}
		
		return sb.toString();

	}
	
	/**
	 * Returns a list of words after removing all stopwords in it.
	 * @param inputWordsList
	 * @return
	 */
	public static List<String> removeStopWordsFromString(List<String> inputWordsList)
	{

		List<String> inputWithoutStopWords = new ArrayList<>(inputWordsList);
		for(String word : inputWithoutStopWords)
		{
			word = Phase2.getAlphabeticalString(word.toLowerCase().trim());
		}
		inputWithoutStopWords.removeAll(stopWords);

		return inputWithoutStopWords;
	}

	/**
	 * Initializes a Set, stopWords it gets from a file SW_FILE.
	 * Goes through each file in URLS_DIR, and creates stopword-free output
	 * which is stored into SWFREE_DIR
	 */
	public static void removeStopWordsFromURLTexts() {

		File folder;
		File[] listOfFiles;
		
		String[] lineWords;
		String[] allFiles;
		List<String> fileWords;
		List<String> union;
		List<String> intersection;
		
		try {
			
			folder = new File(deimos.phase2.TextFromURL.DIR_URLS);
			listOfFiles = folder.listFiles();
			fileWords = new ArrayList<>();
			
			allFiles = new String[listOfFiles.length];
			for (int i = 0; i < listOfFiles.length; i++) {
				allFiles[i] = listOfFiles[i].getName().toString();
			}

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
					System.out.format("%4d", i);
					System.out.println(": " + filename);
					
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
