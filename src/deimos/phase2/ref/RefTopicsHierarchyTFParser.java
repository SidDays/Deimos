package deimos.phase2.ref;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLProtocolException;

import org.jsoup.HttpStatusException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import deimos.common.DeimosConfig;
import deimos.common.StatisticsUtilsCSV;
import deimos.phase2.DBOperations;
import deimos.phase2.collection.PageFetcher;
import deimos.phase2.collection.StemmerApplier;
import deimos.phase2.collection.StopWordsRemoval;

/**
 * Parses DMOZ data to prepare the reference ontology.
 * 
 * Reference:
 * https://nickcharlton.net/posts/guide-to-sax-in-java.html
 * stackoverflow.com/questions/10795121/getting-sax-parser-attributes
 * 
 * @author Siddhesh Karekar
 */
public class RefTopicsHierarchyTFParser
{


	// Logging functions

	/** Output the time taken per URL to a *.csv file. */
	private static boolean OPTION_RECORD_STATS_URL_TIMES = true;

	/** If false, blank the csv file */
	private static final boolean OPTION_RECORD_STATS_APPEND = false;

	/** The name of the CSV file to append info to. */
	private static final String FILENAME_STATS_URL_TIMES = "stats_ref_url_times.csv";

	private static StatisticsUtilsCSV csvStats;

	public RefTopicsHierarchyTFParser()
	{

		// Use DMOZHandler constructor for other stuff


	}

	public static int getDepth(String topicName)
	{
		int level = 0;

		for(int i = 0; i < topicName.length(); i++)
		{
			char ch = topicName.charAt(i);
			if(ch == '/')
			{
				level++;
			}
		}

		return level;
	}

