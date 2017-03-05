package deimos.phase2.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import deimos.phase2.DBOperations;

public class IDFUser {
	
	int totalURLs, URLsWithTerm, user_id = 1;
	double user_idf;
	String query;
	ResultSet rs;
	DBOperations dbo;
	long startTime, stopTime;
	
	public static void main(String[] args) {
		
	}
	
	void computeUserIDF() {
		try {
			dbo = new DBOperations();
			dbo.truncateTable("idf_users");
			
			rs = dbo.executeQuery("SELECT DISTINCT term FROM tf_users");
			
			List<String> distinctTerms = new ArrayList<String>();
			while(rs.next())
			{
		        String currentURL = rs.getString("url");
		        distinctTerms.add(currentURL);
			}
			System.out.println("\nNo. of distinct terms: "+distinctTerms.size());
			
			System.out.println("Computing IDF...");
			for(String termName : distinctTerms) {
				
				ResultSet rs2 = dbo.executeQuery(
						"SELECT COUNT(DISTINCT url) AS url_total FROM tf_users WHERE url LIKE '"+termName+"'");
				
				while(rs2.next()) {
					URLsWithTerm = rs2.getInt("tf_users");
				}
				
				ResultSet rs1=dbo.executeQuery(
						"SELECT COUNT(DISTINCT url) AS total FROM topics");
				
				while(rs1.next()) {
					totalURLs = rs1.getInt("total");
				}
				
				user_idf = Math.log((double)totalURLs/URLsWithTerm);
				query = "INSERT INTO idf (user_id, term, idf) VALUES ('"+user_id+"', '"+ termName+"', '"+user_idf +"')";
				
				dbo.executeUpdate(query);
			}
			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
