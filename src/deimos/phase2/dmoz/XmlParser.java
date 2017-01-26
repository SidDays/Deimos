package deimos.phase2.dmoz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Parses XML data.
 * 
 * Reference:
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
	// final public static String FILE_XML_EXAMPLE = "E:/Downloads/Padhai/Deimos/Dmoz/content.rdf.u8/content.rdf.u8";
	final public static String FILE_XML_EXAMPLE2 = "export-dmoz.xml";

	public static void removeRecursively(Node node, short nodeType, String name) {
		if (node.getNodeType()==nodeType && (name == null || node.getNodeName().equals(name))) {
			node.getParentNode().removeChild(node);
		}
		else {
			// check the children recursively
			NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				removeRecursively(list.item(i), nodeType, name);
			}
		}
	}

	public static final void prettyPrint(Document xml)
			throws TransformerConfigurationException, TransformerException, IOException
	{
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		// Writer out = new StringWriter();
		FileWriter fw = new FileWriter(FILE_XML_EXAMPLE2);
		tf.transform(new DOMSource(xml), new StreamResult(fw));
		System.out.println("Exported cleaned XML to "+FILE_XML_EXAMPLE2+".");
	}
	
	public static void removeNodes()
			throws TransformerConfigurationException, TransformerException,
			ParserConfigurationException, FileNotFoundException, SAXException,
			IOException
	{
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		Document doc = db.parse(new FileInputStream(new File(FILE_XML_EXAMPLE)));
		
		// remove all elements named 'item'
		removeRecursively(doc, Node.ELEMENT_NODE, "ExternalPage");

		// remove all comment nodes
		// removeRecursively(doc, Node.COMMENT_NODE, null);
		
		// Normalize the DOM tree, puts all text nodes in the
		// full depth of the sub-tree underneath this node
		doc.normalize();
		
		prettyPrint(doc);
	}

	public static void main(String[] args) {

		try {
			removeNodes();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
