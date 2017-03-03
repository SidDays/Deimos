package deimos.phase2.dmoz;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import deimos.phase2.DBOperations;

public class IDF
{
	public static void main(String[] args)
	{
		try {
			DBOperations dbo = new DBOperations();
			dbo.truncateTable("IDF");
			
			double idf;
			int totalTopics = 0;
			// int index = 1;
			int topicsWithTerm = 0;
			// String termName;
			String query;
			ResultSet rs;
			
			rs = dbo.executeQuery("SELECT DISTINCT term FROM tf_weight");
			
			List<String> terms = new ArrayList<String>();
			while(rs.next())
			{
		        String currentTerm = rs.getString("term");
		        terms.add(currentTerm);
			}
			
			// System.out.println("No. of distinct terms: "+terms.size());
			int idfComputeCount = 0;
			/*for(int i = 0; i < terms.size(); i++) {
				System.out.println(terms.get(i));
			}*/
			
			long startTime = System.currentTimeMillis();
			
			System.out.println("Computing IDF...");
			for(String termName : terms)
			{
				// System.out.println("Term-name: "+termName);
				
				ResultSet rs2 = dbo.executeQuery(
						"SELECT COUNT(DISTINCT topic_name) AS tf_total FROM tf_weight WHERE term LIKE '"+termName+"'");
				
				while(rs2.next()) {
					topicsWithTerm = rs2.getInt("tf_total");
				}
				
				// System.out.println("Total topics with term: "+topicsWithTerm);
				
				ResultSet rs1=dbo.executeQuery(
						"SELECT COUNT(DISTINCT topic_name) AS total FROM topics");
				while(rs1.next()) {
					totalTopics = rs1.getInt("total");
				}
				
				// System.out.println("Total topics: "+totalTopics);
				
				idf = Math.log((double)totalTopics/topicsWithTerm);
				query = "INSERT INTO idf (term, idf) VALUES ('"+termName+"', '"+ idf +"')";
				
				dbo.executeUpdate(query);
				idfComputeCount++;
			}
			
			long stopTime = System.currentTimeMillis();
			System.out.format("IDF Calculation completed for %d terms in %.3fs.\n",
					idfComputeCount, (stopTime-startTime)/1000f);
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
