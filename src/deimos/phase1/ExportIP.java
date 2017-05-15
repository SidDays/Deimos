package deimos.phase1;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.UnknownHostException;

import org.ipify.Ipify;

import deimos.common.DeimosConfig;
import deimos.common.TimeUtils;

public class ExportIP {
	
	private static PrintStream fileStream;
	
	public static String retrievePublicIP() throws UnknownHostException {
		
		String ipAddress = null;
		
		try {
			ipAddress = Ipify.getPublicIp();
		}
		catch (UnknownHostException uhe) {
			throw uhe;
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return ipAddress;
	}
	
	public static void retrievePublicIPAsFile(String fileName) throws UnknownHostException
	{
		
		long startTime = System.currentTimeMillis();
		
		int count = 0;
		try
		{
			String output = retrievePublicIP();	

			try {
				fileStream = new PrintStream(new File(fileName));
				fileStream.println(output);
				count++;
				
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
			
		} catch (UnknownHostException e) {
			
			throw e;
		}
		finally {
			
			if(fileStream != null)
				fileStream.close();
			
			
		}
		
		long stopTime = System.currentTimeMillis();
		
		System.out.println(count + " public IP(s) exported to "+fileName+ " in " +TimeUtils.formatHmss(stopTime-startTime)+ ".");
	}
	
	public static void main(String args[]) {
		
		try {
			retrievePublicIPAsFile(DeimosConfig.FILE_OUTPUT_PUBLICIP);
		} catch (UnknownHostException e) {

			e.printStackTrace();
		}
	}

}
