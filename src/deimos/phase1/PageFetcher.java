package deimos.phase1;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

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
		}
		catch (IllegalArgumentException e) {
			System.out.println("ERROR: invalid URL - missed a protocol?\n");
			e.printStackTrace();
		}
		
		return "";
	}
	
	public static void main(String[] args) throws IOException
	{
		
		if(args.length > 0) {
			
			String url = args[0];
			
			System.out.println(fetchHTML(url));
		}
		
		else {
			System.out.println(
					"Error: You must specify a web page to fetch as a parameter.");
		}
		
	}

}
