package deimos.phase2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import deimos.common.DeimosConfig;

public class StemmerApplier {
	
	final public static String PS_DIR = DeimosConfig.DIR_OUTPUT + "/pstemmedtexts";
	
	/**
	 * While Stemmer provides an implementation of the algorithm,
	 * this class uses it to stem the outputs produced on StopWordsRemoval
	 * and saves them to PS_DIR. (This implementation is similar to the 
	 * main defined in Stemmer.
	 */
	
	public static void applyStemmerOnSWFreeOutput()
	{
		int count = 0;
		
		// Specifies directory of stopword-free output
		File directory = new File(deimos.phase2.StopWordsRemoval.DIR_SWFREE);

		File[] cleanedFiles = directory.listFiles();
		String[] files = new String[cleanedFiles.length];
		String[] names = new String[cleanedFiles.length];
		
		// Make a list of all available files, and their filenames
		for (int i = 0; i < cleanedFiles.length; i++) {
			files[i] = cleanedFiles[i].getAbsolutePath();
			names[i] = cleanedFiles[i].getName();
			// System.out.println(files[i]);
		}

		// Creates empty output directory if it doesn't exist
		new File(PS_DIR).mkdirs();

		// Initialize output 'engine' (idk)
		File outputFile;
		BufferedWriter writer = null;

		// Use Stemmer on the files; mostly copied from Stemmer
		char[] w = new char[501];
		Stemmer s = new Stemmer();
		for (int i = 0; i < files.length; i++)
		{
			try
			{
				FileInputStream in = new FileInputStream(files[i]);
				try
				{
					outputFile = new File(PS_DIR + "/"+ names[i]);
					count++;
					
					writer = new BufferedWriter(new FileWriter(outputFile));
					while(true)
					{
						int ch = in.read();
						if (Character.isLetter((char) ch))
						{
							int j = 0;
							while(true)
							{
								ch = Character.toLowerCase((char) ch);
								w[j] = (char) ch;
								if (j < 500) j++;
								ch = in.read();
								if (!Character.isLetter((char) ch))
								{
									/* to test add(char ch) */
									for (int c = 0; c < j; c++)
										s.add(w[c]);

									s.stem();

									/* and now, to test toString() */
									String u = s.toString();
									
									// System.out.print(u);
									writer.write(u + " ");
									break;
								}
							}
						}
						if (ch < 0)
							break;
						// System.out.print((char)ch);
					}

					try {
						writer.close();
					}
					catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
				catch (IOException e)
				{
					System.out.println("Error reading " + files[i]);
					e.printStackTrace();
					break;
				}

				try {
					in.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			catch (FileNotFoundException e)
			{
				System.out.println("File " + files[i] + " not found.");
				break;
			}
			// System.out.println();
		}
		
		System.out.println(count + " stopword-free text(s) stemmed.");
	}
}
