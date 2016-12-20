package deimos.phase1;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import deimos.common.DeimosConfig;

public class ExportUserInfo {
	
	private static PrintStream fileStream;
	
	public static void retrieveUserInfoAsFile(String firstName, String lastName,
			String gender, int yearOfBirth, String fileName) {
		
		if (firstName == null || firstName.isEmpty())
			firstName = "null";
		
		if (lastName == null || lastName.isEmpty())
			lastName = "null";
		
		int count = 0;
		String output = firstName + DeimosConfig.DELIM +
				lastName + DeimosConfig.DELIM +
				gender + DeimosConfig.DELIM +
				yearOfBirth;	

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
	
	public static void main(String args[]) {
		
		retrieveUserInfoAsFile("John", "Doe", "male", 1995, DeimosConfig.FILE_OUTPUT_USERINFO);
	}

}
