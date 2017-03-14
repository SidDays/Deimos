package deimos.phase2.collection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import deimos.common.DeimosConfig;

/**
 * Contains functions for actually using Porter-Stemming
 * algorithm in our system.
 * 
 * Reference:
 * stackoverflow.com/questions/13163547/using-hashmap-to-count-instances
 * stackoverflow.com/questions/1066589/iterate-through-a-hashmap
 * 
 * @author Siddhesh Karekar
 *
 */
public class StemmerApplier {
	
	@Deprecated
	final public static String PS_DIR = DeimosConfig.DIR_OUTPUT + "/pstemmedtexts";
	
	/**
	 * Reuse the same Stemmer object whether it is to process multiple files
	 * or a single one.
	 */
	public static Stemmer stemmer;
	
	static {
		stemmer = new Stemmer();
	}
	
	/**
	 * While Stemmer provides an implementation of the algorithm,
	 * this class uses it to stem the outputs produced on StopWordsRemoval
	 * and saves them to PS_DIR.
	 * 
	 * It reads every file in the specified directory,
	 * then goes through each file byte-by-byte, and
	 * finally stems each word it finds.
	 * 
	 * This implementation is similar to the 
	 * main defined in Stemmer.
	 */
	@Deprecated
	public static void applyStemmerOnSWFreeOutput()
	{
		int count = 0;
		
		// Specifies directory of stopword-free output
		File directory = new File(deimos.phase2.collection.StopWordsRemoval.DIR_SWFREE);

		File[] cleanedFiles = directory.listFiles();
		String[] files = new String[cleanedFiles.length];
		String[] names = new String[cleanedFiles.length];
		
		// Make a list of all available files, and their filenames
		for (int i = 0; i < cleanedFiles.length; i++) {
			files[i] = cleanedFiles[i].getAbsolutePath();
			names[i] = cleanedFiles[i].getName();
			// System.out.println(files[i]);
		}

		// Creates empty output directory if it doesn't exist
		new File(PS_DIR).mkdirs();

		// Initialize output 'engine' (idk)
		File outputFile;
		BufferedWriter writer = null;

		// Use Stemmer on the files; mostly copied from Stemmer
		char[] w = new char[501];
		
		for (int i = 0; i < files.length; i++)
		{
			try
			{
				FileInputStream in = new FileInputStream(files[i]);
				try
				{
					outputFile = new File(PS_DIR + "/"+ names[i]);
					count++;
					
					writer = new BufferedWriter(new FileWriter(outputFile));
					while(true)
					{
						int ch = in.read();
						if (Character.isLetter((char) ch))
						{
							int j = 0;
							while(true)
							{
								ch = Character.toLowerCase((char) ch);
								w[j] = (char) ch;
								if (j < 500) j++;
								ch = in.read();
								if (!Character.isLetter((char) ch))
								{
									/* to test add(char ch) */
									for (int c = 0; c < j; c++)
										stemmer.add(w[c]);

									stemmer.stem();

									/* and now, to test toString() */
									String u = stemmer.toString();
									
									// System.out.print(u);
									writer.write(u + " ");
									break;
								}
							}
						}
						if (ch < 0)
							break;
						// System.out.print((char)ch);
					}

					try {
						writer.close();
					}
					catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
				catch (IOException e)
				{
					System.out.println("Error reading " + files[i]);
					e.printStackTrace();
					break;
				}

				try {
					in.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			catch (FileNotFoundException e)
			{
				System.out.println("File " + files[i] + " not found.");
				break;
			}
			// System.out.println();
		}
		
		System.out.println(count + " stopword-free text(s) stemmed.");
	}
	
	/**
	 * Use stemmer to only stem a single word.
	 * @param currentWord
	 * @return
	 */
	public static String stemSingleWord(String currentWord)
	{
		// clear stemmer buffer
		stemmer.stem();
		
		for(int j = 0; j < currentWord.length() && j < 501; j++)
		{
			stemmer.add(currentWord.charAt(j));
		}
		
		stemmer.stem();
		 
		return stemmer.toString();
	}
	
	/**
	 * Alternative to applyStemmerOnSWFreeOutput -
	 * Gets the porter-stemmed output for just a source input text.
	 */
	public static String stemText(String input)
	{
		// clear stemmer buffer
		stemmer.stem();
		
		StringBuilder sb = new StringBuilder();
		
		String[] words = input.split(" ");
		for(int i = 0; i < words.length; i++)
		{			
			sb.append(stemSingleWord(words[i]));
			sb.append(" ");
		}
		
		return sb.toString();
	}
	
	/**
	 * Alternative to applyStemmerOnSWFreeOutput -
	 * Returns the stemmed words and their counts as a key-count HashMap.
	 * Also converts all the words to lowercase.
	 * 
	 * @param input The input text
	 * @return a Map containing all the stemmed words and their occurences.
	 */
	public static Map<String,Integer> stemmedWordsAndCount(String input)
	{
		Map<String,Integer> wordCounts = new HashMap<>();
		
		String[] words = input.split(" ");
		for(int i = 0; i < words.length; i++)
		{	
			String stemmedWord = stemSingleWord(words[i].toLowerCase());
			if(!invalidStemmedWord(stemmedWord))
			{
				// Try to get the 'count' of this stemmed result
				Integer existingCount = wordCounts.get(stemmedWord);

				// If it is not in the HashMap, null will be returned.
				if(existingCount == null)
					wordCounts.put(stemmedWord, 1);
				else
					wordCounts.put(stemmedWord, existingCount + 1);
			}
		}
		
		return wordCounts;
	}
	
	/**
	 * Returns the stemmed words and their counts as a key-count HashMap,
	 * for a List of input text Strings.
	 * Also converts all the words to lowercase.
	 * 
	 * @param inputTexts A List of the input text
	 * @return a Map containing all the stemmed words and their occurences.
	 */
	public static Map<String,Integer> stemmedWordsAndCount(List<String> inputTexts)
	{
		HashMap<String,Integer> wordCounts = new HashMap<>();

		String[] words;

		for(String input : inputTexts)
		{
			words = input.split(" ");
			for(int i = 0; i < words.length; i++)
			{	
				String stemmedWord = stemSingleWord(words[i].toLowerCase());
				if(!invalidStemmedWord(stemmedWord))
				{
					// Try to get the 'count' of this stemmed result
					Integer existingCount = wordCounts.get(stemmedWord);

					// If it is not in the HashMap, null will be returned.
					if(existingCount == null)
						wordCounts.put(stemmedWord, 1);
					else
						wordCounts.put(stemmedWord, existingCount + 1);
				}
			}
		}
		
		return wordCounts;
	}
	
	/**
	 * Returns true if the word after stemming is invalid:
	 * e.g. e, f, g, aaa
	 * @param word
	 * @return true if invalid (i.e. not to include in list of stemmed words),
	 * false if valid (do include).
	 */
	public static boolean invalidStemmedWord(String word)
	{
		// TODO add more functions, but keep in mind optimization.
		
		if(word.length() < 2)
			return true;
		
		return false;
	}
	
	/** Test it out. */
	public static void main(String[] args)
	{
		String testInput = "Jump jumper jumping dog doggone doge "
				+ "eat eatery eaten earthy earth earthen "
				+ "jumped jumping eated eaten";
		System.out.println("Test string: "+testInput+"\n");
		
		// System.out.println(stemText(testInput));
		
		Map<String,Integer> testWordCounts = stemmedWordsAndCount(testInput);
		
		// print it
		System.out.format("%8s %s\n", "Word", "Count");
		for (Map.Entry<String, Integer> entry : testWordCounts.entrySet())
		{
		    String key = entry.getKey();
		    int value = entry.getValue();
		    
		    System.out.format("%8s %2d\n", key, value);
		}
	}
}
