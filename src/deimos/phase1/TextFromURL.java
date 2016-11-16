package deimos.phase1;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;

public class TextFromURL {

	// this function deletes all the files exist in the URL texts directory
	// here also directories are local machine dependent
	public static void cleanDirectory() {
		File file = new File("C:\\Users\\Owner\\git\\Deimos-BE-A-2017-KJSCE\\URL Texts");
		String[] myFiles;
		if (file.isDirectory()) {
			myFiles = file.list();
			for (int i = 0; i < myFiles.length; i++) {
				File myFile = new File(file, myFiles[i]);
				myFile.delete();
			}
		}
	}

	public static void main(String[] args) {
		try {
			int noOfURLs; // this represents number of URLs taken into
							// consideration
			File historyFile = new File("export-history.txt"); 
		
			FileReader fileReader = new FileReader(historyFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			List<String> historyContent = new ArrayList<String>();
			List<String> urls = new ArrayList<String>();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				historyContent.add(line);
				// historyContent.add("\n");
			}
			fileReader.close();

			// till now, reading of file is done into an arraylist
			// following code gets only urls from the above arraylist

			for (int i = 1; i < historyContent.size(); i++) {
				String s = historyContent.get(i);
				// System.out.println(s);
				int index = s.indexOf('|');
				String subst = s.substring(index + 2);
				urls.add(subst);
			}

			PrintStream fileStream;

			try {
				noOfURLs = 100; // we can provide any value to it ranging from 1 to urls.size()
				fileStream = new PrintStream(new File("C:\\Users\\Owner\\git\\Deimos-BE-A-2017-KJSCE\\URL Texts\\URLs.txt")); // contains all urls
				fileStream.println("Total URLs: "+noOfURLs);

				// System.out.println(output.get(i));
				for (int i = 0; i < noOfURLs; i++) {
					if(i<10)
						fileStream.println((i+1)+"	"+urls.get(i));
					else
						fileStream.println((i+1)+"	"+urls.get(i));
				}
				int count = 1;

				cleanDirectory(); // calling the function to delete previous text files

				System.out.println("This program extracts text from fetched URLs");

				// here I am fetching only first 10 URLs but we need to fetch urls.size()!!!
				for (int i = 0; i < noOfURLs; i++) {
					if (i == 99) {
						PageFetcher.fetchHTMLAsFile("C:\\Users\\Owner\\git\\Deimos-BE-A-2017-KJSCE\\URL Texts\\URL-text "+ (count++) + ".txt", urls.get(i));
					} else {
						String currentStr = urls.get(i);
						String nextStr = urls.get(i + 1);
						URI currentURI = new URI(currentStr);
						URI nextURI = new URI(nextStr);
						if (!(currentURI.equals(nextURI)))
							PageFetcher.fetchHTMLAsFile("C:\\Users\\Owner\\git\\Deimos-BE-A-2017-KJSCE\\URL Texts\\URL-text " + (count++)+ ".txt", urls.get(i));
					}
				}
				System.out.println("All files are fetched!!!");

			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
