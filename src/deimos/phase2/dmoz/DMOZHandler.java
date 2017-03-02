package deimos.phase2.dmoz;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import deimos.phase2.DBOperations;
import deimos.phase2.PageFetcher;
import deimos.phase2.StemmerApplier;
import deimos.phase2.StopWordsRemoval;

/**
 * Reference:
 * 
 * nickcharlton.net/posts/guide-to-sax-in-java.html
 * stackoverflow.com/questions/10795121/getting-sax-parser-attributes
 * 
 * @author Siddhesh Karekar
 */

public class DMOZHandler extends DefaultHandler
{
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
    
    public DMOZHandler() throws SQLException
    {
        content = new StringBuilder();
        countTopics = 0;
        countURLs = 0;
        currentTopicURLs = new ArrayList<>();
        dbo = new DBOperations();
        dbo.clearAllTables();
        currentTopicTermCounts = new HashMap<>();
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
        	content.setLength(0); // Not required, produces 'catid' numbers
        	currentTopicURLs.clear(); // Start a fresh list of URLs
        	currentTopicTermCounts.clear();
        	
        	try
        	{
        		// Populate topics_children (parent-child hierarchy)
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
	            	
	            	query = "INSERT INTO topics_children (topic_name, child_name) VALUES ('" +
	        				parentName + "','" + currentTopicName+ "')";
	            	dbo.executeUpdate(query);

       		 	}
        	}
        	catch (SQLException e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
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
        if(localName.equalsIgnoreCase("Topic")) {
        	inTopic = false;
        	System.out.println("\nTopic:\t"+currentTopicName);
        	System.out.println("CatID:\t"+content.toString().trim());
        	
        	// Print all urls in current list
        	for(String link : currentTopicURLs)
        	{
        		System.out.println("Link:\t"+link);
        	}
        	
        	// Insert this shit into the table.
			for (Map.Entry<String, Integer> entry : currentTopicTermCounts.entrySet())
			{
				String term = entry.getKey();
				
				if(term.length() > 50)
					term = term.substring(0, 50);
				
				Integer tf = entry.getValue();
				
				String query = String.format(
						"INSERT INTO tf_weight (topic_name, term, tf, weight) VALUES ('%s', '%s', %d, null)",
						currentTopicName,
						term,
						tf
						);
				// System.out.println(query);
				try {
					dbo.executeUpdate(query);
				} catch (SQLException e) {
					
					// e.printStackTrace();
					System.out.println(e);
				}
			}
        	
        	countTopics++;
        }
        
        if(localName.equalsIgnoreCase("link") || 
        		localName.equalsIgnoreCase("link1"))
        {
        	// inLink = false;
        	// System.out.println("Link:\t"+link);
        	
        	// DON'T ADD THE LINK UNLESS EVERYTHING GOES FINE!
        	
        	// Populate topics (topics and URLs)
        	try {
        		query = "INSERT INTO topics (topic_name, url) VALUES ('" +
        				currentTopicName + "','" + currentURL+ "')";

        		dbo.executeUpdate(query);
        	} 
        	catch (SQLException e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	// Fetch current web page text
        	try
        	{
        		// System.out.println();
        		System.out.println("Current URL: "+currentURL);
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
				
				
			} catch (IOException e) {

				e.printStackTrace();
			} catch (Exception ex) {
				System.err.println("Skipping this URL.");
			}
        	        	
        	countURLs++;
        }
        
        
        if(localName.equalsIgnoreCase("RDF")) {
    		System.out.println("\nTotal Topics parsed: "+countTopics);
    		System.out.println("Total URLs parsed: "+countURLs);
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