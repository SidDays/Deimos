package deimos.phase2.dmoz;

import java.sql.ResultSet;
import java.sql.SQLException;
import deimos.phase2.DBOperations;

public class WeightCalculation {

	public static void main(String args[])
	{
		String term, query;
		double idf, weight, tf;
		try
		{
			DBOperations dbo = new DBOperations();
			ResultSet rs = dbo.executeQuery("SELECT DISTINCT * FROM idf");
			while(rs.next())
			{
				term=rs.getString("term");
				ResultSet rs1 = dbo.executeQuery("SELCT DISTINCT tf FROM tf.weight where term = '"+term+"'");
				tf=rs1.getInt("tf");
				idf=rs.getInt("idf");
				weight= tf*idf;
				query="INSERT INTO tf.weight VALUES "+weight;
				dbo.executeUpdate(query);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
	}
}
