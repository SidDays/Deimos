package deimos.phase2.dmoz;

import java.io.FileInputStream;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import deimos.common.DeimosConfig;

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
            System.err.println(e); 
        }
	}

}
