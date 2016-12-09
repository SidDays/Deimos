package deimos.phase1;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.ipify.Ipify;

public class ExportIP {
	
	private static PrintStream fileStream;
	
	public static String retrievePublicIP() {
		
		String ipAddress = null;
		
		try {
			ipAddress = Ipify.getPublicIp();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return ipAddress;
	}
	
	public static void retrievePublicIPAsFile(String fileName) {
		
		int count = 0;
		String output = retrievePublicIP();	

		try {
			fileStream = new PrintStream(new File(fileName));
			fileStream.println(output);
			count++;
			
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		System.out.println(count + " public IP exported.");
	}
	
	public static void main(String args[]) {
		
		retrievePublicIPAsFile("export-publicIP.txt");
	}

}
