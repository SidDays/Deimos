package deimos.phase1;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class TextFromURL {
	
	// this function deletes all the files exist in the URL texts directory
	 
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
			File historyFile = new File("\\C:\\Users\\Owner\\git\\Deimos-BE-A-2017-KJSCE\\export-history.txt"); //location of history text file, machine dependent!
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
			
			//till now, reading of file is done into an arraylist
			//following code gets only urls from the above arraylist
			
			for (int i = 1; i < historyContent.size(); i++) {
				String s = historyContent.get(i);
				// System.out.println(s);
				int index = s.indexOf('|');
				String subst = s.substring(index + 2);
				urls.add(subst);
			}

			PrintStream fileStream;

			try {
				fileStream = new PrintStream(new File("URLs.txt")); //contains all urls
				fileStream.println(urls.size());
				
				// System.out.println(output.get(i));
				
				for (int i = 0; i < urls.size(); i++)
					fileStream.println(urls.get(i));
				int count = 1;
				
				cleanDirectory(); //calling the function to delete previous text files 
				
				System.out.println("This program extracts text from fetched URLs");
				//here I am fetching only first 10 URLs but we need to fetch urls.size()!!!
				for (int i = 0; i < 10; i++)
					PageFetcher.fetchHTMLAsFile("C:\\Users\\Owner\\git\\Deimos-BE-A-2017-KJSCE\\URL Texts\\URL-text " + (count++) + ".txt", urls.get(i));
				System.out.println("All files are fetched!!!");
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
