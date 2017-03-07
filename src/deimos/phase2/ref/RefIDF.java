package deimos.phase2.ref;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import deimos.common.TimeUtils;
import deimos.phase2.DBOperations;

/**
 * Inverse Document Frequency (IDF) operations.
 * 
 * @author Bhushan Pathak
 * @author Siddhesh Karekar
 */
public class RefIDF
{
	double idf;
	
	int idfComputeCount;
	
	/** Used to resume IDF calculation from
	 * a certain index in the list of distinct terms from ref_tf.
	 * 0 Makes it start over
	 * -1 Makes it start over and also truncate existing IDF*/
	int resumeIndex;
	
	int totalTopics;
	int topicsWithTerm;
	String query;
	ResultSet rs;
	DBOperations dbo;
	long startTime, stopTime;
	
	public RefIDF() {
		startTime = System.currentTimeMillis();
		idfComputeCount = 0;
		resumeIndex = 0;
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
		double rate = (idfComputeCount-resumeIndex)/(elapsed/60000.0);
		return String.format("Rate: %.3f terms/m", rate);
	}
	
	/**
	 * Selects distinctly all the terms in ref_tf, and prepares a List.
	 * Computes IDF for all the terms in that list, and inserts them into ref_idf table.
	 * You can specify a resumeIndex to not have to do the entire process at one go.
	 * <br>
	 * <br>
	 * In any case, the term is a primary key of ref_idf, so duplicate entries will not be inserted.
	 * Thus, if the ref_tf table is updated, you MUST specify the resumeIndex as -1 to
	 * truncate the table beforehand!
	 * 
	 * @param resumeIndex If resumeIndex == -1, truncates the tables and starts afresh;
	 * If resumeIndex == 0, no change;
	 * If resumeIndex > 0, it will start only from the index mentioned.
	 */
	void computeIDF(int resumeIndexParam) // Resume with CARE!
	{
		this.resumeIndex = resumeIndexParam;
		try {
			dbo = new DBOperations();
			
			rs = dbo.executeQuery("SELECT DISTINCT term FROM ref_tf");
			
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
			
			// Resume facility
			if(resumeIndex > 0) {
				System.out.format("The processing will resume from term %d of %d (%.2f%s complete).\n",
						resumeIndex, terms.size(), (resumeIndex*100.0f/terms.size()), "%");
				System.out.println("ref_idf table was not truncated. "
						+ "Make sure ref_tf was not changed beforehand!");
			}
			else if(resumeIndex == -1) {
				dbo.truncateTable("IDF");
				resumeIndex++;
			}
			System.out.println();
			
			for(idfComputeCount = resumeIndex; idfComputeCount < terms.size(); idfComputeCount++)
			{
				String termName = terms.get(idfComputeCount);
				// System.out.println("Term-name: "+termName);
				
				ResultSet rs2 = dbo.executeQuery(
						"SELECT COUNT(DISTINCT topic_name) AS tf_total FROM ref_tf WHERE term LIKE '"+termName+"'");
				
				while(rs2.next()) {
					topicsWithTerm = rs2.getInt("tf_total");
				}
				
				// System.out.println("Total topics with term: "+topicsWithTerm);
				
				ResultSet rs1=dbo.executeQuery(
						"SELECT COUNT(DISTINCT topic_name) AS total FROM ref_topics");
				while(rs1.next()) {
					totalTopics = rs1.getInt("total");
				}
				
				// System.out.println("Total topics: "+totalTopics);
				
				idf = Math.log((double)totalTopics/topicsWithTerm);
				query = "INSERT INTO ref_idf (term, idf) VALUES ('"+termName+"', '"+ idf +"')";
				
				try {
					dbo.executeUpdate(query);
					System.out.format("%6d - %s (%d/%d) IDF = %.3f, %s\n",
							(idfComputeCount+1), termName, topicsWithTerm, totalTopics, idf, getRatePerMinute());
					
				}
				catch (SQLIntegrityConstraintViolationException sqle) {
					System.err.format("%6d - %s (%d/%d) IDF = %.3f (Already in database!)\n",
							(idfComputeCount+1), termName, topicsWithTerm, totalTopics, idf);
				}

			}
			
			System.out.println("idf calculation for Ref_idf complete!");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Selects distinctly all the terms in ref_tf, and prepares a List.
	 * Computes IDF for all the terms in that list, and inserts them into ref_idf table.
	 * <br>
	 * <br>
	 * Same as computeIDF(0) - no truncate or resume.
	 */
	void computeIDF() {
		this.resumeIndex = 0;
		computeIDF(0);
	}
	
	public static void main(String[] args)
	{
		RefIDF idf = new RefIDF();
		idf.computeIDF(0); // Where to resume from
	}
}
