package deimos.phase2.ref;

import java.io.FileInputStream;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import deimos.common.DeimosConfig;

/**
 * Parses DMOZ data to prepare the reference ontology.
 * 
 * Reference:
 * https://nickcharlton.net/posts/guide-to-sax-in-java.html
 * 
 * @author Siddhesh Karekar
 */
public class RefTopicsHierarchyTFParser
{
	/**
	 * 
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

}
