package deimos.phase2.dmoz;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Reference:
 * https://nickcharlton.net/posts/guide-to-sax-in-java.html
 * 
 * @author Siddhesh Karekar
 */

public class DMOZHandler extends DefaultHandler
{
    private boolean inTopic = false;
    
    @Override
    public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes atts) throws SAXException {
        if (localName.equals("Topic")) inTopic = true;
    }
    
    @Override
    public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
        if (localName.equals("Topic")) inTopic = false;
    }
    
    // TODO This
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
    	if(inTopic)
    	{
    		/*for (int i = start; i < start+length; i++) {
    			System.out.print(ch[i]); 
    		}*/
    	}
    }
}