package deimos.phase2.refine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import deimos.common.DeimosConfig;
import deimos.phase2.DBOperations;

public class GEW {

	public static int counte(String topicName)
	{
		int i=0, j=0;
		while(i!=-1)
		{
			i=topicName.indexOf('/', i);
			j++;
			i++;
		}
		return j;
	}
	public static void main(String[] args)
	{
		try {
			DBOperations dbi = new DBOperations();	
			DBOperations dbo = new DBOperations();
			
			ResultSet rs0 = dbo.executeQuery("SELECT DISTINCT topic_name FROM ");	//the table which contains the similarity measure
			ResultSet rs = dbo.executeQuery("SELECT DISTINCT child_name FROM topics_children");
			int totalTopics = 0, index = 1, count, i;
			int topicsWithTerm = 0, j;
			String topicName, query;
			List<String> term = new ArrayList<String>();
			ResultSetMetaData metadata = rs.getMetaData();
			int numberOfColumns = metadata.getColumnCount();
			for(i=5; i>0; i--)
			{
				rs.first();
				while(rs.next()) 
				{
					topicName = rs.getString("topic_name");
					System.out.println("Topic-name: "+topicName);
					count= counte(topicName);
					if(count==i)
					{
						ResultSet rs2 = dbo.executeQuery("SELECT DISTINCT child_name FROM topics_children WHERE topic_name = '"+topicName+"'");
						while(rs2.next()) {
							
						topicsWithTerm = rs2.getInt("tf_total");
						}
						System.out.println("Total topics with term: "+topicsWithTerm);
						ResultSet rs1=dbo.executeQuery("SELECT DISTICT COUNT(*) AS total FROM topics");
						while(rs1.next()) {
							totalTopics = rs1.getInt("total");
						}
						System.out.println("Total topics: "+totalTopics);
					
						//idf = java.lang.Math.log(totalTopics/topicsWithTerm);
						//query = "INSERT INTO idf (term, idf) VALUES ('"+topicName+"', '"+ idf +"')";
					
						//dbo.executeUpdate(query);
					}
				}
			}
			dbo.con.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}


