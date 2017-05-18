package deimos.phase2.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import deimos.common.DeimosConfig;
import deimos.common.StringUtils;
import deimos.phase2.DBOperations;
import deimos.phase3.NeuralConstants;
import deimos.phase3.User;

/**
 * 
 * @author Siddhesh Karekar
 *
 */
public class UserTrainingInput
{

	public static final List<String> TOPICS_TOP_LEVEL = Arrays.asList(new String[] {
			"Top/Shopping/Antiques_and_Collectibles",
			"Top/Shopping/Auctions",
			"Top/Shopping/By_Region",
			"Top/Shopping/Children",
			"Top/Shopping/Classifieds",
			"Top/Shopping/Clothing",
			"Top/Shopping/Consumer_Electronics",
			"Top/Shopping/Crafts",
			"Top/Shopping/Death_Care",
			"Top/Shopping/Directories",
			"Top/Shopping/Entertainment",
			"Top/Shopping/Ethnic_and_Regional",
			"Top/Shopping/Flowers",
			"Top/Shopping/Food",
			"Top/Shopping/General_Merchandise",
			"Top/Shopping/Gifts",
			"Top/Shopping/Health",
			"Top/Shopping/Holidays",
			"Top/Shopping/Home_and_Garden",
			"Top/Shopping/Jewelry",
			"Top/Shopping/Music",
			"Top/Shopping/Niche",
			"Top/Shopping/Office_Products",
			"Top/Shopping/Pets",
			"Top/Shopping/Photography",
			"Top/Shopping/Publications",
			"Top/Shopping/Recreation",
			"Top/Shopping/Sports",
			"Top/Shopping/Tobacco",
			"Top/Shopping/Tools",
			"Top/Shopping/Toys_and_Games",
			"Top/Shopping/Travel",
			"Top/Shopping/Vehicles",
			"Top/Shopping/Visual_Arts",
			"Top/Shopping/Weddings"
	});

	/** Create Statements and preparedStatements on this connection. */
	private static Connection db_conn;

	private static PreparedStatement pstmtCalc;
	private static PreparedStatement pstmtInsert;

