package deimos.phase2;

import deimos.common.DeimosConfig;
import deimos.phase2.collection.StemmerApplier;
import deimos.phase2.collection.StopWordsRemoval;
import deimos.phase2.collection.TextFromURL;

/**
 * Performs all the actions involved in Phase 2.
 * 
 * Very old at this point!
 * 
 * @author Siddhesh Karekar
 */
public class Phase2 {
	
	/**
	 * Returns the string with only its alphabetical characters.
	 * This function should be moved elsewhere later. */
	public static String getAlphabeticalString(String s) {
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if(Character.isLetter(c))
				sb.append(c);
		}
		
		return sb.toString();
	}

	/**
	 * Fetches the URL Texts for each URL in the history dump,
	 * removes stop words from each of these,
	 * applies Porter-Stemmer to stopword-free output
	 */
	@Deprecated
	public static void phase2CollectionOld() {
		
		// Make sure to configure DeimosConfig.LIMIT_URLS_DOWNLOADED !
		
		TextFromURL.fetchTextFromHistoryDump(DeimosConfig.FILE_OUTPUT_HISTORY);
		System.out.println();
		
		StopWordsRemoval.removeStopWordsFromURLTexts();
		System.out.println();
		
		StemmerApplier.applyStemmerOnSWFreeOutput();
	}
	
	public static void main(String[] args)
	{	
		// phase2old();
		
	}
}
