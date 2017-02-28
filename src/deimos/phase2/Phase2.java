package deimos.phase2;

import deimos.common.DeimosConfig;

public class Phase2 {

	/**
	 * Performs all the actions involved in Phase 2 (INCOMPLETE)
	 * Fetches the URL Texts for each URL in the history dump,
	 * removes stop words from each of these,
	 * applies Porter-Stemmer to stopword-free output
	 */
	public static void phase2() {
		
		// Make sure to configure DeimosConfig.LIMIT_URLS_DOWNLOADED !
		
		TextFromURL.fetchTextFromHistoryDump(DeimosConfig.FILE_OUTPUT_HISTORY);
		System.out.println();
		
		StopWordsRemoval.removeStopWordsFromURLTexts();
		System.out.println();
		
		StemmerApplier.applyStemmerOnSWFreeOutput();
	}
	
	public static void main(String[] args) {	
		phase2();
	}
}
