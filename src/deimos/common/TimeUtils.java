package deimos.common;

import java.util.concurrent.TimeUnit;

public class TimeUtils {
	
	/**
	 * Reference:
	 * stackoverflow.com/questions/6710094/how-to-format-an-elapsed-time-interval-in-hhmmss-sss-format-in-java
	 * 
	 * @param longTime
	 * @return
	 * @author Jarrod Roberson
	 */
	public static String formatHmss(final long longTime)
    {
        final long hr = TimeUnit.MILLISECONDS.toHours(longTime);
        final long min = TimeUnit.MILLISECONDS.toMinutes(longTime - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(longTime - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(longTime - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
        return String.format("%02dh %02dm %02ds %03dms", hr, min, sec, ms);
    }
	
	/** Test */
	/*public static void main(String args[])
	{
		System.out.println(formatHmss(111001200));
	}*/
}
