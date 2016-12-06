package deimos.phase2;

public class Phase2 {

	/**
	 * Performs all the actions involved in Phase 2 (INCOMPLETE)
	 * Fetches the URL Texts for each URL in the history dump,
	 * removes stop words from each of these,
	 * applies Porter-Stemmer to stopword-free output
	 */
	public static void phase2() {
		
		TextFromURL.fetchTextFromHistoryDump();
		
		StopWordsRemoval.removeStopWordsFromURLTexts();
		
		StemmerApplier.applyStemmerOnSWFreeOutput();
	}
	
	public static void main(String[] args) {	
		phase2();
	}
}
