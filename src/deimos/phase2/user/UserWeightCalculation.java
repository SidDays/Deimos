package deimos.phase2.user;

import java.sql.SQLException;

import deimos.phase2.DBOperations;

/**
 * Computes the weights as a product of TF and IDF
 * only for user databases.
 * 
 * @author Amogh Bhabal
 * @author Siddhesh Karekar
 */
public class UserWeightCalculation {

	static DBOperations dbo;

	static
	{
		try
		{
			dbo = new DBOperations();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * In case the IDF table has changed, you can
	 * optionally clear the weight column before you
	 * re-compute all the weights.
	 */
	public static void nullAllWeights(int user_id)
	{
		try
		{
			String query = String.format("UPDATE tf_users SET weight = null WHERE user_id = "+user_id);
			dbo.executeUpdate(query);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * For each term in IDF table,
	 * Find all rows in tf_users of that term,
	 * And update their weight as tf*idf.
	 */
	public static void updateWeights(int user_id)
	{
		try
		{
			System.out.println("Weight calculation and updation for tf_users started.");

			// nullAllWeights();

			// Update weights
			String queryUpdate = "UPDATE tf_users SET tf_users.weight = tf_users.tf * ( SELECT idf FROM idf_users WHERE idf_users.term = tf_users.term ) WHERE user_id = "+user_id;
			dbo.executeUpdate(queryUpdate);

			System.out.println("Weight calculation and updation finished for tf_users!");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		// TODO userid dont hardcode!! lols
		updateWeights(1);

	}

}
