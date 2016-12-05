package deimos.phase2;

public class ProcessPhaseTwo {
	public static void main() {
		
		TextFromURL.fetchTextFromHistoryDump();
		RemoveStopWords.removeStopWordsFromURLTexts();
	}
}
