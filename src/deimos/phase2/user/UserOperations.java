package deimos.phase2.user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLHandshakeException;

import org.jsoup.HttpStatusException;

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
	
	static List<String> urls = new ArrayList<String>();
	
	static List<String> urlTimestamp = new ArrayList<String>();
	
	static Map<String, Integer> currentTopicTermCounts;
	
	static int noOfURLs = DeimosConfig.LIMIT_URLS_DOWNLOADED;
	static int user_id = 1;
	
	static DBOperations dbo;
	
	public static void main(String[] args) {
		fetchTextFromURL();
	}
	
	static void tfTableInsertion() {
		try {
			Map<String, Integer> porter = StemmerApplier.stemmedWordsAndCount(currentURLText);
			for (Map.Entry<String, Integer> entry : porter.entrySet())
			{
				String stemmedWord = entry.getKey();
				Integer porterCount = entry.getValue();

				// Update currentTopicTermCounts
				// If it is not in the HashMap, null will be returned.
				Integer existingCount = currentTopicTermCounts.get(stemmedWord);
				if(existingCount == null)
					currentTopicTermCounts.put(stemmedWord, 1);
				else
					currentTopicTermCounts.put(stemmedWord, existingCount + porterCount);
			}
			
			System.out.println(Collections.singletonList(currentTopicTermCounts));
			
			for (Map.Entry<String, Integer> entry : currentTopicTermCounts.entrySet())
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
				
				dbo.executeUpdate(query);
			}
		}
		catch (SQLException e) {
			// e.printStackTrace();
			System.out.println(e);
		}
	}
	
	static void usersTableInsertion() {
		try {
			for (int i = 0; (i < noOfURLs && i < urls.size()) ; i++)
			{
				currentTimestamp = urlTimestamp.get(i);

				currentURL = urls.get(i);

				/*currentURL = currentURL.substring(0, 
						Math.min(currentURL.length(), 32));*/

				System.out.println(currentTimestamp+" | "+currentURL);
				currentURLText = PageFetcher.fetchHTML(currentURL);

				currentURLText = StopWordsRemoval.removeStopWordsFromString(currentURLText);

				System.out.println();
				System.out.println("URL Text:");
				System.out.println(currentURLText);
				System.out.println();

				query = "INSERT INTO users (user_td, url_timestamp, url) VALUES ('"+ 
						user_id + "','" + "TO_TIMESTAMP('"+currentTimestamp + "','" + "'YYYY-MM-DD HH24:MI:SS'"+"')"+"','"+ currentURLText +"')";

				query = String.format(
						"INSERT INTO users (user_id, url_timestamp, url) VALUES (%d, '%s', '%s')",
						user_id,
						currentTimestamp,
						currentURL
						);

				dbo.executeUpdate(query);	
				tfTableInsertion();
			}
		}
		catch (SQLException e) {
			// e.printStackTrace();
			System.out.println(e);
		} catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SSLHandshakeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpStatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void fetchTextFromURL() {
		try {
			currentTopicTermCounts = new HashMap<>();
			dbo = new DBOperations();
			//dbo.truncateAllTables();
			File historyFile = new File("export-history.txt"); 

			FileReader fileReader = new FileReader(historyFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				urls.add(line);
			}
			fileReader.close();

			// remove the first line that has the number of URLs
			urls.remove(0);

			System.out.println("---------------------------------------------");
			for (int i = 0; i < noOfURLs; i++) {

				urlTimestampSubst = urls.get(i).substring(0, urls.get(i).indexOf('|'));
				System.out.println(urlTimestampSubst);
				urlTimestamp.add(urlTimestampSubst);

				urlSubst = urls.get(i).substring(urls.get(i).indexOf('|') + 1);
				System.out.println(urlSubst);
				System.out.println("---------------------------------------------");
				urls.set(i, urlSubst);
			}
			
			usersTableInsertion();
			
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception ex) {
			System.err.println("Skipping this URL.");
		}

	}
}
