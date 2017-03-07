package deimos.phase2.ref;

import java.sql.SQLException;
import deimos.phase2.DBOperations;

/**
 * Computes the weights as a product of TF and IDF
 * only for Reference ontology.
 * 
 * @author Amogh Bhabal
 * @author Siddhesh Karekar
 */
public class RefWeightCalculation
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
	 * In case the ref_idf table has changed, you can
	 * optionally clear the weight column before you
	 * re-compute all the weights.
	 */
	public static void nullAllWeights()
	{
		try
		{
			String query = String.format("UPDATE ref_tf SET weight = null");
			dbo.executeUpdate(query);
			System.out.println("Nulled all weights in table.");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * For each term in ref_idf table,
	 * Find all rows in ref_tf of that term,
	 * And update their weight as tf*idf.
	 */
	public static void updateWeights()
	{
		try
		{
			System.out.println("Weight calculation and updation for ref_tf started.");

			// nullAllWeights();

			// Update weights
			String queryUpdate = "UPDATE ref_tf "
					+ "SET ref_tf.weight = ref_tf.tf * ( SELECT idf FROM ref_idf WHERE ref_idf.term = ref_tf.term )";
			dbo.executeUpdate(queryUpdate);

			System.out.println("Weight calculation and updation finished for ref_tf!");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String args[])
	{
		RefWeightCalculation.updateWeights();
	}
}
