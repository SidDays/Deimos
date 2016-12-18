package deimos.phase1;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class ExportUserInfo {
	
	private static PrintStream fileStream;
	private static final String DELIM = "|";
	
	public static void retrieveUserInfoAsFile(String firstName, String lastName,
			String gender, int yearOfBirth, String fileName) {
		
		int count = 0;
		String output = firstName + DELIM + lastName + DELIM + gender + DELIM + yearOfBirth;	

		try {
			fileStream = new PrintStream(new File(fileName));
			fileStream.println(output);
			count++;
			
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		System.out.println(count + " user data record exported to "+fileName+ ".");
	}
	
	public static void main(String args[]) {
		
		retrieveUserInfoAsFile("John", "Doe", "male", 1995, "export-userInfo.txt");
	}

}
