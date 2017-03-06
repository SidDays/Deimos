package deimos.phase2.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import deimos.phase2.DBOperations;

public class SimilarityMapper {
	
	private static List<String> referenceTerms = new ArrayList<>();
	
	private static List<String> userTerms = new ArrayList<>();
	
	private static List<String> unionOfTerms;
	
	private static List<Double> referenceTermsWeights = new ArrayList<>();
	
	private static List<Double> userTermsWeights = new ArrayList<>();
	
	private static String topicName, currentTerm, url;
	private static final double threshold = Float.MIN_VALUE;
	private static double currentWeight;
	
	private static ResultSet rs, rs1;
	private static DBOperations dbo;
	
	public static void main(String[] args) {
		try {
			dbo = new DBOperations();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** This function inserts all terms of one topic into the list */
	private static void populateReferenceList() {
		try {
			
			rs = dbo.executeQuery("SELECT DISTINCT topic_name FROM tf_weight");
			
			while(rs.next())
			{
				topicName = rs.getString("topic_name");
				
				rs1 = dbo.executeQuery("SELECT DISTINCT term FROM tf_weight WHERE topic_name LIKE " + topicName);
				while(rs1.next())
				{
			        currentTerm = rs.getString("term");
			        referenceTerms.add(currentTerm);
				}
			}
			
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void populateUserList() {
		try {
			rs = dbo.executeQuery("SELECT DISTINCT url FROM tf_users");
			
			while(rs.next()) {
				url = rs.getString("url");
				
				rs1 = dbo.executeQuery("SELECT DISTINCT term FROM tf_user WHERE url LIKE " + url);
				while(rs1.next())
				{
			        currentTerm = rs.getString("term");
			        referenceTerms.add(currentTerm);
				}
			}
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void unionOfLists() {
		unionOfTerms = new ArrayList<String>(referenceTerms);
		unionOfTerms.addAll(userTerms);
	}
	
	private static void insertUserWeights() {
		try {
			rs = dbo.executeQuery("SELECT DISTINCT url FROM tf_users");
			
			while(rs.next()) {
				url = rs.getString("url");
				
				rs1 = dbo.executeQuery("SELECT weight FROM tf_user WHERE url LIKE " + url);
				while(rs1.next())
				{
			        currentWeight = rs.getDouble("weight");
			        userTermsWeights.add(currentWeight);
				}
			}
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void insertReferenceWeights() {
		try {
			rs = dbo.executeQuery("SELECT DISTINCT topic_name FROM tf_weights");
			
			while(rs.next())
			{
				topicName = rs.getString("topic_name");
				
				rs1 = dbo.executeQuery("SELECT weight FROM tf_weight WHERE topic_name LIKE " + topicName);
				while(rs1.next())
				{
					currentWeight = rs.getDouble("weight");
			        referenceTermsWeights.add(currentWeight);
				}
			}
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void computeSimilarity() {
		for(String termName: unionOfTerms) {
			if(userTerms.contains(termName)) {
				insertUserWeights();
			}
			else {
				userTermsWeights.add(0.0);
			}
		}
		
		for(String termName: unionOfTerms) {
			if(referenceTerms.contains(termName)) {
				insertUserWeights();
			}
			else {
				referenceTermsWeights.add(0.0);
			}
		}
	}
}
