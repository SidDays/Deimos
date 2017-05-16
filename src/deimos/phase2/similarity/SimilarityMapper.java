package deimos.phase2.similarity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import deimos.common.StringUtils;
import deimos.common.TimeUtils;
import deimos.phase2.DBOperations;

public class SimilarityMapper
{
	private static List<String> referenceTerms = new ArrayList<>();
	private static List<Double> referenceWeights = new ArrayList<>();

	private static List<String> userTerms = new ArrayList<>();
	private static List<Double> userWeights = new ArrayList<>();
	
	/** Computed similarity values less than this value will not be stored in the database. */
	private static final double THRESHOLD = Float.MIN_VALUE;
	
	/** The larger the value of the base, the
	 * smaller the effect of visit count on a URL's similarity. */
	private static final double BASE = Math.E;
	
	/** Create Statements and preparedStatements on this connection. */
	private static Connection db_conn;
	
	private static int noOfRows = 1;
	private static int currentRowNumber = 0;
	private static String status;
	
	/** Statement object, for single-time queries */
	private static Statement stmt;
	
	private static PreparedStatement pstmtPopRefList;
	
	private static PreparedStatement pstmtPopUserList;
	
	private static PreparedStatement pstmtVisitCount;
	
	private static PreparedStatement pstmtSimilarity;
	
	/** Testing purposes only. */
	public static void main(String[] args)
	{
		computeSimilarity(1);
	}
	
	public static double getProgress()
	{
		return currentRowNumber*1.0/noOfRows;
	}
	
