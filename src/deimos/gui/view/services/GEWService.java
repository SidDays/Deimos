package deimos.gui.view.services;

import deimos.phase2.similarity.GradualExtraWeight;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class GEWService extends Service<Void>{
	private int userId = -1;
	
	public void setUserId(int id)
	{
		this.userId = id;
	}
	
	@Override
	protected Task<Void> createTask() {

		return new Task<Void>() {
			@Override
			public Void call() {
				GradualExtraWeight.executeGEWQuery(userId);
				return null;
			}
		};
	}
}
