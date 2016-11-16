package deimos.phase1;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class PageFetcher {

	public static String fetchHTML(String url) throws IOException {

		try {
			Document doc = Jsoup.connect(url).get();

			// String html = doc.html();
			// This above, returns the entire html

			String html = doc.text();
			// This returns only the text

			// further stripping required for '?'

			return html;
		} catch (IllegalArgumentException e) {
			System.out.println("ERROR: invalid URL - missed a protocol?\n");
			e.printStackTrace();
		}

		return "";
	}
	
	//this one method is added to link it with TextFromURL
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
