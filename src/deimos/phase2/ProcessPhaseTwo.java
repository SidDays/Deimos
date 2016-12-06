package deimos.phase2;

import java.io.File;

public class ProcessPhaseTwo {
	public static void main(String[] args) {	
		
		TextFromURL.fetchTextFromHistoryDump();
		RemoveStopWords.removeStopWordsFromURLTexts();
		File directory = new File(deimos.phase2.RemoveStopWords.SWFREE_DIR);
		File[] cleanedFiles = directory.listFiles();
		String[] files = new String[cleanedFiles.length];
		for (int i = 0; i < cleanedFiles.length; i++) {
			files[i] = cleanedFiles[i].getName();
			//System.out.println(files[i]);
		}
		Stemmer.main(files);
	}
}
