package deimos.phase2.user;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Types;
import java.util.List;

import deimos.common.DeimosConfig;
import deimos.common.ProcessFileUtils;
import deimos.common.StringUtils;
import deimos.phase2.DBOperations;

/**
 * Scans export text files for user Info and Public IP
 * and puts the resulting data into the database.
 * 
 * @author Siddhesh Karekar
 *
 */
public class UserInfo
{

	/** Create Statements and preparedStatements on this connection. */
	private static Connection db_conn;

	/** Le insert query. */
	private static PreparedStatement pstmt;

	private static String firstName, lastName, publicIP, location;
	private static int yearOfBirth;
	private static char gender;
	
	/**
	 * Reads the file (e.g. export-userInfo.txt)
	 * into local variables
	 * firstName, lastName, gender, yearOfBirth and location.
	 * 
	 * @param userInfoFilePath
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void parseUserInfo(String userInfoFilePath)
			throws FileNotFoundException, IOException
	{
		List<String> lines = ProcessFileUtils.readFileIntoList(userInfoFilePath);
		String[] parts = lines.get(0).split(DeimosConfig.DELIM);

		// first name
		firstName = parts[0];
		if(firstName.equalsIgnoreCase("null"))
			firstName = null;

		// last name
		lastName = parts[1];
		if(lastName.equalsIgnoreCase("null"))
			lastName = null;

		// Gender
		String genderString = parts[2];
		if(genderString.equalsIgnoreCase("female") |
				genderString.equalsIgnoreCase("girl") |
				genderString.equalsIgnoreCase("woman"))
		{
			gender = 'f';
		}
		else { // Default to male
			gender = 'm';
		}

		// year of Birth
		String yearOfBirthString = parts[3];
		try {
			yearOfBirth = Integer.parseInt(yearOfBirthString);
		}
		catch(NumberFormatException nfe) {
			System.err.println(nfe + "");
			yearOfBirth = -1;
		}

		// Location
		location = parts[4];
		if(location.equalsIgnoreCase("null"))
			location = null;


	}

	/**
	 * Reads the file (e.g. export-publicIP.txt)
	 * into local variable publicIP.
	 * 
	 * @param publicIPFilePath
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void parsePublicIP(String publicIPFilePath)
			throws FileNotFoundException, IOException
	{
		List<String> lines = ProcessFileUtils.readFileIntoList(publicIPFilePath);
		String ipString = lines.get(0);

		if(StringUtils.isValidIPv4(ipString)) {
			publicIP = ipString;
		}
		else {
			System.err.println("IP from file is not valid IPv4. Setting it in publicIP anyway.");
			publicIP = ipString;
		}
	}

	public static void insertUserInfoIntoDB(int user_id,
			String userInfoFilePath, String publicIPFilePath, boolean truncate)
	{
		try
		{
			// Open connection
			db_conn = DBOperations.getConnectionToDatabase("UserInfo");
			
			if(truncate)
				DBOperations.truncateUserTable(db_conn, "user_info", user_id);

			// Insert it into the database
			pstmt = db_conn.prepareStatement("INSERT INTO user_info "
					+ "(user_id, first_name, last_name, gender, birth_year, location, ip) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?)");

			try
			{
				parseUserInfo(userInfoFilePath);
				pstmt.setInt(1, user_id);

				// First name
				if(firstName != null)
					pstmt.setString(2, firstName);
				else
					pstmt.setNull(2, Types.VARCHAR);

				// last Name
				if(lastName != null)
					pstmt.setString(3, lastName);
				else
					pstmt.setNull(3, Types.VARCHAR);

				// Gender
				pstmt.setString(4, String.valueOf(gender));

				// yoB
				if(yearOfBirth != -1)
					pstmt.setInt(5, yearOfBirth);
				else
					pstmt.setNull(5, Types.INTEGER);

				// location
				if(location != null)
					pstmt.setString(6, location);
				else
					pstmt.setNull(6, Types.VARCHAR);


				parsePublicIP(publicIPFilePath);
				pstmt.setString(7, publicIP);

				// Run the Query
				try {
					pstmt.executeUpdate();

					System.out.format("\nUser info (%d, %s, %s, %s, %d, %s, %s) inserted into database!\n",
							user_id, firstName, lastName, gender, yearOfBirth, location, publicIP);
				}
				catch(SQLIntegrityConstraintViolationException sicve)
				{
					System.out.println("User already exists "+ sicve);
				}

			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			pstmt.close();
			db_conn.close();


		}

		catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		// Hardcoded for now
		insertUserInfoIntoDB(1, DeimosConfig.FILE_OUTPUT_USERINFO, DeimosConfig.FILE_OUTPUT_PUBLICIP, false);
	}

}
