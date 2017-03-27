package deimos.gui.view.services;

import deimos.phase2.user.UserURLsTF;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class URLsTFService extends Service<Void> {

	private int userId = -1;
	private boolean truncate = false;
	private String filePath;

	public void setUserId(int id)
	{
		this.userId = id;
	}
	
	public void setTruncate(boolean truncate)
	{
		this.truncate = truncate;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	protected Task<Void> createTask() {

		return new Task<Void>() {
			@Override
			public Void call(){

				if(userId == -1)
					System.err.println("Warning: userId = -1.");

				UserURLsTF.userURLAndTFTableInsertion(userId, truncate, filePath);
				return null;
			}
		};
	}

}