package deimos.phase2;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class PageFetcher {
	
	/**
	 * Returns a String containing only the textual contents of a web page.
	 * @param url The URL to visit
	 * @return a String containing the text of that web page.
	 * @throws IOException while trying to get the Document for the Connection
	 */
	public static String fetchHTML(String url) throws IOException {

		try {
			Document doc = Jsoup.connect(url).get();

			// String html = doc.html();
			// This above, returns the entire html

			String html = doc.text();
			// This returns only the text

			// further stripping required for stuff like '?'

			return html;
			
		} catch (IllegalArgumentException e) {
			System.out.println("ERROR: invalid URL - missed a protocol?\n");
			e.printStackTrace();
		}

		return "";
	}
	
	/**
	 * Saves the textual contents of a web page to a filename specified.
	 * @param fileName The name of output file.
	 * @param url The URL to visit
	 */
	
	public static void fetchHTMLAsFile(String fileName, String url) {
		PrintStream fileStream;
		try {
			String urlText = fetchHTML(url);
			try {
				fileStream = new PrintStream(new File(fileName));

				// System.out.println(output.get(i));
				fileStream.println(urlText);
				
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves the textual contents of a web page specified as the first
	 * command line argument to a "URL-text.txt".
	 * @param args Only the first String is processed as the URL
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {

		if (args.length > 0) {

			String url = args[0];
			fetchHTMLAsFile("URL-text.txt", url);
			
			//System.out.println(fetchHTML(url));
		}

		else {
			System.out.println("Error: You must specify a web page to fetch as a parameter.");
		}

	}

}
