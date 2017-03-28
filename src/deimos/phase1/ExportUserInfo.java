package deimos.phase1;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import deimos.common.DeimosConfig;

public class ExportUserInfo {
	
	private static PrintStream fileStream;
	
	/**
	 * Outputs the specified parameters to a text file, in a single line
	 * separated by DeimosConfig.DELIM (> at the time of writing.)
	 * 
	 * @param firstName First name, e.g. "John"
	 * @param lastName Last name, e.g. "Doe"
	 * @param gender Expected to be "male" or "female", not validated.
	 * @param yearOfBirth User's Birth year, not validated.
	 * @param location User's address.
	 * @param fileName The name of the output file.
	 */
	public static void retrieveUserInfoAsFile(String firstName, String lastName,
			String gender, int yearOfBirth, String location, String fileName) {
		
		if (firstName == null || firstName.isEmpty())
			firstName = "null";
		
		if (lastName == null || lastName.isEmpty())
			lastName = "null";
		
		if (location == null || location.isEmpty())
			location = "null";
		
		int count = 0;
		String output = firstName + DeimosConfig.DELIM +
				lastName + DeimosConfig.DELIM +
				gender + DeimosConfig.DELIM +
				yearOfBirth + DeimosConfig.DELIM +
				location;	

		try {
			fileStream = new PrintStream(new File(fileName));
			fileStream.println(output);
			count++;
			
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			if(fileStream != null)
				fileStream.close();
		}
		
		System.out.println(count + " user data record exported to "+fileName+ ".");
	}
	
	/**
	 * Outputs the specified parameters to a text file, in a single line
	 * separated by DeimosConfig.DELIM (> at the time of writing.)
	 * <br><br>
	 * Provides backward compatibility for the old implementation
	 * that did not include 'location' as a paremeter.
	 */
	public static void retrieveUserInfoAsFile(String firstName, String lastName,
			String gender, int yearOfBirth, String fileName)
	{
		retrieveUserInfoAsFile(firstName, lastName, gender, yearOfBirth, "null", fileName);
	}
	
	public static void main(String args[]) {
		
		retrieveUserInfoAsFile("John", "Doe", "male", 1995, DeimosConfig.FILE_OUTPUT_USERINFO);
	}

}
