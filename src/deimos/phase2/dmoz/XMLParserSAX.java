package deimos.phase2.dmoz;

import java.io.FileInputStream;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Parses XML data.
 * 
 * Reference:
 * https://nickcharlton.net/posts/guide-to-sax-in-java.html
 * 
 * @author Siddhesh Karekar
 */
public class XMLParserSAX
{
	/**
	 * Location of XML file to parse.
	 */
	// final public static String FILE_XML_DMOZ = "resources/xmlexample.xml";
	final public static String FILE_XML_DMOZ = "E:/Downloads/Padhai/Deimos/Dmoz/content-noExternalPage2.rdf.u8";
	
	public static void main(String[] args)
	{
		try
		{
            // specify the SAXParser
            XMLReader parser = XMLReaderFactory.createXMLReader(
                "com.sun.org.apache.xerces.internal.parsers.SAXParser"
            );
            
            // setup the handler
            ContentHandler handler = new DMOZHandler();
            parser.setContentHandler(handler);
            
            // open the file
            FileInputStream in = new FileInputStream(FILE_XML_DMOZ);
            InputSource source = new InputSource(in);
            
            // parse the data
            parser.parse(source);
            
            // print an empty line under the data
            System.out.println("Parsed successfully!");
            
            // close the file
            in.close();
        }
        catch (Exception e) {
            System.err.println(e); 
        }
	}

}
