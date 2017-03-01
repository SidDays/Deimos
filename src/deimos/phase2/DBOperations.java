package deimos.phase2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import deimos.common.DeimosConfig;

/**
 * Until a better name comes up, this class will be used for performing
 * all the operations on our Oracle 11g XE database.
 * 
 * Reference:
 * www.javatpoint.com/example-to-connect-to-the-oracle-database
 * docs.oracle.com/javase/tutorial/jdbc/overview/index.html
 * 
 * @author Siddhesh Karekar
 */
public class DBOperations
{
	Connection con;
	
	/**
	 * Estabilishes a connection with the Oracle 11g XE database.
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean connectToDatabase(String username, String password)
	{
		try
		{
			con = DriverManager.getConnection(
			        "jdbc:oracle:thin:@localhost:1521:xe",
			        username,
			        password);
			System.out.println("Connection to database established succesfully.");
			return true;
		}
		catch (SQLException e)
		{
			System.err.println("Failed to connect to database.");
			// e.printStackTrace();
		}
		return false;
	}
	
	// TODO
	public void queryDatabase(String query) throws SQLException
	{

	    Statement stmt = con.createStatement();
	    ResultSet rs =
	    		stmt.executeQuery(query);

	    /*while (rs.next()) {
	        int x = rs.getInt("a");
	        String s = rs.getString("b");
	        float f = rs.getFloat("c");
	    }*/
	}
	
	public static void main(String[] args)
	{
		DBOperations dbo = new DBOperations();
		
		try {
			dbo.connectToDatabase(DeimosConfig.DB_USER, DeimosConfig.DB_PASSWORD);
		}
		catch(Exception e) { e.printStackTrace(); }
	}
}
