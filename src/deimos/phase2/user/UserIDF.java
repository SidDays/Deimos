package deimos.phase2.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;

import deimos.phase2.DBOperations;

public class UserIDF {
	
	private static int totalURLs, URLsWithTerm;
	private static double idf;
	private static String query;
	private static ResultSet rs;
	private static DBOperations dbo;
	
	static {
		
	}

	
	public static void computeUserIDF(int user_id)
	{
		try {
			dbo = new DBOperations();
			dbo.truncateTable("user_idf");
			
			rs = dbo.executeQuery("SELECT DISTINCT term FROM user_tf WHERE user_id = "+user_id);
			
			List<String> distinctTerms = new ArrayList<String>();
			while(rs.next())
			{
		        String currentTerm = rs.getString("term");
		        distinctTerms.add(currentTerm);
			}
			System.out.println("\nNo. of distinct terms: "+distinctTerms.size());
			
			System.out.println("Computing IDF...");
			for(String termName : distinctTerms) {
				
				ResultSet rs2 = dbo.executeQuery(
						"SELECT COUNT(DISTINCT url) AS urls_with_term FROM user_tf WHERE term LIKE '"+termName+"'" +
				"AND user_id = " + user_id);
				
				while(rs2.next()) {
					URLsWithTerm = rs2.getInt("urls_with_term");
				}
				
				ResultSet rs1=dbo.executeQuery(
						"SELECT COUNT(url) AS url_total FROM user_urls WHERE user_id = " + user_id);
				
				while(rs1.next()) {
					totalURLs = rs1.getInt("url_total");
				}
				
				String str = String.format("Term = %s, URLsWithTerm = %d, totalURLs = %d", termName, URLsWithTerm, totalURLs);
				System.out.println(str);
				
				idf = Math.log(totalURLs*1.0/URLsWithTerm);
				query = "INSERT INTO user_idf (user_id, term, idf) VALUES ("+user_id+", '"+ termName+"', "+idf +")";
				
				try { // TODO replace this by preparedStatement
					// System.out.println(query); 
					dbo.executeUpdate(query);
					
				} catch (SQLSyntaxErrorException e) {
					System.err.println(e);
				} catch (SQLException sqle) {
					sqle.printStackTrace();
				}
				
			}
			
			System.out.println("\nidf calculation for user_idf complete!");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		// TODO remove hardcode
		computeUserIDF(1);
	}
}
