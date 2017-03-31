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
 * www.tutorialspoint.com/jdbc/preparestatement-object-example.htm
 * 
 * @author Siddhesh Karekar
 */
public class DBOperations
{

	/**
	 * Creates a new connection with the DB while making 
	 * sure one is not already open, by passing that as a reference.
	 * @param db_conn
	 * @param caller Name of the calling class for convenience (optional)
	 * @return The connection
	 * @throws SsQLException
	 */
	public static void connectToDatabaseIfNot(Connection db_conn, String ... caller) throws SQLException
	{
		if(db_conn == null || db_conn.isClosed()) {
			db_conn = DBOperations.getConnectionToDatabase(caller);
		}
	}
	
	/**
	 * Closes a connection object if it is not null and is open.
	 * @param db_conn
	 * @throws SQLException
	 */
	public static void closeConnectionToDBIfNot(Connection db_conn) throws SQLException
	{
		if(db_conn != null)
		{
			if(!db_conn.isClosed()) {
				db_conn.close();
				System.out.println("Database connection closed.");
			}
		}
	}

	/**
	 * Creates a new connection with the DB to create
	 * statements and preparedStatements upon.
	 * @param caller Name of the calling class for convenience (optional)
	 * @return The connection
	 * @throws SQLException
	 */
	public static Connection getConnectionToDatabase(String ... caller) throws SQLException
	{
		Connection conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@localhost:1521:xe",
				DeimosConfig.DB_USER,
				DeimosConfig.DB_PASSWORD);

		if(caller.length > 0)
			System.out.println(caller[0]+"'s connection to database established successfully.");
		else
			System.out.println("Connection to database established successfully.");

		return conn;
	}	

	/** Truncate the specified table.
	 * Use with extreme caution! */
	public static void truncateTable(Connection conn, String tableName)
	{

		try {
			String query = String.format("TRUNCATE TABLE %s", tableName);
			// System.out.println(query);
			Statement stmt = conn.createStatement();

			stmt.executeUpdate(query);
			System.out.println("Truncated the table "+ tableName +".");

			stmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** Deletes rows from the specified table for given user Id
	 * Use with extreme caution! */
	public static void truncateUserTable(Connection conn, String tableName, int user_id)
	{
		try {
			String query = String.format("DELETE FROM %s WHERE user_id = %d", tableName, user_id);
			// System.out.println(query);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
			System.out.println("Deleted all rows in "+ tableName +" for user "+user_id+".");
			stmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** Truncate all tables used to build the reference ontology.
	 * Use with extreme caution!
	 */
	public static void truncateAllReferenceTables(Connection conn)
	{
		truncateTable(conn, "ref_topics");
		truncateTable(conn, "ref_hierarchy");
		truncateTable(conn, "ref_tf");
		truncateTable(conn, "ref_idf");
	}

	/** Truncate all tables used in user data processing.
	 * Use with extreme caution!
	 */
	public static void truncateAllUserTables(Connection conn, int user_id)
	{
		truncateUserTable(conn, "user_urls", user_id);
		truncateUserTable(conn, "user_tf", user_id);
		truncateUserTable(conn, "user_ref_similarity", user_id);
	}


	// TODO deprecate the entire old implementation
	// OLD OLD OLD
	///////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////////
	///////////////////////////////////////////////

	@Deprecated
	public Connection con;

	@Deprecated
	private Statement stmt1;

	@Deprecated
	private Statement stmt2;

	/**
	 * Close the connection before exiting.
	 */
	@Override
	protected void finalize()
	{
		// Close the connection.
		try {
			if(stmt1 != null)
				stmt1.close();

			if(stmt2 != null)
				stmt2.close();

			if(con != null)
				con.close();
			System.out.println("Connection to database closed successfully");
		}
		catch (SQLException e)
		{
			System.err.println("Failed to close connection to database.");
		}
	}

	/** By default, connect using configured username/pw */
	@Deprecated
	public DBOperations() throws SQLException {
		connectToDatabase(DeimosConfig.DB_USER, DeimosConfig.DB_PASSWORD);
	}

	/**
	 * Estabilishes a connection with the Oracle 11g XE database.
	 * @param username
	 * @param password
	 * @return
	 */
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
	public int executeUpdate(String query) throws SQLException
	{
		return stmt1.executeUpdate(query);
	}


	/** Truncate the specified table.
	 * Use with extreme caution! */
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
	public void truncateAllUserTables(int user_id)
	{
		this.truncateUserTable("user_urls", user_id);
		this.truncateUserTable("user_tf", user_id);
		this.truncateUserTable("user_ref_similarity", user_id);
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
