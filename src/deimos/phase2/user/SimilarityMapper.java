package deimos.phase2.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import deimos.phase2.DBOperations;

public class SimilarityMapper
{
	private static List<String> referenceTerms = new ArrayList<>();
	private static List<Double> referenceWeights = new ArrayList<>();

	private static List<String> userTerms = new ArrayList<>();
	private static List<Double> userWeights = new ArrayList<>();

	private static final double THRESHOLD = Float.MIN_VALUE;

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
			ResultSet rs1 = dbo.executeQuery("SELECT term, weight FROM tf_weight "
					+ "WHERE topic_name LIKE '" + topicName+"' AND weight != 0");

			referenceTerms.clear();
			referenceWeights.clear();
			while(rs1.next())
			{
				String currentTerm = rs1.getString("term");
				double currentWeight = rs1.getDouble("weight");
				referenceTerms.add(currentTerm);
				referenceWeights.add(currentWeight);
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
			ResultSet rs2 = dbo.executeQuery("SELECT term, weight FROM tf_users "
					+ "WHERE url LIKE '" + url +"'  AND weight != 0");

			userTerms.clear();
			userWeights.clear();
			while(rs2.next())
			{
				String currentTerm = rs2.getString("term");
				double currentWeight = rs2.getDouble("weight");
				userTerms.add(currentTerm);
				userWeights.add(currentWeight);
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
			ResultSet rsTest = dbo.executeQuery(
					"select count(*) "
					+ "FROM topics CROSS JOIN users "
					+ "WHERE user_id = 1");
			rsTest.next();
			System.out.println("Total number of cross-joined rows: "+rsTest.getInt(1));
			rsTest.close();

			// Reference ontology
			ResultSet rsXJoin = dbo.executeQueryAgain(
					"select topics.topic_name, "
					+ "users.url "
					+ "FROM topics CROSS JOIN users "
					+ "WHERE user_id = 1");
			while(rsXJoin.next())
			{
				String currentTopic = rsXJoin.getString(1);
				String currentURL = rsXJoin.getString(2);
				populateReferenceList(currentTopic);
				
				System.out.println();
				System.out.println("Populated currentTopic: "+currentTopic);
				populateUserList(currentURL);
				System.out.println("Populated currentURL: "+currentURL);

				List<String> commonTerms = new ArrayList<>(referenceTerms);
				commonTerms.retainAll(userTerms);
				List<Double> commonWeightsReference = new ArrayList<>();
				List<Double> commonWeightsUser = new ArrayList<>();

				for(String commonTerm : commonTerms)
				{
					int userIndex = userTerms.indexOf(commonTerm);
					double commonWeight = userWeights.get(userIndex);
					commonWeightsReference.add(commonWeight);
					commonWeightsUser.add(commonWeight);

					// Remove from user
					userWeights.remove(userIndex);
					userTerms.remove(userIndex);

					// Remove this term from reference
					int referenceIndex = referenceTerms.indexOf(commonTerm);
					referenceWeights.remove(referenceIndex);
					referenceTerms.remove(referenceIndex);
				}

				// Handle remaining terms in user
				for(int i = 0; i < userTerms.size(); i++)
				{
					commonTerms.add(userTerms.get(i));
					commonWeightsUser.add(userWeights.get(i));
					commonWeightsReference.add(0.0);
				}

				// Handle remaining terms in reference
				for(int i = 0; i < referenceTerms.size(); i++)
				{
					commonTerms.add(referenceTerms.get(i));
					commonWeightsUser.add(0.0);
					commonWeightsReference.add(referenceWeights.get(i));
				}

				// print
				System.out.println("\nReady for mapping!");
				for(int i = 0; i < commonTerms.size(); i++)
				{
					System.out.format("%5d %s: UW: %.1e, RW: %.1e %s\n",
							i,
							commonTerms.get(i),
							commonWeightsUser.get(i),
							commonWeightsReference.get(i),
							(commonWeightsUser.get(i) == 0 && commonWeightsReference.get(i) == 0)?"(Both are zero)":"");
				}

				// Do the mapping!

				// Calculate Dot Product
				double dotProduct = 0;
				double denReference = 0, denUsers = 0;
				for(int i = 0; i < commonTerms.size(); i++)
				{
					dotProduct += commonWeightsReference.get(i) * commonWeightsUser.get(i);
					denReference += Math.pow(commonWeightsReference.get(i), 2);
					denUsers += Math.pow(commonWeightsUser.get(i), 2);
				}

				// Root denominators
				denReference = Math.sqrt(denReference);
				denUsers = Math.sqrt(denUsers);

				// compute similarity!! omg!!
				double similarity = dotProduct/(denReference * denUsers);
				System.out.print("Similarity = "+similarity);
				if(similarity < THRESHOLD)
				{
					System.out.println(" (less than threshold!");
				}
				else
				{
					System.out.println();
					
					// Insert into Database
					String query = String.format("INSERT INTO Similarity (url, topic_name, similarity) VALUES ('%s', '%s', %f)",
							currentURL,
							currentTopic,
							similarity);
					dbo.executeQuery(query);
					System.out.println("Inserted into database! YAY!");
				}

			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
}
