package deimos.phase2.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import deimos.phase2.DBOperations;

public class UserIDF {
	
	private static int totalURLs, URLsWithTerm;
	private static double idf;
	
	/** Create Statements and preparedStatements on this connection. */
	private static Connection db_conn;
	
	private static int noOfDistinctTerms = 1;
	private static int currentTermNo = 0;
	private static String status;
	
	/** Testing only */
	public static void main(String[] args)
	{
		// remove hardcode
		computeUserIDF(1);
	}
	
	public static double getProgress()
	{
		return currentTermNo*1.0/noOfDistinctTerms;
	}
	
	public static String getStatus() {
		return status;
	}
	
	/**
	 * Computes IDF of all terms inside pages visited by a user,
	 * and stores the values in user_idf.
	 * 
	 * @param user_id
	 */
	public static void computeUserIDF(int user_id)
	{
		try
		{
			// Open connection
			db_conn = DBOperations.getConnectionToDatabase("UserIDF");

			DBOperations.truncateUserTable(db_conn, "user_idf", user_id);

			Statement stmt = db_conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT DISTINCT term FROM user_tf WHERE user_id = "+user_id);
			List<String> distinctTerms = new ArrayList<String>();
			while(rs.next())
			{
		        String currentTerm = rs.getString("term");
		        distinctTerms.add(currentTerm);
			}
			rs.close();
			System.out.println("\nNo. of distinct terms: "+distinctTerms.size());
			noOfDistinctTerms = distinctTerms.size();
			
			System.out.println("Computing IDF...");
			
			// Get total URL count
			rs = stmt.executeQuery("SELECT COUNT(url) AS url_total FROM user_urls WHERE user_id = " + user_id);
			if(rs.next()) {
				totalURLs = rs.getInt("url_total");
			}
			rs.close();
			stmt.close();
			
			// Prepare statement
			PreparedStatement pstmt1 = db_conn.prepareStatement(
					"SELECT COUNT(DISTINCT url) AS urls_with_term "
					+ "FROM user_tf "
					+ "WHERE term LIKE ? "
					+ "AND user_id = ?");
			pstmt1.setInt(2, user_id);
			
			PreparedStatement pstmt2 = db_conn.prepareStatement(
					"INSERT INTO user_idf (user_id, term, idf) "
					+ "VALUES (?, ?, ?)");
			status = "Starting...";
			
			for(int i = 0; i < distinctTerms.size(); i++) {
				
				String termName = distinctTerms.get(i);
				
				pstmt1.setString(1, termName);
				// User ID already set
				ResultSet rs2 = pstmt1.executeQuery();
				if(rs2.next()) {
					URLsWithTerm = rs2.getInt("urls_with_term");
				}
				rs2.close();
				
				idf = Math.log(totalURLs*1.0/URLsWithTerm);
				String str = String.format("%6d | %s (%d/%d) IDF = %.6f", i, termName, URLsWithTerm, totalURLs, idf);
				System.out.println(str);
				currentTermNo = i;
				status = String.format("(%d/%d)", i, noOfDistinctTerms);
				
				try {
					
					// query = "INSERT INTO user_idf (user_id, term, idf) VALUES ("+user_id+", '"+ termName+"', "+idf +")";
					pstmt2.setInt(1, user_id);
					pstmt2.setString(2, termName);
					pstmt2.setFloat(3, (float)idf);
					pstmt2.executeUpdate();
					
				} catch (SQLSyntaxErrorException e) {
					System.err.println(e);
				} catch (SQLException sqle) {
					sqle.printStackTrace();
				}
				
			}
			
			pstmt1.close();
			pstmt2.close();
			db_conn.close();
			
			System.out.println("\nidf calculation for user_idf complete!");
			status = "Finished!";
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
