package deimos.phase2.user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deimos.common.DeimosConfig;
import deimos.phase2.DBOperations;
import deimos.phase2.PageFetcher;
import deimos.phase2.StemmerApplier;
import deimos.phase2.StopWordsRemoval;

public class UserOperations {
	
	static String urlTimestampSubst;
	
	static String timestampFormat = "YYYY-MM-DD HH24:MI:SS";
	
	static String urlSubst;
	
	static String currentURL;
	
	static String currentTimestamp;
	
	static String currentURLText;
	
	static String query;
	
	/** Store all URLs in user history. */
	static List<String> urls = new ArrayList<String>();
	
	/** Store all timestamps in user history for respective URLs. */
	static List<String> urlTimeStamps = new ArrayList<String>();
	
	static Map<String, Integer> currentURLTermCounts;
	
	static int noOfURLs = DeimosConfig.LIMIT_URLS_DOWNLOADED;
	static int user_id = 1;
	
	static DBOperations dbo;

	/**
	 * Prepare the List of urls in user browsing history;
	 * load the history text file and parse it.
	 * @param historyFileName The location of the history text file.
	 */
	public static void prepareHistory(String historyFileName)
	{
		File historyFile = new File(historyFileName); 
		
		try {
			FileReader fileReader = new FileReader(historyFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				urls.add(line);
			}
			fileReader.close();
			
			// remove the first line that has the number of URLs
			urls.remove(0);
			
			// Convert the URLs into URL + Timestamp
			for (int i = 0; i < urls.size(); i++)
			{
				urlTimestampSubst = urls.get(i).substring(0, urls.get(i).indexOf('|'));
				urlTimeStamps.add(urlTimestampSubst);

				urlSubst = urls.get(i).substring(urls.get(i).indexOf('|') + 1);
				
				// System.out.println(urlTimestampSubst+" "+urlSubst);
				
				// Replace the URL+Timestamp with only URL
				urls.set(i, urlSubst);
			}
			System.out.format("Finished parsing user history of %d URL(s).\n", urls.size());
			
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Prepare the List of urls in user browsing history,
	 * by default, use the exported output text file.
	 */
	public static void prepareHistory()
	{ 
		prepareHistory(DeimosConfig.FILE_OUTPUT_HISTORY);
	}
	
	static void tfTableInsertion()
	{
		try
		{
			currentURLTermCounts.clear();
			Map<String, Integer> porter = StemmerApplier.stemmedWordsAndCount(currentURLText);
			for (Map.Entry<String, Integer> entry : porter.entrySet())
			{
				String stemmedWord = entry.getKey();
				Integer porterCount = entry.getValue();

				// Update currentTopicTermCounts
				// If it is not in the HashMap, null will be returned.
				Integer existingCount = currentURLTermCounts.get(stemmedWord);
				if(existingCount == null)
					currentURLTermCounts.put(stemmedWord, 1);
				else
					currentURLTermCounts.put(stemmedWord, existingCount + porterCount);
			}
			
			// Print currentTopicTermCounts
			// System.out.println(Collections.singletonList(currentTopicTermCounts));
			
			// Put this stuff into the table
			int termsAlreadyInTable = 0;
			int totalTerms = currentURLTermCounts.size();
			for (Map.Entry<String, Integer> entry : currentURLTermCounts.entrySet())
			{
				String term = entry.getKey();
				
				if(term.length() > 50)
					term = term.substring(0, 50);
				
				Integer tf = entry.getValue();
				query = String.format(
						"INSERT INTO tf_users (user_id, url, term, tf, weight) VALUES (%d, '%s', '%s', %d, null)",
						user_id,
						currentURL,
						term,
						tf
						);
				
				try
				{
					dbo.executeUpdate(query);
				}
				catch (SQLIntegrityConstraintViolationException sicve) {
					termsAlreadyInTable++;
				}
			}
			if(termsAlreadyInTable > 0)
				System.out.format("%d/%d URL terms already in tf_users.\n", termsAlreadyInTable, totalTerms);
			else
				System.out.println("Inserted into tf_users.");
		}
		catch (SQLException e) {
			
			// e.printStackTrace();
			System.out.println(e);
		}
	}

	public static void userAndTFTableInsertion()
	{
		// Initialize everything
		currentURLTermCounts = new HashMap<>();

		try {
			dbo = new DBOperations();
		} catch (SQLException e) {

			e.printStackTrace();
		}

		// USE WITH CAUTION!
		dbo.truncateAllUserTables();

		// Prepare the urls List.
		prepareHistory();
		
		System.out.println("\nFetching pages and populating users and tf_users...");
		for (int i = 0; (i < noOfURLs && i < urls.size()) ; i++)
		{
			currentTimestamp = urlTimeStamps.get(i);

			currentURL = urls.get(i);

			// Only for printing
			int displayLen = 40;
			String displayURL = currentURL.replace("https://","").replace("http://","");
			if(displayURL.length() < displayLen)
				displayURL = String.format("%"+displayLen+"s", displayURL);
			else
				displayURL = displayURL.substring(0, displayLen-3)+"...";
			
			System.out.format("%6d | ", i);
			System.out.print(currentTimestamp+" | "+displayURL + " | ");
			
			try
			{
				currentURLText = PageFetcher.fetchHTML(currentURL);
				if(currentURLText.isEmpty())
    				throw new Exception();

				currentURLText = StopWordsRemoval.removeStopWordsFromString(currentURLText);

				// System.out.println("URL Text:"+currentURLText);

				query = String.format(
						"INSERT INTO users (user_id, url_timestamp, url) "
						+ "VALUES (%d, TO_TIMESTAMP('%s', 'YYYY-MM-DD HH24:MI:SS'), '%s')",
						user_id, currentTimestamp, currentURL);

				// System.out.println(query);
				try
				{
					dbo.executeUpdate(query);
					System.out.print("Inserted into users | ");
				}
				catch (SQLIntegrityConstraintViolationException sicve) {
					System.out.print("Already in users | ");
				}

				tfTableInsertion();
			}
			catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception ex) {
				System.out.println("Skipping this URL.");
			}
		}
		
		System.out.println();
		System.out.println("\nFinished fetching pages and populating users and tf_users!");
	}

	public static void main(String[] args) {
		userAndTFTableInsertion();
	}
}
