package deimos.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Sourced from:
 * stackoverflow.com/questions/81902/
 * how-to-find-and-kill-running-win-processes-from-within-java
 * 
 * @author Kara Rawson
 */

public class ProcessUtils {

	private static final String TASKLIST = "tasklist";
	private static final String KILL = "taskkill /F /IM ";

	public static boolean isProcessRunning(String serviceName) throws IOException
	{
		Process p = Runtime.getRuntime().exec(TASKLIST);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null)
		{
			// System.out.println(line);
			if (line.contains(serviceName))
			{
				return true;
			}
		}

		return false;

	}

	public static void killProcess(String serviceName) throws IOException {

		Runtime.getRuntime().exec(KILL + serviceName);

	}

}
