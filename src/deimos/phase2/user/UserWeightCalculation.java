package deimos.phase2.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import deimos.common.TimeUtils;
import deimos.phase2.DBOperations;

/**
 * Computes the weights as a product of TF and IDF
 * only for user databases.
 * 
 * @author Amogh Bhabal
 * @author Siddhesh Karekar
 */
public class UserWeightCalculation {

	private static Connection db_conn;

	/**
	 * In case the IDF table has changed, you can
	 * optionally clear the weight column before you
	 * re-compute all the weights.
	 */
	public static void nullAllWeights(int user_id)
	{
		try
		{
			Statement stmt = db_conn.createStatement();
			String query = String.format("UPDATE user_tf SET weight = NULL WHERE user_id = "+user_id);
			stmt.executeUpdate(query);
			stmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * For each term in IDF table,
	 * Find all rows in user_tf of that term,
	 * And update their weight as tf*idf.
	 */
	public static void updateWeights(int user_id)
	{
		try
		{
			long startTime = System.currentTimeMillis();
			
			System.out.println("Weight calculation and updation for user_tf started for user "+user_id+".");
			
			db_conn = DBOperations.getConnectionToDatabase("UserWeightCalculation");

			// nullAllWeights();

			// Update weights
			Statement stmt = db_conn.createStatement();
			String queryUpdate = "UPDATE user_tf "
					+ "SET user_tf.weight = user_tf.tf * "
					+ "( SELECT idf FROM user_idf WHERE user_idf.term = user_tf.term AND user_id = "+user_id+" ) "
							+ "WHERE user_id = "+user_id;
			stmt.executeUpdate(queryUpdate);
			stmt.close();
			
			db_conn.close();
			
			long stopTime = System.currentTimeMillis();
			System.out.format("Weight calculation and updation finished for user_tf for user %d in %s.\n",
					user_id, TimeUtils.formatHmss(stopTime-startTime));
			System.out.println();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/** Testing */
	public static void main(String[] args)
	{
		// dont hardcode!! lols
		updateWeights(1);

	}

}
