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
public class StringUtils {

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
}
