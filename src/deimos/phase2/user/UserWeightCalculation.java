package deimos.phase2.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import deimos.phase2.DBOperations;

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
		String term, query;
		String topicName;
		int tf;
		double idf, weight;
		try
		{
			System.out.println("Weight calculation and updation for user "+user_id+" started.");

			// For each term in IDF table
			ResultSet rs = dbo.executeQuery("SELECT * FROM idf_users WHERE user_id = "+user_id);
			List<String> terms = new ArrayList<>();
			List<Double> terms_idf = new ArrayList<>();
			while(rs.next())
			{
				term = rs.getString("term");
				terms.add(term);

				idf = rs.getDouble("idf");
				terms_idf.add(idf);
			}
			System.out.println(terms.size()+" terms in IDF table.");

			for(int i = 0; i < terms.size(); i++)
			{
				term = terms.get(i);
				idf = terms_idf.get(i);

				System.out.format("\nComputing weights for term '%s' with IDF %.3f.\n", term, idf);

				// Find that term in tf_users table
				ResultSet rs1 = dbo.executeQuery("SELECT * FROM tf_users WHERE term = '" + term + "' AND user_id = "+user_id);
				while(rs1.next())
				{
					tf = rs1.getInt("tf");
					topicName = rs1.getString("url");

					// Compute weight
					weight = tf*idf;

					// Update tf_users table
					query = String.format("UPDATE tf_users SET weight = %f WHERE url = '%s' AND term = '%s' AND user_id = %d",
							weight, topicName, term, user_id);

					dbo.executeUpdate(query);
					System.out.format("Computed weight %.3f for topic '%s' with TF %d.\n", weight, topicName, tf);
				}
			}
			System.out.println("\nWeight calculation and updation for user "+user_id+" finished!");
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