	public static String getStatus() {
		return status;
	}
	/**
	 * Computes the similarity value for the combinations of
	 * all reference topics and web pages visited by a user.
	 * (Truncates any previous similarity calculation.)
	 * @param user_id Which user that is
	 */
	public static void computeSimilarity(int user_id)
	{		
		try
		{
			long startTime = System.currentTimeMillis();
			
			db_conn = DBOperations.getConnectionToDatabase("SimilarityMapper");
			
			DBOperations.truncateUserTable(db_conn, "user_ref_similarity", user_id);
			
			// Statement object, for single-time queries */
			stmt = db_conn.createStatement();
			
			// To get the total number of cross-joined rows
			ResultSet rsTest = stmt.executeQuery(
					"SELECT COUNT(*) "
							+ "FROM (SELECT DISTINCT topic_name FROM ref_topics) CROSS JOIN user_urls "
							+ "WHERE user_id = "+user_id);
			if(rsTest.next())
			{
				System.out.println("Total number of cross-joined rows: "+rsTest.getInt(1));
				noOfRows = rsTest.getInt(1);
			}
			
			rsTest.close();

			// Cross Join all topics in Reference ontology with All URLs visited by that user
			ResultSet rsXJoin = stmt.executeQuery(
					"SELECT DISTINCT ref_topics.topic_name, "
							+ "user_urls.url "
							+ "FROM ref_topics CROSS JOIN user_urls "
							+ "WHERE user_id = "+user_id);
			
			 // Helps for progress indication
			System.out.println();
			
			// Prepare statements that will be required ahead
			pstmtPopRefList = db_conn.prepareStatement("SELECT term, weight FROM ref_tf "
					+ "WHERE topic_name LIKE ? AND weight != 0");
			
			pstmtPopUserList = db_conn.prepareStatement("SELECT term, weight FROM user_tf "
					+ "WHERE url LIKE ?  AND weight != 0");
			
			pstmtVisitCount = db_conn.prepareStatement("SELECT visit_count FROM user_urls WHERE url = ? AND user_id = ?");
			
			pstmtSimilarity = db_conn.prepareStatement("INSERT INTO user_ref_similarity (url, topic_name, similarity, user_id) "
					+ "VALUES (?, ?, ?, ?)");
			
			while(rsXJoin.next())
			{
				String currentTopic = rsXJoin.getString(1);
				String currentURL = rsXJoin.getString(2);
				
				System.out.format("%6d", currentRowNumber);
				status = String.format("(%d/%d)", currentRowNumber, noOfRows);
				populateReferenceList(currentTopic);
				System.out.format(" | %40s",StringUtils.truncate(currentTopic.substring(4), 40)); // Removes the "Top/"
				
				populateUserList(currentURL);
				System.out.format(" | %45s",StringUtils.truncateURL(currentURL, 45));

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
				// System.out.print(" | Ready for mapping!");
				/*for(int i = 0; i < commonTerms.size(); i++)
				{
					System.out.format("%5d %s: UW: %.1e, RW: %.1e %s\n",
							i,
							commonTerms.get(i),
							commonWeightsUser.get(i),
							commonWeightsReference.get(i),
							(commonWeightsUser.get(i) == 0 && commonWeightsReference.get(i) == 0)?"(Both are zero)":"");
				}*/

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

				if(!(denUsers == 0 || denReference ==0) )
				{

					// compute similarity!! omg!!
					double similarity = dotProduct/(denReference * denUsers);
					
					// find visit count vagaira
					pstmtVisitCount.setString(1, currentURL);
					pstmtVisitCount.setInt(2, user_id);
					
					// String queryVisitCount = "SELECT visit_count FROM user_urls WHERE url = '"+currentURL+"' AND user_id = "+user_id;
					ResultSet rsVisit = pstmtVisitCount.executeQuery(); 
					int visitCount = 0;
					if(rsVisit.next())
						visitCount = rsVisit.getInt("visit_count");
					rsVisit.close();
					
					similarity = similarity * transformVisitCount(visitCount);
					
					System.out.format(" | %3d visits | Sim. %.4f", visitCount, similarity);
					
					System.out.format(" | Progress = %.5f",getProgress());
					
					if(similarity < THRESHOLD)
					{
						System.out.println(" | (less than threshold!");
					}
					else
					{
						/*System.out.println();
						String query = String.format("INSERT INTO user_ref_similarity (url, topic_name, similarity, user_id) VALUES ('%s', '%s', %f, %d)",
								currentURL,
								currentTopic,
								similarity,
								user_id);*/
						pstmtSimilarity.setString(1, currentURL);
						pstmtSimilarity.setString(2, currentTopic);
						pstmtSimilarity.setFloat(3, (float)similarity);
						pstmtSimilarity.setInt(4, user_id);
						
						try {
							pstmtSimilarity.executeUpdate();
							System.out.println(" | Inserted!");
						}
						catch (SQLIntegrityConstraintViolationException sicve) {

							System.out.println(" | "+sicve+" Duplicate url-topic combo? url = "+
									currentURL+", topic = "+currentTopic);
						}

					}

				}
				else {
					System.out.println(" | Denominator = 0!");
				}
				
				currentRowNumber++;
			}
			
			// Close all statements
			pstmtSimilarity.close();
			pstmtVisitCount.close();
			pstmtPopUserList.close();
			pstmtPopRefList.close();
			stmt.close();	
			
			// Close connection
			db_conn.close();
			
			long stopTime = System.currentTimeMillis();
			System.out.format("Finished computing similarity for user %d in %s.\n",
					user_id, TimeUtils.formatHmss(stopTime-startTime));
			status = "Finished!";
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * Loads all terms and weights of a chosen concept/topic
	 * of reference ontology
	 * into the Lists referenceTerms and referenceWeights
	 * respectively.
	 * 
	 * Requires an initialized PreparedStatement pstmtPopRefList.
	 * 
	 * @param topicName That concept of reference ontology
	 */
	private static void populateReferenceList(String topicName)
	{
		try
		{
			/*ResultSet rs1 = dbo.executeQuery("SELECT term, weight FROM ref_tf "
					+ "WHERE topic_name LIKE '" + topicName+"' AND weight != 0");*/
			
			pstmtPopRefList.setString(1, topicName);
			ResultSet rs1 = pstmtPopRefList.executeQuery();

			referenceTerms.clear();
			referenceWeights.clear();
			while(rs1.next())
			{
				String currentTerm = rs1.getString("term");
				double currentWeight = rs1.getDouble("weight");
				referenceTerms.add(currentTerm);
				referenceWeights.add(currentWeight);
			}
			rs1.close();

		} 
		catch (SQLException e) {

			e.printStackTrace();
		}
	}
	
	/**
	 * Loads all terms and weights of a chosen URL/web page
	 * visited by the user_id currently being processed,
	 * into the Lists userTerms and userWeights
	 * respectively.
	 * 
	 * Requires an initialized PreparedStatement pstmtPopUserList.
	 * 
	 * @param url That chosen URL/web page visited
	 */
	private static void populateUserList(String url)
	{
		try
		{
			/*ResultSet rs2 = dbo.executeQuery("SELECT term, weight FROM user_tf "
					+ "WHERE url LIKE '" + url +"'  AND weight != 0");*/
			
			pstmtPopUserList.setString(1, url);
			ResultSet rs2 = pstmtPopUserList.executeQuery();

			userTerms.clear();
			userWeights.clear();
			while(rs2.next())
			{
				String currentTerm = rs2.getString("term");
				double currentWeight = rs2.getDouble("weight");
				userTerms.add(currentTerm);
				userWeights.add(currentWeight);
			}
			rs2.close();
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Factor visit count into similarity calculation
	 * Use a function to transform the base of the visit count calculation
	 * Current function f(n) = n*(1+ln(n))
	 * 
	 * @param visitCount
	 * @return
	 */
	private static double transformVisitCount(double visitCount)
	{
		return visitCount * (1 + Math.log(visitCount)/Math.log(BASE));
	}

	
}
