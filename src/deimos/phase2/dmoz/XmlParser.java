package deimos.phase2.dmoz;

import java.io.File;
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
 * (unused now)
 * examples.javacodegeeks.com/core-java/xml/dom/remove-nodes-from-dom-document-recursively
 * stackoverflow.com/questions/11883294/writing-to-txt-file-from-stringwriter
 * 
 * @author Bhushan Pathak
 * @author Siddhesh Karekar
 *
 */

public class XmlParser {
	
	/**
	 * Location of XML file to parse.
	 */
	final public static String FILE_XML_EXAMPLE = "resources/xmlexample.xml";
	// final public static String FILE_XML_EXAMPLE = "E:/Downloads/Padhai/Deimos/Dmoz/content.rdf.u8/content-noExternalPage.rdf.u8";
	final public static String FILE_XML_OUTPUT = "export-dmoz.xml";
	
	public void getAllExternalPages(String fileName) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		File file = new File(fileName);
		if (file.exists()) {
			Document doc = null;
			try {
				doc = db.parse(file);
			} catch (SAXException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Element docEle = doc.getDocumentElement();

			// Print root element of the document
			System.out.println("Root element of the document: "
					+ docEle.getNodeName());
			NodeList pageList = docEle.getElementsByTagName("ExternalPage");

			// Print total student elements in document
			System.out.println("Total: " + pageList.getLength());
			
			/*
			for(int i = 0; i < pageList.getLength(); i++) {
				System.out.println(pageList.item(i));
			}
			*/
			
			if(pageList != null && pageList.getLength() > 0)
			{
				for(int i = 0; i < pageList.getLength(); i++)
				{
					Node node = pageList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {

						System.out.println();

						Element e = (Element) node;
						NodeList nodeList = e.getElementsByTagName("d:Title");
						System.out.println("ExternalPage:\t"
								+ nodeList.item(0).getChildNodes().item(0)
										.getNodeValue());
						
						
						nodeList = e.getElementsByTagName("d:Description");
						String d = nodeList.item(0).getChildNodes().item(0)
								.getNodeValue();
						d.replace("\t", ""); // Not working?
						System.out.println("Description:\t"
								+ d);

						nodeList = e.getElementsByTagName("topic");
						System.out.println("Topic:\t\t"
								+ nodeList.item(0).getChildNodes().item(0)
										.getNodeValue());
					}
				}
			}
			
			else {
				System.err.println("Error while parsing!!!");
			}
		}
	}
	
	public static void main(String[] args) {
		
		XmlParser parser = new XmlParser();
		parser.getAllExternalPages(FILE_XML_EXAMPLE);
	}
}
