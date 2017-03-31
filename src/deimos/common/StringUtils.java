package deimos.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/**
 * A collection of several utility functions for working with Strings and file names.
 * 
 * @author Siddhesh Karekar
 * @author Various others (credited)
 */
public class StringUtils
{

	/**
	 * Converts a long string into a format displayable in a fixed size.
	 * Adds "..." if the length is exceeded.
	 * @param str
	 * @param length
	 */
	public static String truncate(String str, int length)
	{
		if(str.length() > length)
			return str.substring(0, length-3)+"...";
		else
			return str;
	}

	public static String truncateURL(String url)
	{
		return url.replace("https://","").replace("http://","");
	}
	public static String truncateURL(String url, int length)
	{
		return truncate(truncateURL(url), length);
	}

	/**
	 * Convert a String with spaces or
	 * underscores into camelCase.
	 * e.g. "Hello World" -> "helloWorld"
	 * @param str Input String
	 * @return Output String
	 */
	public static String toCamelCase(String str)
	{
		StringBuilder sb = new StringBuilder();

		int length = str.length();
		for(int i = 0; i < length ; i++)
		{
			char ch = str.charAt(i);
			if(ch == ' ' | ch == '_')
			{
				if(i < length-1)
				{
					char chNext = str.charAt(i+1);
					sb.append(Character.toUpperCase(chNext));
					i ++;
				}
			}
			else sb.append(Character.toLowerCase(ch));
		}

		return sb.toString();
	}



	/**
	 * Convert a String with spaces or
	 * underscores into camelCase.
	 * e.g. "Hello World" -> "HelloWorld"
	 * @param str Input String
	 * @return Output String
	 */
	public static String toTitleCase(String str)
	{
		StringBuilder sb = new StringBuilder();

		int length = str.length();
		for(int i = 0; i < length ; i++)
		{
			char ch = str.charAt(i);

			if(ch == ' ' | ch == '_')
			{

				if(i < length-1)
				{
					char chNext = str.charAt(i+1);
					sb.append(Character.toUpperCase(chNext));
					i ++;
				}
			}
			else if(i == 0) {
				sb.append(Character.toUpperCase(ch));
			}
			else sb.append(Character.toLowerCase(ch));
		}

		return sb.toString();
	}


	private static MessageDigest md;

	/** 
	 * Generates an MD5 Hash to use as a file name.
	 * This helps to determine whether one URL is visited more than once
	 * @param name The URL to hash
	 * @return A legible filename with a hash.
	 */
	public static String hashFilename(String name) {

		// Initialize the MessageDigest object that lets us produce hashes
		if(md == null) {
			try {
				md = MessageDigest.getInstance("MD5");

			} catch(NoSuchAlgorithmException e) {
				e.printStackTrace();
			} 
		}

		String filename = "";
		byte[] digest = md.digest(name.getBytes());
		filename = (new HexBinaryAdapter()).marshal(digest);

		return filename;
	}

	/**
	 * Returns the string with only its alphabetical characters.
	 * This function should be moved elsewhere later. */
	public static String onlyAlphabeticalString(String s) {
		StringBuilder sb = new StringBuilder();

		for(int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if(Character.isLetter(c))
				sb.append(c);
		}

		return sb.toString();
	}


	/** Replace every character which is not a letter, number,
	 * underscore or dot with an underscore, using regex.
	 * 
	 * Sourced from:
	 * stackoverflow.com/questions/1184176/
	 * how-can-i-safely-encode-a-string-in-java-to-use-as-a-filename
	 * @author JonasCz
	 */
	public static String sanitizeFilename(String inputName) {
		return inputName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
	}

	/**
	 * Convert a String in the format yyyy-MM-dd HH:mm:ss to a TimeStamp.
	 * 
	 * Ref: 
	 * stackoverflow.com/questions/18915075/java-convert-string-to-timestamp
	 * 
	 * @param str
	 * @return
	 * @author Harsh
	 * @throws ParseException
	 */
	public static Timestamp toTimestamp(String str) throws ParseException
	{
		DateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // YYYY-MM-DD HH24:MI:SS
		// you can change format of date
		Date date = formatter.parse(str);
		Timestamp timeStampDate = new Timestamp(date.getTime());

		return timeStampDate;
	}


	/**
	 * Returns a CSV row string containing each of the input
	 * parameter strings.
	 * Uses toCSVSafeString to escape CSV-unsafe chars.
	 * @param strings an array of Strings e.g. Appl,e, Ball, "Poop"
	 * @return "Appl_e","Ball","_Poop_"
	 */
	public static String toCSV(String ...strings)
	{
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < strings.length; i++)
		{
			String current = toCSVSafeString(strings[i]);
			sb.append("\""+current+"\"");
			if(i != strings.length-1)
			{
				sb.append(",");
			}
		}
		return sb.toString();
	}

	/**
	 * Replaces CSV-incompatible characters,
	 * such as double quotes and commas, by underscores.
	 * @param input
	 * @return
	 */
	public static String toCSVSafeString(String input)
	{
		return input.replace(",", "_").replace("\"", "_");
	}

	/**
	 * When given a CSV line as inpu, returns all the individual
	 * cells in that row in an Array
	 * @param csvLine
	 * @return String[] containing subparts
	 */
	public static String[] getCSVParts(String csvLine)
	{

		/*String parts[] = csvLine.split(",");

		for(int i = 0; i < parts.length; i++)
		{
			parts[i] = parts[i].trim();
			if(parts[i].startsWith("\""))
			{
				parts[i] = parts[i].replace("\"","");
				parts[i] = parts[i].substring(1);
					if(parts[i].endsWith("\""))
						parts[i] = parts[i].substring(0, parts[i].length()-1);
			}
		}*/

		List<String> partsList = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		boolean insideQuotes = false;

		for(int i = 0; i < csvLine.length(); i++)
		{
			char ch = csvLine.charAt(i);


			if(ch == '\"')
			{
				insideQuotes = true^insideQuotes;
			}

			if(i == csvLine.length()-1)
			{
				sb.append(ch);

				partsList.add(sb.toString());
				// System.out.println(sb.toString());

				sb.setLength(0);
			}
			else if(ch == ',' && !insideQuotes)
			{
				partsList.add(sb.toString());
				// System.out.println(sb.toString());

				sb.setLength(0);

			}
			else {
				sb.append(ch);
			}
		}

		String[] parts = new String[partsList.size()];
		for(int i = 0; i < parts.length; i++)
		{
			parts[i] = partsList.get(i).trim();
			if(parts[i].startsWith("\""))
			{
				parts[i] = parts[i].replace("\"","");
			}
		}

		return parts;
	}

	/**
	 * Reference:
	 * stackoverflow.com/questions/4581877/validating-ipv4-string-in-java
	 * 
	 * @param text
	 * @return
	 * @author rouble
	 */
	public static boolean isValidIPv4 (String ip)
	{
		try {
			if ( ip == null || ip.isEmpty() ) {
				return false;
			}

			String[] parts = ip.split( "\\." );
			if ( parts.length != 4 ) {
				return false;
			}

			for ( String s : parts ) {
				int i = Integer.parseInt( s );
				if ( (i < 0) || (i > 255) ) {
					return false;
				}
			}
			if ( ip.endsWith(".") ) {
				return false;
			}

			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
}
