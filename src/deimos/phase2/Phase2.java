package deimos.phase2;

import deimos.common.DeimosConfig;
import deimos.common.TimeUtils;
import deimos.phase2.collection.StemmerApplier;
import deimos.phase2.collection.StopWordsRemoval;
import deimos.phase2.collection.TextFromURL;
import deimos.phase2.ref.RefIDF;
import deimos.phase2.ref.RefTopicsHierarchyTFParser;
import deimos.phase2.ref.RefWeightCalculation;
import deimos.phase2.similarity.SimilarityMapper;
import deimos.phase2.user.UserIDF;
import deimos.phase2.user.UserInfo;
import deimos.phase2.user.UserTrainingInput;
import deimos.phase2.user.UserURLsTF;
import deimos.phase2.user.UserWeightCalculation;

/**
 * Performs all the actions involved in Phase 2.
 * 
 * @author Siddhesh Karekar
 */
public class Phase2 {
	
	private static void prepareReferenceOntology()
	{
		// if true, truncate everything, else resume.
		RefTopicsHierarchyTFParser.generateTopicsHierarchyAndTF(true);
		
		RefIDF idf = new RefIDF();
		idf.computeIDF(-1); // Where to resume from (if -1, truncate and start over)
		
		RefWeightCalculation.updateWeights();
	}
	
	private static void prepareUserData(int user_id)
	{
		// remove hardcode
		UserInfo.insertUserInfoIntoDB(user_id, DeimosConfig.FILE_OUTPUT_USERINFO, DeimosConfig.FILE_OUTPUT_PUBLICIP, false);
		
		UserURLsTF.userURLAndTFTableInsertion(user_id);
		
		UserIDF.computeUserIDF(user_id);
		
		UserWeightCalculation.updateWeights(user_id);
	}
	
	private static void similarityMapping(int user_id)
	{
		SimilarityMapper.computeSimilarity(user_id);
		
		UserTrainingInput.calculateTrainingInputs(user_id, true);
	}
	
	public static void phase2(int user_id) {
		prepareReferenceOntology();
		prepareUserData(user_id);
		similarityMapping(user_id);
	}
	
	/**
	 * Fetches the URL Texts for each URL in the history dump,
	 * removes stop words from each of these,
	 * applies Porter-Stemmer to stopword-free output
	 */
	@Deprecated
	public static void phase2Old() {
		
		// Make sure to configure DeimosConfig.LIMIT_URLS_DOWNLOADED !
		
		TextFromURL.fetchTextFromHistoryDump(DeimosConfig.FILE_OUTPUT_HISTORY);
		System.out.println();
		
		StopWordsRemoval.removeStopWordsFromURLTexts();
		System.out.println();
		
		StemmerApplier.applyStemmerOnSWFreeOutput();
	}
	
	public static void main(String[] args)
	{	
		
		long startTime = System.currentTimeMillis();
		
		System.out.println("All Phase 2 operations started together.\n");
		
		// remove hardcode
		phase2(1);
		
		long stopTime = System.currentTimeMillis();
		
		System.out.println("\nAll Phase 2 operations complete. Took "+TimeUtils.formatHmss(stopTime-startTime));
	}
}
