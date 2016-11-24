package deimos.phase2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class RemoveStopWords {
	public static void main(String[] args) {
		File folder;
		File[] listOfFiles;
		List<String> stopWords;
		String[] lineWords;
		String[] allFiles;
		List<String> fileWords;
		List<String> union;
		List<String> intersection;
		
		try {
			folder = new File("C:\\Users\\Owner\\git\\Deimos-BE-A-2017-KJSCE\\output\\urltexts");
			listOfFiles = folder.listFiles();
			stopWords = new ArrayList<String>();
			fileWords = new ArrayList<String>();
			
			System.out.println(listOfFiles.length);

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					System.out.println(i + 1 + "	" + listOfFiles[i].getName());
				} else if (listOfFiles[i].isDirectory()) {
					System.out.println("Directory " + listOfFiles[i].getName());
				}
			}
			
			allFiles = new String[listOfFiles.length];
			for (int i = 0; i < listOfFiles.length; i++) {
				allFiles[i] = listOfFiles[i].getName().toString();
			}
			File stopWordFile = new File("C:\\Users\\Owner\\git\\Deimos-BE-A-2017-KJSCE\\Stopwords.txt");

			FileReader fileReader = new FileReader(stopWordFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				stopWords.add(line);
			}
			fileReader.close();
			
			/**for (int i = 0; i < stopWords.size(); i++) {
				System.out.println(stopWords.get(i));
			}*/
			
			File inputFile = new File("C:\\Users\\Owner\\git\\Deimos-BE-A-2017-KJSCE\\output\\urltexts\\2A914F7F0183A96455FE15F96747F488.txt");
			//System.out.println(inputFile);
			
			File theDir = new File("C:\\Users\\Owner\\git\\Deimos-BE-A-2017-KJSCE\\output\\Stop Word free texts");

			// if the directory does not exist, create it
			if (!theDir.exists()) {
			    System.out.println("creating directory: Stop Word free texts");
			    boolean result = false;

			    try{
			        theDir.mkdir();
			        result = true;
			    } 
			    catch(SecurityException se){
			        //handle it
			    }        
			    if(result) {    
			        System.out.println("New directory created");  
			    }
			}
			
			File outputFile = new File("C:\\Users\\Owner\\git\\Deimos-BE-A-2017-KJSCE\\output\\Stop Word free texts\\2A914F7F0183A96455FE15F96747F488.txt");
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

			String currentLine;
			
			while((currentLine = reader.readLine()) != null) {
			    // trim newline 
			    String trimmedLine = currentLine.trim();
			    lineWords = trimmedLine.toLowerCase().split(" ");
			    for(int j = 0; j < lineWords.length; j++)
			    	fileWords.add(lineWords[j]);
			}
			System.out.println(fileWords);
			union = new ArrayList<String>(stopWords);
			union.addAll(fileWords);
			intersection = new ArrayList<String>(stopWords);
			intersection.retainAll(fileWords);
			union.removeAll(intersection);
			intersection = new ArrayList<String>(stopWords);
			intersection.retainAll(union);
			union.removeAll(intersection);
			for(String s: union) {
				writer.write(s+" ");
			}
			writer.close(); 
			reader.close();
			
			
			
			/**for(int i = 0; i < listOfFiles.length; i++) {
					String fileName = listOfFiles[i].getName().toString();
					System.out.println(fileName);
					File inputFile = new File(fileName+".txt");
					
					File theDir = new File("C:\\Users\\Owner\\git\\Deimos-BE-A-2017-KJSCE\\output\\Stop Word free texts");

					// if the directory does not exist, create it
					if (!theDir.exists()) {
					    System.out.println("creating directory: Stop Word free texts");
					    boolean result = false;

					    try{
					        theDir.mkdir();
					        result = true;
					    } 
					    catch(SecurityException se){
					        //handle it
					    }        
					    if(result) {    
					        System.out.println("New directory created");  
					    }
					}
					File outputFile = new File("C:\\Users\\Owner\\git\\Deimos-BE-A-2017-KJSCE\\output\\Stop Word free texts\\"+fileName+".txt");
					BufferedReader reader = new BufferedReader(new FileReader(inputFile));
					BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

					String currentLine;
					
					while((currentLine = reader.readLine()) != null) {
					    // trim newline 
					    String trimmedLine = currentLine.trim();
					    lineWords = trimmedLine.toLowerCase().split(" ");
					    for(int j = 0; j < lineWords.length; j++)
					    	fileWords.add(lineWords[j]);
					}
					System.out.println(fileWords);
					union = new ArrayList<String>(stopWords);
					union.addAll(fileWords);
					intersection = new ArrayList<String>(stopWords);
					intersection.retainAll(fileWords);
					union.removeAll(intersection);
					intersection = new ArrayList<String>(stopWords);
					intersection.retainAll(union);
					union.removeAll(intersection);
					for(String s: union) {
						writer.write(s+" ");
					}
					writer.close(); 
					reader.close();

			}*/
		} catch (Exception e) {
		}
	}
}
