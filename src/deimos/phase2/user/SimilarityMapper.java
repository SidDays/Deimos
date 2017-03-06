package deimos.phase2.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import deimos.phase2.DBOperations;

public class SimilarityMapper
{
	private static List<String> referenceTerms = new ArrayList<>();
	
	private static List<String> userTerms = new ArrayList<>();
	
	private static List<Double> referenceTermsWeights = new ArrayList<>();
	
	private static List<Double> userTermsWeights = new ArrayList<>();
	
	private static final double THRESHOLD = Float.MIN_VALUE;
	
	// private static ResultSet rs, rs1;
	private static DBOperations dbo;
	
	static
	{
		try
		{
			dbo = new DBOperations();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		computeSimilarity(1);
	}
	
	private static void populateReferenceList(String topicName)
	{
		try
		{
			ResultSet rs1 = dbo.executeQuery("SELECT term, weight FROM tf_weight WHERE topic_name LIKE '" + topicName+"'");
			while(rs1.next())
			{
				String currentTerm = rs1.getString("term");
				double currentWeight = rs1.getDouble("weight");
				referenceTerms.add(currentTerm);
				referenceTermsWeights.add(currentWeight);
			}

		} 
		catch (SQLException e) {

			e.printStackTrace();
		}
	}
	
	private static void populateUserList(String url)
	{
		try
		{
			ResultSet rs2 = dbo.executeQuery("SELECT term, weight FROM tf_users WHERE url LIKE '" + url +"'");
			while(rs2.next())
			{
				String currentTerm = rs2.getString("term");
				double currentWeight = rs2.getDouble("weight");
				userTerms.add(currentTerm);
				userTermsWeights.add(currentWeight);
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	
	private static void computeSimilarity(int user_id)
	{
		try
		{
			// Reference ontology
			ResultSet rs_topics = dbo.executeQuery("SELECT topic_name FROM topics");
			while(rs_topics.next())
			{
				String currentTopic = rs_topics.getString("topic_name");
				populateReferenceList(currentTopic);
			}
			
			// User history
			ResultSet rs_urls = dbo.executeQuery("SELECT url FROM users WHERE user_id = "+user_id);
			while(rs_urls.next())
			{
				String currentURL = rs_urls.getString("url");
				populateUserList(currentURL);
				
			}
			
			List<String> commonTerms = new ArrayList<>(referenceTerms);
			commonTerms.retainAll(userTerms);
			List<Double> commonWeightsReference = new ArrayList<>();
			List<Double> commonWeightsUser = new ArrayList<>();
			
			for(String commonTerm : commonTerms)
			{
				int userIndex = userTerms.indexOf(commonTerm);
				double commonWeight = userTermsWeights.get(userIndex);
				commonWeightsReference.add(commonWeight);
				commonWeightsUser.add(commonWeight);
				
				// Remove from user
				userTermsWeights.remove(userIndex);
				userTerms.remove(userIndex);
				
				// Remove this term from reference
				int referenceIndex = referenceTerms.indexOf(commonTerm);
				referenceTermsWeights.remove(referenceIndex);
				referenceTerms.remove(referenceIndex);
			}
			
			// Handle remaining terms in user
			for(int i = 0; i < userTerms.size(); i++)
			{
				commonTerms.add(userTerms.get(i));
				commonWeightsUser.add(userTermsWeights.get(i));
				commonWeightsReference.add(0.0);
			}

			// Handle remaining terms in reference
			for(int i = 0; i < referenceTerms.size(); i++)
			{
				commonTerms.add(referenceTerms.get(i));
				commonWeightsUser.add(0.0);
				commonWeightsReference.add(referenceTermsWeights.get(i));
			}
			
			// print
			System.out.println("\nReady for mapping!");
			for(int i = 0; i < commonTerms.size(); i++)
			{
				System.out.format("%5d %s: UW: %.3f, RW: %.3f\n",
						i,
						commonTerms.get(i),
						commonWeightsUser.get(i),
						commonWeightsReference.get(i));
			}
			
			// Do the mapping!
			// TODO INCOMPLETEs
			

		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
}
