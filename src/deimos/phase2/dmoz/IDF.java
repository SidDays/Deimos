package deimos.phase2.dmoz;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import deimos.phase2.DBOperations;

public class IDF {
	public static void main(String[] args) {
		try {
			DBOperations dbo = new DBOperations();
			ResultSet rs = dbo.executeQuery("SELECT DISTINCT term FROM tf_weight");
			
			double idf;
			int totalTopics = 0, index = 1;
			int topicsWithTerm = 0;
			String termName, query;
			List<String> term = new ArrayList<String>();
			ResultSetMetaData metadata = rs.getMetaData();
			int numberOfColumns = metadata.getColumnCount();
			while(rs.next()) {
				int i = 1;
		        while(i <= numberOfColumns) {
		            term.add(rs.getString(i++));
		        }
			}
			System.out.println("Size: "+term.size());
			/*for(int i = 0; i < term.size(); i++) {
				System.out.println(term.get(i));
			}*/
			while(rs.next()) {
				termName = rs.getString("term");
				System.out.println("Term-name: "+termName);
				ResultSet rs2 = dbo.executeQuery("SELECT DISTINCT COUNT(*) AS tf_total FROM tf_weight WHERE term = '"+termName+"'");
				while(rs2.next()) {
					topicsWithTerm = rs2.getInt("tf_total");
				}
				System.out.println("Total topics with term: "+topicsWithTerm);
				ResultSet rs1=dbo.executeQuery("SELECT DISTICT COUNT(*) AS total FROM topics");
				while(rs1.next()) {
					totalTopics = rs1.getInt("total");
				}
				System.out.println("Total topics: "+totalTopics);
				
				idf = java.lang.Math.log(totalTopics/topicsWithTerm);
				query = "INSERT INTO idf (term, idf) VALUES ('"+termName+"', '"+ idf +"')";
				
				dbo.executeUpdate(query);
			}
			dbo.con.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
