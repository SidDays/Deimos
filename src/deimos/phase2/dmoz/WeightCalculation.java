package deimos.phase2.dmoz;

import java.sql.SQLException;
import deimos.phase2.DBOperations;

/**
 * Computes the weights as a product of TF and IDF
 * Only for Reference ontology for now.
 * 
 * @author Amogh Bhabal
 * @author Siddhesh Karekar
 */
public class WeightCalculation
{
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
	public static void nullAllWeights()
	{
		try
		{
			String query = String.format("UPDATE tf_weight SET weight = null");
			dbo.executeUpdate(query);
			System.out.println("Nulled all weights in table.");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * For each term in IDF table,
	 * Find all rows in TF_weight of that term,
	 * And update their weight as tf*idf.
	 */
	public static void updateWeights()
	{
		try
		{
			System.out.println("Weight calculation and updation for tf_weight started.");

			// nullAllWeights();

			// Update weights
			String queryUpdate = "UPDATE tf_weight "
					+ "SET tf_weight.weight = tf_weight.tf * ( SELECT idf FROM idf WHERE idf.term = tf_weight.term )";
			dbo.executeUpdate(queryUpdate);

			System.out.println("Weight calculation and updation finished for tf_weight!");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String args[])
	{
		WeightCalculation.updateWeights();
	}
}
