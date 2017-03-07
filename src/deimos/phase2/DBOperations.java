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
	private Statement stmt1;
	private Statement stmt2;
	
	/**
	 * Close the connection before exiting.
	 */
	@Override
	protected void finalize()
	{
		// Close the connection.
		try {
			stmt1.close();
			stmt2.close();
			con.close();
			System.out.println("Connection to database closed successfully");
		}
		catch (SQLException e)
		{
			System.err.println("Failed to close connection to database.");
		}
	}
	
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
				stmt1 = con.createStatement();
				stmt2 = con.createStatement();
			} catch (SQLException sqle) {
				
				System.err.println("Failed to create statement(s).");
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
	
	/**
	 * For select queries. Use executeQueryAgain if
	 * you are unable to close this ResultSet.
	 * 
	 * @param query
	 * @throws SQLException
	 */
	public ResultSet executeQuery(String query) throws SQLException
	{
	    ResultSet rs =
	    		stmt1.executeQuery(query);	    
	    return rs;
	}
	
	/**
	 * For select queries, where the first resultSet cannot be closed yet.
	 * @param query
	 * @throws SQLException
	 */
	public ResultSet executeQueryAgain(String query) throws SQLException
	{
	    ResultSet rs =
	    		stmt2.executeQuery(query);
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
		return stmt1.executeUpdate(query);
	}
	
	/**
	 * For more insert, update or delete queries (first use
	 * executeUpdate).
	 * 
	 * @param query
	 * @return
	 * @throws SQLException
	 */
	public int executeUpdateAgain(String query) throws SQLException
	{
		return stmt2.executeUpdate(query);
	}
	
	/** Truncate the specified table.
	 * Use with extreme caution! */
	public void truncateTable(String tableName)
	{
		try {
			String query = String.format("TRUNCATE TABLE %s", tableName);
			// System.out.println(query);
			this.executeUpdate(query);
			System.out.println("Truncated the table "+ tableName +".");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/** Deletes rows from the specified table for given user Id
	 * Use with extreme caution! */
	public void truncateUserTable(String tableName, int user_id)
	{
		try {
			String query = String.format("DELETE FROM %s WHERE user_id = %d", tableName, user_id);
			// System.out.println(query);
			this.executeUpdate(query);
			System.out.println("Deleted all rows in "+ tableName +" for user "+user_id+".");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/** Truncate all tables used to build the reference ontology.
	 * Use with extreme caution!
	 */
	public void truncateAllReferenceTables()
	{
		this.truncateTable("ref_topics");
		this.truncateTable("ref_hierarchy");
		this.truncateTable("ref_tf");
		this.truncateTable("ref_idf");
	}
	
	/** Truncate all tables used in user data processing.
	 * Use with extreme caution!
	 */
	public void truncateAllUserTables(int user_id)
	{
		this.truncateUserTable("user_urls", user_id);
		this.truncateUserTable("user_tf", user_id);
		this.truncateTable("user_ref_similarity");
	}
	
	/** Test purposes only... Remove later! */
	@Deprecated
	public static void main(String[] args)
	{
		try {
			@SuppressWarnings("unused")
			DBOperations dbo = new DBOperations();
			
			// dbo.truncateAllReferenceTables();
		}
		catch(Exception e) {
			e.printStackTrace();
		}


	}
}
