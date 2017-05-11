package deimos.gui.view.services;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;

import org.sqlite.SQLiteException;

import deimos.phase2.DBOperations;
import deimos.phase2.user.UserTrainingInput;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * @author Siddhesh Karekar
 */
public class ExportUserService extends Service<Void> {
	
	private int userId = -1;
	
	public void setUserId(int id)
	{
		this.userId = id;
	}

	@Override
	protected Task<Void> createTask() {
		
		return new Task<Void>() {
            @Override
            public Void call() throws SQLiteException
            {
            	try
            	{
					Connection db_conn = DBOperations.getConnectionToDatabase("UserTrainingInput");
					UserTrainingInput.exportTrainingValues(db_conn, userId);
					db_conn.close();
					
				} catch (FileNotFoundException | SQLException e) {
					e.printStackTrace();
				}
            	
               	return null;
            }
        };
	}
}