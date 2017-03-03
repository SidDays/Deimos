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
	public Connection con;
	private Statement stmt;
	
	/** By default, connect using configured username/pw */
	public DBOperations() throws SQLException {
		connectToDatabase(DeimosConfig.DB_USER, DeimosConfig.DB_PASSWORD);
	}
	
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
			
			
			
			System.out.println(username +
					"'s connection to database established successfully.");
			
			try {
				stmt = con.createStatement();
			} catch (SQLException sqle) {
				
				System.err.println("Failed to create statement.");
				sqle.printStackTrace();
			}
			
			return true;
		}
		catch (SQLException e)
		{
			System.err.println(username+" failed to connect to database.");
			// e.printStackTrace();
		}
		return false;
	}
	
	// TODO
	/**
	 * For select queries.
	 * @param query
	 * @throws SQLException
	 */
	public ResultSet executeQuery(String query) throws SQLException
	{
	    ResultSet rs =
	    		stmt.executeQuery(query);

	    // Print the output
	    /*while (rs.next()) {
	        int x = rs.getInt("a");
	        String s = rs.getString("b");
	        float f = rs.getFloat("c");
	    }*/
	    
	    return rs;
	}
	
	/**
	 * For insert, update or delete queries.
	 * @param query
	 * @return
	 * @throws SQLException
	 */
	public int executeUpdate(String query) throws SQLException
	{
		return stmt.executeUpdate(query);
	}
	
	public void clearAllTables()
	{
		String queries[] = {
				"TRUNCATE TABLE topics"
				, "TRUNCATE TABLE topics_children"
				, "TRUNCATE TABLE tf_weight"
				, "TRUNCATE TABLE idf"
		};
		try {
			for(String query : queries)
			{
				stmt.executeUpdate(query);
				System.out.println("Truncated the table "+
				query.substring(query.indexOf("TABLE ")+6)+".");
			}
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}

	}
	
	public static void main(String[] args)
	{
		try {
			DBOperations dbo = new DBOperations();
			
			dbo.clearAllTables();

			// Close the connection.
			try {
				dbo.con.close();	
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}


	}
}
