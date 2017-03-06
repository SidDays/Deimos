package deimos.phase2.dmoz;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
		String term, query;
		String topicName;
		int tf;
		double idf, weight;
		try
		{
			System.out.println("Weight calculation and updation started.");
			
			// nullAllWeights();
			
			// For each term in IDF table
			ResultSet rs = dbo.executeQuery("SELECT * FROM idf");
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
				
				// Find that term in tf_weight table
				ResultSet rs1 = dbo.executeQuery("SELECT * FROM tf_weight WHERE term = '"+term+"'");
				while(rs1.next())
				{
					tf = rs1.getInt("tf");
					topicName = rs1.getString("topic_name");
					
					// Compute weight
					weight = tf*idf;
					
					// Update tf_weight table
					query = String.format("UPDATE tf_weight SET weight = %f WHERE topic_name = '%s' AND term = '%s'",
							weight, topicName, term);
					
					dbo.executeUpdate(query);
					System.out.format("Computed weight %.3f for topic '%s' with TF %d.\n", weight, topicName, tf);
				}
			}
			System.out.println("\nWeight calculation and updation finished!");
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
