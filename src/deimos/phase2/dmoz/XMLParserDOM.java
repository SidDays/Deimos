package deimos.phase2.dmoz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Parses XML data.
 * 
 * Reference:
 * http://viralpatel.net/blogs/parsing-reading-xml-file-in-java-xml-reading-java-tutorial/
 * 
 * @author Bhushan Pathak
 * @author Siddhesh Karekar
 */

public class XMLParserDOM {
	
	/**
	 * Location of XML file to parse.
	 */
	final public static String FILE_XML_DMOZ_EXAMPLE = "resources/xmlexample.xml";
	final public static String FILE_XML_DMOZ =
			"E:/Downloads/Padhai/Deimos/Dmoz/content-noExternalPage2.rdf.u8";
	final public static boolean PRINT_LINKS = true;
	
	public void printAllTopicsWithLinks2(String fileName) throws FileNotFoundException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// dbf.setValidating(false);
		DocumentBuilder db = null;
		
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		File file = new File(fileName);
		if (file.exists())
		{
			Document doc = null;
			try {
				doc = db.parse(file);
			} catch (SAXException | IOException e) {
				e.printStackTrace();
			}
			Element docEle = doc.getDocumentElement();

			// Print root element of the document
			System.out.println("Root element of the document: "
					+ docEle.getNodeName());
			NodeList pageList = docEle.getElementsByTagName("Topic");

			// Print total 'Topic' elements in document
			System.out.println("Total: " + pageList.getLength());
			
			if(pageList != null && pageList.getLength() > 0)
			{
				for(int i = 0; i < pageList.getLength(); i++)
				{
					Node node = pageList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE)
					{
						String topicName = node.getAttributes().getNamedItem("r:id").getNodeValue();
						
						// Only for ease of printing
						if(topicName.isEmpty())
							topicName = "null";
						
						System.out.println("\nTopic:\t" + topicName);
						
						if(PRINT_LINKS)
						{
							NodeList nodeList;
							nodeList = ((Element)(node)).getElementsByTagName("link");
							for(int j = 0; j < nodeList.getLength(); j++)
							{
								Node nodeLink = nodeList.item(j);
								if (nodeLink.getNodeType() == Node.ELEMENT_NODE)
								{
									String link = nodeLink.getAttributes().
											getNamedItem("r:resource").getNodeValue();
									System.out.println("Link:\t" + link);
								}
							}
						}
					}
				}
			}
			else {
				System.err.println("Error while parsing.");
			}
		}
		else {
			throw new FileNotFoundException();
		}
	}
	
	public static void main(String[] args) {
		
		XMLParserDOM parser = new XMLParserDOM();
		try {
			parser.printAllTopicsWithLinks2(FILE_XML_DMOZ_EXAMPLE);
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		
	}
}
