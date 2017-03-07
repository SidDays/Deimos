package deimos.phase2.collection;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLProtocolException;

public class PageFetcher {
	
	/**
	 * Returns a String containing only the textual contents of a web page.
	 * @param url The URL to visit
	 * @return a String containing the text of that web page.
	 * @throws IOException while trying to get the Document for the Connection
	 */
	public static String fetchHTML(String url)
			throws IOException,
			IllegalArgumentException, SocketException,
			SocketTimeoutException, HttpStatusException,
			UnknownHostException, SSLHandshakeException,
			SSLProtocolException {
		
		// TODO Re-throw all these exceptions
		String html = "";
		
		try {
			Document doc = Jsoup.connect(url).get();

			// String html = doc.html();
			// This above, returns the entire html

			
			html = doc.text();
			// This returns only the text

			// further stripping required for stuff like '?'
			
		} catch (IllegalArgumentException e) {
			System.out.println("Invalid URL - missed a protocol?\n");
			e.printStackTrace();
		}
		catch (SocketException se) {
			
			System.err.println("SocketException, Malformed or blank URL?");
		}
		catch (SocketTimeoutException ste) {
			System.err.println("SocketTimeout - text took too long to access.");
		}
		catch (HttpStatusException hse) {
			System.err.println("HttpStatusException.");
		}
		catch (UnknownHostException uhe) {
			System.err.println("UnknownHostException.");
		}
		catch (SSLHandshakeException sshe) {
			System.err.println("SSLHandshakeException.");
		}
		catch (SSLProtocolException spe) {
			System.err.println("SSLProtocolException.");
		}

		return html;
	}
	
	/**
	 * Saves the textual contents of a web page to a filename specified.
	 * @param fileName The name of output file.
	 * @param url The URL to visit
	 */
	@Deprecated
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
	@Deprecated
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
