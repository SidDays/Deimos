package deimos.phase2.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import deimos.phase2.DBOperations;

/**
 * 
 * @author Siddhesh Karekar
 *
 */
public class UserTrainingInput {

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

	/** Testing only */
	public static void main(String[] args)
	{
		// Hardcode4Lyf lmao
		calculateTrainingInputs(2, true);
	}

}
