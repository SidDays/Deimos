package deimos.phase1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import deimos.common.DeimosConfig;

/**
 * Contains utility functions pertaining to zipping of input-output files.
 * 
 * @author Siddhesh Karekar
 *
 */
public class Zipper {
	
	/**
	 * Zips all the output files defined in DeimosConfig
	 * into a single ZIP file.
	 */
	public static void zipOutputFiles() {
		
		/** If any errors were found. */
		// boolean errors = false;
		
		/** The number of files zipped. */
		int count_files = 0;
		
		/** The number of ZIP files created. */
		int count = 0;
		
		byte[] buffer = new byte[1024];

		try{

			FileOutputStream fos = new FileOutputStream(DeimosConfig.FILE_OUTPUT_ALL_ZIP);
			ZipOutputStream zos = new ZipOutputStream(fos);
			count++;

			// System.out.println("Output to ZIP: " + DeimosConfig.FILE_OUTPUT_ALL_ZIP);

			for(String file : DeimosConfig.FILES_OUTPUT_ALL){

				// System.out.println("File Added: " + file);
				ZipEntry ze= new ZipEntry(file);
				zos.putNextEntry(ze);

				FileInputStream in =
						new FileInputStream(file);

				int len;
				
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();
				count_files++;
			}

			zos.closeEntry();
			
			// Remember to close it.
			zos.close();
			
		}
		catch(IOException ex) {
			ex.printStackTrace();
			// errors = true;
		}
		finally {
			System.out.println(count+ " ZIP file(s) created at " + DeimosConfig.FILE_OUTPUT_ALL_ZIP + ".");
			System.out.println(count_files+ " file(s) zipped into " + DeimosConfig.FILE_OUTPUT_ALL_ZIP + ".");
		}
	}

}
