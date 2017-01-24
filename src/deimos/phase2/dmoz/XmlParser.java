package deimos.phase2.dmoz;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Parses XML data.
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

	public static void removeRecursively(Document doc, Node node, short nodeType, String name) {
		if (node.getNodeType()==nodeType && (name == null || node.getNodeName().equals(name))) {
			node.getParentNode().removeChild(node);
		}
		else {
			NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				removeRecursively(doc, list.item(i), nodeType, name);
			}
		}
		Transformer transformer;
		StreamResult result = null;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		String xmlString = result.getWriter().toString();
		System.out.println(xmlString);
	}

	public void printXML(Document doc) throws TransformerException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);

		String xmlString = result.getWriter().toString();
		System.out.println(xmlString);
	}

	public static final void prettyPrint(Document xml) throws Exception {
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		Writer out = new StringWriter();
		tf.transform(new DOMSource(xml), new StreamResult(out));
		System.out.println(out.toString());
	}

	public void getAllExternalPages(String fileName) throws Exception {
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

			removeRecursively(doc, doc, Node.ELEMENT_NODE, "ExternalPage");
			//prettyPrint(doc);
			//printXML(doc);
			/**NodeList pageList = docEle.getElementsByTagName("ExternalPage");

			// Print total student elements in document
			System.out.println("Total: " + pageList.getLength());

			/*
			for(int i = 0; i < pageList.getLength(); i++) {
				System.out.println(pageList.item(i));
			}


			if(pageList != null && pageList.getLength() > 0)
			{
				for(int i = 0; i < pageList.getLength(); i++)
				{
					Node node = pageList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {

						System.out.println();
						String attribute = pageList.item(i).getAttributes().getNamedItem("about").getNodeValue();
						System.out.println("ExternalPage's attribute about: "+attribute);

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
			}*/
		}
	}

	public static void main(String[] args) {

		XmlParser parser = new XmlParser();
		try {
			parser.getAllExternalPages(FILE_XML_EXAMPLE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
