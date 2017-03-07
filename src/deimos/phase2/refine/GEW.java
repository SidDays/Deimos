package deimos.phase2.refine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import deimos.phase2.DBOperations;

// TODO

/**
 * References:
 * http://www.javatpoint.com/ResultSet-interface
 * 
 * @author Amogh Bhabal
 *
 */
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
			DBOperations dbo = new DBOperations();
			ResultSet rs = dbo.executeQuery("SELECT * FROM ref_hierarchy");	//the table which contains the similarity measure
			ResultSet rs3;
			int count, i, k, j;	
			double tempval, tempval1;
			double alpha=0.3, EP;			//alpha is the specified default value. Not sure to set it as 0.5 or (1/2) because of the decimal point limit
			String topicName, query, childtopic;
			//List<String> topic = new ArrayList<String>();
			List<String> urls = new ArrayList<String>();
			List<Double> child = new ArrayList<>();
			List<Double> parent = new ArrayList<>();
			for(i=3; i>0; i--)			//The level limit. Used to check if the level of parent topic is equal or not
			{
				EP=(alpha*i)/3;
				rs.first();				//setting the cursor back to the first row
				while(rs.next()) 
				{
					topicName = rs.getString("topic_name");
					System.out.println("Topic-name: "+topicName);
					count= counte(topicName);
					if(count==i)
					{
						ResultSet rs2 = dbo.executeQuery("SELECT child_name FROM ref_hierarchy WHERE topic_name = '"+topicName+"'");
						while(rs2.next()) 
						{
							childtopic=rs2.getString("child_name");
							rs3=dbo.executeQuery("SELECT * FROM similarity WHERE topic_name = '"+childtopic+"'");
							while(rs3.next())
							{
								child.add(rs3.getDouble("similarity"));
							}
							ResultSet rs4 =dbo.executeQuery("SELECT * FROM similarity WHERE topic_name = '"+topicName+"'");
							while(rs4.next())
							{
								parent.add(rs4.getDouble("similarity"));
								urls.add(rs4.getString("url"));
							}
							for(j=0; j<child.size(); j++)
							{
								tempval=child.get(j);
								tempval=tempval*EP;
								child.set(j, tempval);
							}
							for(j=0; j<parent.size(); j++)
							{
								tempval1=parent.get(j);
								for(k=0; k<child.size(); k++)
								{
									tempval=child.get(k);
									tempval1=tempval1+tempval;
									
								}
								parent.set(j, tempval1);
								
								query = String.format("UPDATE similarity SET similarity = %f WHERE topic_name = '%s' AND url = '%s'",tempval1, topicName, urls.get(j));
								dbo.executeUpdate(query);
							}
							
							//topicsWithTerm = rs2.getInt("tf_total");
						}
						//System.out.println("Total topics with term: "+topicsWithTerm);
						//ResultSet rs1=dbo.executeQuery("SELECT DISTICT COUNT(*) AS total FROM ref_topics");
						//while(rs1.next()) {
						//	totalTopics = rs1.getInt("total");
						//}
						
						//System.out.println("Total topics: "+totalTopics);
					
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


