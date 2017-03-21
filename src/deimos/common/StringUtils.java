package deimos.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
	 * Returns a CSV row string containing each of the input
	 * parameter strings. Escapes double quotes with underscores.
	 * @param strings an array of Strings e.g. Apple, Ball, "Poop"
	 * @return "Apple", "Ball", "_Poop_"
	 */
	public static String toCSV(String ...strings)
	{
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < strings.length; i++)
		{
			String current = strings[i].replace("\"", "");
			sb.append("\""+current+"\"");
			if(i != strings.length-1)
			{
				sb.append(",");
			}
		}
		return sb.toString();
	}
}
