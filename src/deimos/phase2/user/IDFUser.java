package deimos.phase2.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import deimos.phase2.DBOperations;

public class IDFUser {
	
	static int totalURLs, URLsWithTerm;
	static double idf;
	static String query;
	static ResultSet rs;
	static DBOperations dbo;
	static long startTime, stopTime;
	
	public static void main(String[] args) {
		// TODO remove hardcode
		computeUserIDF(1);
	}
	
	static void computeUserIDF(int user_id) {
		try {
			dbo = new DBOperations();
			dbo.truncateTable("idf_users");
			
			rs = dbo.executeQuery("SELECT DISTINCT term FROM tf_users");
			
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
						"SELECT COUNT(DISTINCT url) AS urls_with_term FROM tf_users WHERE term LIKE '"+termName+"'" +
				"AND user_id = " + user_id);
				
				while(rs2.next()) {
					URLsWithTerm = rs2.getInt("urls_with_term");
				}
				
				ResultSet rs1=dbo.executeQuery(
						"SELECT COUNT(url) AS url_total FROM users WHERE user_id = " + user_id);
				
				while(rs1.next()) {
					totalURLs = rs1.getInt("url_total");
				}
				
				System.out.format("Term = %s, URLsWithTerm = %d, totalURLs = %d\n", termName, URLsWithTerm, totalURLs);
				
				idf = Math.log(totalURLs*1.0/URLsWithTerm);
				query = "INSERT INTO idf_users (user_id, term, idf) VALUES ("+user_id+", '"+ termName+"', "+idf +")";
				
				// System.out.println(query);
				dbo.executeUpdate(query);
				
			}
			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
