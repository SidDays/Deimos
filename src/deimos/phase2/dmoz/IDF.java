package deimos.phase2.dmoz;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import deimos.common.TimeUtils;
import deimos.phase2.DBOperations;

public class IDF
{
	double idf;
	
	int idfComputeCount;
	int totalTopics;
	int topicsWithTerm;
	String query;
	ResultSet rs;
	DBOperations dbo;
	long startTime, stopTime;
	
	public IDF() {
		startTime = System.currentTimeMillis();
		idfComputeCount = 0;
	}
	
	@Override
	protected void finalize() {
		stopTime = System.currentTimeMillis();
		System.out.format("IDF Calculation completed for %d terms in %s.\n",
				idfComputeCount, TimeUtils.formatHmss(stopTime-startTime));
	}
	
	/*private String getETA()
	{
		// TODO SOMETHING IS WRONG!!
		
		stopTime = System.currentTimeMillis();
		long elapsed = stopTime-startTime;
		long totalTimeRequired = (elapsed * totalTopics)/idfComputeCount;
		
		return "ETA: "+TimeUtils.formatHmss(totalTimeRequired - elapsed);
	}*/
	
	private String getRatePerMinute()
	{
		stopTime = System.currentTimeMillis();
		long elapsed = stopTime-startTime;
		double rate = idfComputeCount/(elapsed/60000.0);
		return String.format("Rate: %.3f terms/m", rate);
	}
	
	void computeIDF()
	{
		try {
			dbo = new DBOperations();
			dbo.truncateTable("IDF");
			
			rs = dbo.executeQuery("SELECT DISTINCT term FROM tf_weight");
			
			List<String> terms = new ArrayList<String>();
			while(rs.next())
			{
		        String currentTerm = rs.getString("term");
		        terms.add(currentTerm);
			}
			
			System.out.println("\nNo. of distinct terms: "+terms.size());
			
			
			int disp = 10;
			int ubound = Math.min(10, terms.size());
			System.out.print("First "+disp+" terms: ");
			for(int i = 0; i < ubound; i++) {
				System.out.print(terms.get(i));
				if(i == ubound-1)
					System.out.println(".");
				else
					System.out.print(", ");
			}
			
			
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
				System.out.format("%6d - %s (%d/%d) IDF = %.3f, %s\n",
						idfComputeCount, termName, topicsWithTerm, totalTopics, idf, getRatePerMinute());
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		IDF idf = new IDF();
		idf.computeIDF();
	}
}
