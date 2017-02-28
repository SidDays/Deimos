package deimos.phase2.dmoz;

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
	private int countTopics;
	
	/**
	 * When a Topic node is entered, the name (r:id)
	 * is stored inside this.
	 */
	private String topicName;
	
	/**
	 * When a link node is entered, the link (r:resource)
	 * is stored inside this.
	 */
	private String link;
	
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
        	topicName = atts.getValue("r:id");
        	content.setLength(0); // Not used produces 'catid' numbers
        }
        if(localName.equalsIgnoreCase("link") || 
        		localName.equalsIgnoreCase("link1"))
        {
        	// inLink = true;
        	link = atts.getValue("r:resource"); 
        }
    }
    
    @Override
    public void endElement(String namespaceURI, String localName,
    		String qualifiedName) throws SAXException
    {
        if(localName.equalsIgnoreCase("Topic")) {
        	inTopic = false;
        	System.out.println("\nTopic:\t"+topicName);
        	System.out.println("CatID:\t"+content.toString().trim());
        	countTopics++;
        }
        
        if(localName.equalsIgnoreCase("link") || 
        		localName.equalsIgnoreCase("link1"))
        {
        	// inLink = false;
        	System.out.println("Link:\t"+link);
        }
        
        if(localName.equalsIgnoreCase("RDF")) {
    		System.out.println("\nTotal: "+countTopics);
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