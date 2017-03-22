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

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLProtocolException;

/**
 * Reference:
 * stackoverflow.com/questions/17031003/jsoup-throws-url-status-503-in-eclipse-but-url-works-fine-in-browser
 * 
 * @author Siddhesh Karekar
 *
 */
public class PageFetcher {
	
	public static final int SECONDS_TO_WAIT = 2;
	
	/**
	 * Returns a String containing only the textual contents of a web page.
	 * 
	 * @param url The URL to visit
	 * @return a String containing the text of that web page.
	 * @throws Exception A large number of exceptions - these must be handled externally
	 * to avoid interruption!
	 * 
	 * @author Siddhesh Karekar
	 * @author faster2b
	 */
	public static String fetchHTML(String url)
			throws IOException,
			IllegalArgumentException, SocketException,
			SocketTimeoutException, HttpStatusException,
			UnknownHostException, SSLHandshakeException,
			SSLProtocolException, SSLException
	{

		String html = "";
		Document doc = Jsoup.connect(url)
				.userAgent("Mozilla/5.0 Chrome/26.0.1410.64 Safari/537.31")
				  .timeout(SECONDS_TO_WAIT*1000)
				  .followRedirects(true)
				  .maxBodySize(1024*1024*3)    //3Mb Max
				  //.ignoreContentType(true) //for download xml, json, etc
				  .get();
		html = doc.text();

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
