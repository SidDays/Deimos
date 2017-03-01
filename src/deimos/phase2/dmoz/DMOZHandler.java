package deimos.phase2.dmoz;

import java.util.List;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
	
    private boolean inTopic = false;
    // private boolean inLink = false;
    
    /**
     * Grabs the content inside a node (topics, for now).
     * Doesn't seem to be required for now.
     */
    private StringBuilder content;
    
    public DMOZHandler() {
        content = new StringBuilder();
        countTopics = 0;
        countURLs = 0;
        currentTopicURLs = new ArrayList<>();
    }
    
    @Override
    public void startElement(String namespaceURI, String localName,
    		String qualifiedName, Attributes atts) throws SAXException
    {
    	if(localName.equalsIgnoreCase("RDF")) {
    		System.out.println("Root element of the document: "+localName);
    	}
    	
        if(localName.equalsIgnoreCase("Topic")) {
        	inTopic = true;
        	currentTopicName = atts.getValue("r:id");
        	content.setLength(0); // Not required, produces 'catid' numbers
        	currentTopicURLs.clear(); // Start a fresh list of URLs
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
        	
        	countTopics++;
        }
        
        if(localName.equalsIgnoreCase("link") || 
        		localName.equalsIgnoreCase("link1"))
        {
        	// inLink = false;
        	// System.out.println("Link:\t"+link);
        	currentTopicURLs.add(currentURL);
        	
        	countURLs++;
        }
        
        if(localName.equalsIgnoreCase("RDF")) {
    		System.out.println("\nTotal Topics parsed: "+countTopics);
    		System.out.println("Total URLs parsed: "+countURLs);
    	}
    }
    
    // TODO This
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
    	// Catid doesn't seem to be required.
    	if(inTopic)
    		content.append(ch, start, length);
    }
}