	/**
	 * @param startAfresh If true, truncates all reference tables to start afresh
	 */
	public static void generateTopicsHierarchyAndTF(boolean startAfresh)
	{
		try
		{
			// specify the SAXParser
			XMLReader parser = XMLReaderFactory.createXMLReader(
					"com.sun.org.apache.xerces.internal.parsers.SAXParser"
					);

			// setup the handler - true if start fresh (truncates all ref. tables)
			ContentHandler handler = new DMOZHandler(startAfresh);
			parser.setContentHandler(handler);

			// open the file
			String dataPath;
			if(DeimosConfig.OPTION_USE_EXAMPLE_DMOZ)
				dataPath = DeimosConfig.FILE_XML_DMOZ_EXAMPLE;
			else
				dataPath = DeimosConfig.FILE_XML_DMOZ;
			FileInputStream in = new FileInputStream(dataPath);
			InputSource source = new InputSource(in);

			long startTime = System.currentTimeMillis();

			// parse the data
			parser.parse(source);

			// print an empty line under the data
			System.out.println("Parsed successfully!");

			long stopTime = System.currentTimeMillis();
			System.out.format("Link insertion, page fetching and TF calculation completed in %.3fs.\n",
					(stopTime-startTime)/1000f);

			// close the file
			in.close();

			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		// if true, truncate everything, else resume.
		generateTopicsHierarchyAndTF(false);
	}

	/**
	 * 
	 */
	private static class DMOZHandler extends DefaultHandler
	{
		/** Not more than these many links, whose pages were able to be fetched,
		 * should be added to the databse.
		 */
		public static final int LIMIT_WORKING_LINKS = 5;

		/** Go down only as many as these levels. */
		public static final int LIMIT_DEPTH = 3;

		/** The total number of Topics parsed. No use. */
		private int countTopics;

		/** The total number of URLs parsed. No use. */
		private int countURLs;

		/**
		 * When a Topic node is entered, the name (r:id)
		 * is stored inside this.
		 */
		private String currentTopicName;

		/**
		 * Keeps track of all the URLs in the current topic
		 */
		private List<String> currentTopicURLs;

		/**
		 * When a link node is entered, the link (r:resource)
		 * is stored inside this.
		 */	
		private String currentURL;

		/** The HTML contents of current URL. */
		private String currentPageText;

		/** The term-count pair map of current list of URLs. */
		private Map<String, Integer> currentTopicTermCounts;

		private String parentName;
		private String query;

		private boolean inTopic = false;
		// private boolean inLink = false;

		/**
		 * Grabs the content inside a node (topics, for now).
		 * Doesn't seem to be required for now.
		 */
		private StringBuilder content;
		private DBOperations dbo;

		public DMOZHandler(boolean startAfresh) throws SQLException
		{
			content = new StringBuilder();
			countTopics = 0;
			countURLs = 0;
			currentTopicURLs = new ArrayList<>();
			dbo = new DBOperations();

			// CAREFUL!
			if(startAfresh)
				dbo.truncateAllReferenceTables();

			currentTopicTermCounts = new HashMap<>();

			// file initialization
			if(OPTION_RECORD_STATS_URL_TIMES) {

				System.out.println("Logging for RefTopicsHierarchyTFParser is enabled.");

				try {
					csvStats = new StatisticsUtilsCSV(FILENAME_STATS_URL_TIMES, OPTION_RECORD_STATS_APPEND);

					// Header row
					if(!OPTION_RECORD_STATS_APPEND)
						csvStats.printAsCSV("Sr. No",
								"URL",
								"Time to fetch (ms)",
								"Success?",
								"Exception");
				}
				catch (IOException fnfe) {
					System.out.println(fnfe);
					System.out.println("Logging will be disabled.");
					OPTION_RECORD_STATS_URL_TIMES = false;
				}
			}

		}

		@Override
		public void startElement(String namespaceURI, String localName,
				String qualifiedName, Attributes atts) throws SAXException
		{
			if(localName.equalsIgnoreCase("RDF")) {
				System.out.println("Root element of the document: "+localName);
			}

			if(localName.equalsIgnoreCase("Topic"))
			{
				inTopic = true;
				currentTopicName = atts.getValue("r:id");

				if(getDepth(currentTopicName) <= LIMIT_DEPTH)
				{

					content.setLength(0); // Not required, produces 'catid' numbers
					currentTopicURLs.clear(); // Start a fresh list of URLs
					currentTopicTermCounts.clear();

					System.out.println("\nTopic:\t"+currentTopicName);

					try
					{
						// Populate ref_hierarchy (parent-child hierarchy)
						if(!currentTopicName.isEmpty())
						{
							int lastIndexOfSlash = currentTopicName.lastIndexOf("/");
							if(lastIndexOfSlash != -1) {
								parentName = currentTopicName.substring(0, lastIndexOfSlash);
							}
							else {
								parentName = "null";
							}

							// System.out.println("Parent: "+ parentName+" Child name: "+ currentTopicName);

							query = "INSERT INTO ref_hierarchy (topic_name, child_name) VALUES ('" +
									parentName + "','" + currentTopicName+ "')";
							dbo.executeUpdate(query);

						}
					} 
					catch (SQLIntegrityConstraintViolationException sicve) {
						System.err.format("Parent-child combo already in database.\n");
					}
					catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			if(localName.equalsIgnoreCase("link") || 
					localName.equalsIgnoreCase("link1"))
			{
				// inLink = true;
				currentURL = atts.getValue("r:resource");
			}
		}

		@Override
		public void endElement(String namespaceURI, String localName,
				String qualifiedName) throws SAXException
		{
			if(localName.equalsIgnoreCase("Topic"))
			{
				inTopic = false;

				if(getDepth(currentTopicName) <= LIMIT_DEPTH)
				{

					System.out.println("All compiled URLS for this topic: ");
					// System.out.println("CatID:\t"+content.toString().trim());

					// Print all urls in current list
					for(String link : currentTopicURLs)
					{
						System.out.println("Link:\t"+link);
					}

					// Insert this shit into the table
					int termsAlreadyInDatabase = 0; // For a clean error message output.
					for (Map.Entry<String, Integer> entry : currentTopicTermCounts.entrySet())
					{
						String term = entry.getKey();

						if(term.length() > 50)
							term = term.substring(0, 50);

						Integer tf = entry.getValue();

						String query = String.format(
								"INSERT INTO ref_tf (topic_name, term, tf, weight) VALUES ('%s', '%s', %d, null)",
								currentTopicName,
								term,
								tf
								);
						// System.out.println(query);

						try
						{
							dbo.executeUpdate(query);
						}
						catch (SQLIntegrityConstraintViolationException sicve) {
							termsAlreadyInDatabase++;

						}
						catch (SQLException e) {
							e.printStackTrace();
						}
					}
					if(termsAlreadyInDatabase > 0)
					{
						System.err.format("%d Topic-term combo(s) already in database.\n",
								termsAlreadyInDatabase);
					}

					countTopics++;
				}
			}

			if(localName.equalsIgnoreCase("link") || 
					localName.equalsIgnoreCase("link1"))
			{
				// After collecting certain number of working URLs, stop
				if(currentTopicURLs.size() < LIMIT_WORKING_LINKS)
				{
					// inLink = false;
					// System.out.println("Link:\t"+link);

					// DON'T ADD THE LINK UNLESS EVERYTHING GOES FINE!

					// Populate ref_topics (topics and URLs)
					try {
						query = "INSERT INTO ref_topics (topic_name, url) VALUES ('" +
								currentTopicName + "','" + currentURL+ "')";

						dbo.executeUpdate(query);
					} 
					catch (SQLIntegrityConstraintViolationException sicve) {
						System.err.format("Topic-URL combo already in database.\n");
					}
					catch (SQLException e) {
						e.printStackTrace();
					}

					long lStartTime, lEndTime;

					// Log to CSV
					lStartTime = Instant.now().toEpochMilli();

					// Fetch current web page text
					Exception caughtEx = null;
					try
					{
						// System.out.println();
						System.out.println("Current URL: "+currentURL);
						currentPageText = "";
						currentPageText = PageFetcher.fetchHTML(currentURL);
						if(currentPageText.isEmpty())
							throw new Exception();

						currentTopicURLs.add(currentURL);

						// System.out.format("Current page text: %s\n", currentPageText);

						// Remove its stopwords
						currentPageText =
								StopWordsRemoval.removeStopWordsFromString(currentPageText);
						// System.out.format("Stopwords removed: %s\n", currentPageText);

						// Get its porter-stemmed result
						Map<String, Integer> porter = StemmerApplier.stemmedWordsAndCount(currentPageText);
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
						// System.out.println("Added all its Porter-Stemmer pairs.");


					}
					catch (SQLException sqle) {
						sqle.printStackTrace();

						caughtEx = sqle;
						System.out.println(caughtEx);

					}
					catch (IllegalArgumentException ile) {				
						caughtEx = ile;
						System.out.println(caughtEx+ ", Invalid URL - missed a protocol?");
					}
					catch (SocketException se) {				
						caughtEx = se;
						System.out.println(caughtEx);
					}
					catch (SocketTimeoutException ste) {
						caughtEx = ste;
						System.out.println(caughtEx);
					}
					catch (HttpStatusException hse) {
						caughtEx = hse;
						System.out.println(caughtEx);
					}
					catch (UnknownHostException uhe) {
						caughtEx = uhe;
						System.out.println(caughtEx);
					}
					catch (SSLHandshakeException sshe) {
						System.err.println(sshe);
						caughtEx = sshe;
					}
					catch (SSLProtocolException spe) {
						caughtEx = spe;
						System.out.println(caughtEx);
					}
					catch (SSLException sslxe){				
						/* General_Merchandise/D
	    				Current URL: http://www.davidmorgan.com/
	    				javax.net.ssl.SSLException: java.lang.RuntimeException: Could not generate DH keypair

	    				Caused by: java.security.InvalidAlgorithmParameterException: Prime size must be multiple of 64,
	    				and can only range from 512 to 2048 (inclusive)
						 */

						caughtEx = sslxe;
						System.out.println(caughtEx);
					}
					catch (Exception ex) {
						caughtEx = ex;
						System.out.println(caughtEx);
					}
					finally
					{
						if(OPTION_RECORD_STATS_URL_TIMES)
						{
							lEndTime = Instant.now().toEpochMilli();
							String ifFetchSuccess = (currentPageText.isEmpty())?"false":"true";
							String exceptString = (caughtEx == null)?"":caughtEx.toString();
							csvStats.printAsCSV(
									String.valueOf(countURLs),
									currentURL,
									String.valueOf(lEndTime-lStartTime),
									ifFetchSuccess,
									exceptString);

						}
					}

					countURLs++;
				}
			}


			// End of whole XML/rdf.u8
			if(localName.equalsIgnoreCase("RDF")) {
				System.out.println("\nTotal Topics parsed: "+countTopics);
				System.out.println("Total URLs parsed: "+countURLs);
				
				if(OPTION_RECORD_STATS_URL_TIMES) {
					
					csvStats.closeOutputStream();
					System.out.println("Logging for UserURLsTF is finished.");
				}
			}

		}


		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
		{
			// Catid doesn't seem to be required.

			if(inTopic)
				content.append(ch, start, length);
		}

	}

}