	public static void calculateTrainingInputs(int user_id, boolean truncate)
	{
		try {
			db_conn = DBOperations.getConnectionToDatabase("UserTrainingInput");

			if(truncate)
				DBOperations.truncateUserTable(db_conn, "user_training_input", user_id);

			pstmtCalc = db_conn.prepareStatement("SELECT topic_name, \"value\"/(SELECT SUM(\"value\") FROM (SELECT topic_name, ((SUM(similarity)/COUNT(similarity))/ (SELECT COUNT(DISTINCT topic_name) FROM user_ref_similarity WHERE user_id = ? AND ((LENGTH(topic_name) - LENGTH(REPLACE(topic_name, '/', ''))) = 2) )) \"value\" FROM user_ref_similarity WHERE user_id = ? AND ((LENGTH(topic_name) - LENGTH(REPLACE(topic_name, '/', ''))) = 2) GROUP BY topic_name)) \"sum\" FROM (SELECT topic_name, ((SUM(similarity)/COUNT(similarity))/ (SELECT COUNT(DISTINCT topic_name) FROM user_ref_similarity WHERE user_id = ? AND ((LENGTH(topic_name) - LENGTH(REPLACE(topic_name, '/', ''))) = 2) )) \"value\" FROM user_ref_similarity WHERE user_id = ? AND ((LENGTH(topic_name) - LENGTH(REPLACE(topic_name, '/', ''))) = 2) GROUP BY topic_name) ");
			pstmtCalc.setInt(1, user_id);
			pstmtCalc.setInt(2, user_id);
			pstmtCalc.setInt(3, user_id);
			pstmtCalc.setInt(4, user_id);

			pstmtInsert = db_conn.prepareStatement("INSERT INTO user_training_input (user_id, topic_name, value) "
					+ "VALUES(?, ?, ?)");
			pstmtInsert.setInt(1, user_id);

			ResultSet rs = pstmtCalc.executeQuery();

			List<Float> topicValues = new ArrayList<>();
			for(int i = 0; i < TOPICS_TOP_LEVEL.size(); i++)
				topicValues.add(0f);

			int rows = 0;
			try {
				while (rs.next())
				{
					String topicName = rs.getString(1);

					int index = TOPICS_TOP_LEVEL.indexOf(topicName);
					float sum = rs.getFloat(2);
					topicValues.set(index, sum);
				}

				System.out.println("Finished calculating training input for user "+user_id+".\n");

				for(int i = 0; i < TOPICS_TOP_LEVEL.size(); i++)
				{
					String topicName = TOPICS_TOP_LEVEL.get(i);
					pstmtInsert.setString(2, topicName); // 1-> user_id
					float sum = topicValues.get(i);
					pstmtInsert.setFloat(3, sum);

					pstmtInsert.executeUpdate();
					System.out.format("%3d | %s - %f\n", rows++, topicName, sum);
				}

				System.out.println("Training input stored into DB for user "+user_id+".");
			}
			catch(SQLIntegrityConstraintViolationException sicve) {
				System.out.println(sicve);
			}

			pstmtInsert.close();
			rs.close();
			pstmtCalc.close();
			db_conn.close();
		}

		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a nametag to help while making filenames.
	 * @return
	 */
	public static String getNameTag(int yearOfBirth, String genderStr, String fname, String lname)
	{
		String demoGroup = "UNKNOWN", fullName = "UNNAMED";

		// Get string containing demographic group
		int age = NeuralConstants.getAge(yearOfBirth);
		try {
			demoGroup = NeuralConstants.GROUPS_SMALL_NAMES[NeuralConstants.getGroup(
					NeuralConstants.getAgeGroup(age),
					NeuralConstants.getGenderGroup(genderStr))];
		}
		catch(ArrayIndexOutOfBoundsException e) {
			demoGroup = "UNKNOWN";
		}

		if(fname.equals("null"))
			fname = "";
		if(lname.equals("null"))
			lname = "";

		fullName = User.getName(fname, lname);

		return demoGroup + "-" + fullName;
	}

	/**
	 * Exports the training input values to a file for easy importing into someplace else.
	 * @param db_conn
	 */
	public static void exportTrainingValues(Connection db_conn, int user_id, String ... names)
			throws SQLException, FileNotFoundException
	{
		String filenameTrainVal = DeimosConfig.FILE_OUTPUT_TRAINVAL;
		String filenameUserInfo = DeimosConfig.FILE_OUTPUT_USERINFO;
		String filenamePublicIP = DeimosConfig.FILE_OUTPUT_PUBLICIP;

		/*String name;
		if(names.length == 0) {
			name = null;
		}
		else {
			name = names[0];

			filenameTrainVal = DeimosConfig.FILE_OUTPUT_TRAINVAL.substring(0,
					DeimosConfig.FILE_OUTPUT_TRAINVAL.length()-4) + "-" + name +".txt";
			filenameUserInfo = DeimosConfig.FILE_OUTPUT_USERINFO.substring(0,
					DeimosConfig.FILE_OUTPUT_USERINFO.length()-4) + "-" + name +".txt";
			filenamePublicIP = DeimosConfig.FILE_OUTPUT_PUBLICIP.substring(0,
					DeimosConfig.FILE_OUTPUT_PUBLICIP.length()-4) + "-" + name +".txt";
		}*/

		exportTrainingValues(db_conn, user_id,
				filenameTrainVal, filenameUserInfo, filenamePublicIP);
	}

	/**
	 * Exports the training input values to a file for easy importing into someplace else.
	 * @param db_conn
	 */
	public static void exportTrainingValues(Connection db_conn, int user_id,
			String fileNameValues, String fileNameUserInfo, String fileNamePublicIP)
					throws SQLException, FileNotFoundException
	{

		// TODO
		int countVal = 0, countUser = 0, countIP = 0;			
		List<String> output = new ArrayList<>();

		Statement stmt = db_conn.createStatement();

		// Export training Values
		ResultSet rs = stmt.executeQuery("SELECT * FROM user_training_input WHERE user_id = "+user_id);

		String headerLine = StringUtils.toCSV(
				"USER_ID", "TOPIC_NAME", "VALUE");
		output.add(headerLine);

		while(rs.next())
		{
			String currentLine = StringUtils.toCSV(
					rs.getString(1), rs.getString(2), rs.getString(3));
			output.add(currentLine);
		}
		rs.close();

		// Export user Info and Public IP
		rs = stmt.executeQuery("SELECT first_name, last_name, gender, birth_year, location, ip "
				+ "FROM user_info "
				+ "WHERE user_id = "+user_id);
		List<String> userInfoFields = new ArrayList<>();

		String publicIP = "null";
		String fullName = "UNNAMED";
		String demoGroup = "UNKNOWN";
		if(rs.next()) {

			// Export user Info
			String fname = rs.getString(1);
			String lname = rs.getString(2);

			// Get string containing name
			fullName = User.getName(fname, lname);

			if(fname == null || fname.isEmpty())
				fname = "null";
			userInfoFields.add(fname);


			if(lname == null || lname.isEmpty())
				lname = "null";
			userInfoFields.add(lname);

			String genderStr = rs.getString(3);
			if(genderStr.equalsIgnoreCase("m"))
				userInfoFields.add("male");
			else if(genderStr.equalsIgnoreCase("f"))
				userInfoFields.add("female");
			else
				userInfoFields.add("null");

			String yearOfBirthStr = rs.getString(4);
			int yearOfBirth = Integer.parseInt(yearOfBirthStr);
			int age = NeuralConstants.getAge(yearOfBirth);
			/*System.out.println(age + ", ageGroup = " + NeuralConstants.getAgeGroup(age) + ", genderGroup = " +
					NeuralConstants.getGenderGroup(genderStr) + ", group = " + NeuralConstants.getGroup(
							NeuralConstants.getAgeGroup(age),
							NeuralConstants.getGenderGroup(genderStr)));*/
			userInfoFields.add(rs.getString(4));

			// Get string containing demographic group
			try {
				demoGroup = NeuralConstants.GROUPS_SMALL_NAMES[NeuralConstants.getGroup(
						NeuralConstants.getAgeGroup(age),
						NeuralConstants.getGenderGroup(genderStr))];
			}
			catch(ArrayIndexOutOfBoundsException e) {
				demoGroup = "UNKNOWN";
			}

			String location = rs.getString(5);
			if(location == null || location.isEmpty())
				location = "null";
			userInfoFields.add(location);

			// Export Public IP
			publicIP = rs.getString(6);
			if(publicIP == null || publicIP.isEmpty())
				publicIP = "null";
			userInfoFields.add(publicIP);
		}
		rs.close();

		// Modify filenames
		String fileNameValuesWoEx = StringUtils.removeExtension(fileNameValues);
		fileNameValues = String.format("%s-%s-%s.csv", fileNameValuesWoEx, demoGroup, fullName);

		String fileNameUserInfoWoEx = StringUtils.removeExtension(fileNameUserInfo);
		fileNameUserInfo = String.format("%s-%s-%s.txt", fileNameUserInfoWoEx, demoGroup, fullName);

		String fileNamePublicIPWoEx = StringUtils.removeExtension(fileNamePublicIP);
		fileNamePublicIP = String.format("%s-%s-%s.txt", fileNamePublicIPWoEx, demoGroup, fullName);

		// Write training values output to file
		PrintStream fileStreamTrain = new PrintStream(new File(fileNameValues));
		for (int i = 0; i < output.size(); i++)
		{
			// System.out.println(output.get(i));
			fileStreamTrain.println(output.get(i));
		}
		countVal++;

		// Write user info output to file
		PrintStream fileStreamUserInfo = new PrintStream(new File(fileNameUserInfo));
		for(int i = 0; i < userInfoFields.size(); i++)
		{
			fileStreamUserInfo.print(userInfoFields.get(i));
			if(i < userInfoFields.size()-1)
				fileStreamUserInfo.print(DeimosConfig.DELIM);
		}
		fileStreamUserInfo.println();
		countUser++;

		// Write publicIP input to file
		PrintStream fileStreamPublicIP = new PrintStream(new File(fileNamePublicIP));
		fileStreamPublicIP.println(publicIP);
		countIP++;


		stmt.close();

		System.out.println("Files exported for user "+user_id+":");
		System.out.println(countVal + " user data training record(s) exported to "+fileNameValues+ ".");
		System.out.println(countUser + " user data record(s) exported to "+fileNameUserInfo+ ".");
		System.out.println(countIP + " public IP(s) exported to "+fileNamePublicIP+ ".");
	}

	/** Testing only */
	public static void main(String[] args)
	{
		// Hardcode4Lyf lmao
		// calculateTrainingInputs(2, true);

		try
		{
			db_conn = DBOperations.getConnectionToDatabase("UserTrainingInput");
			exportTrainingValues(db_conn, 2);
			db_conn.close();

		}
		catch (Exception e) {
			e.printStackTrace();
		} 

	}

}
